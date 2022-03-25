package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.cache.LockService;
import com.playtomic.tests.wallet.dto.ChargeRequestDto;
import com.playtomic.tests.wallet.dto.ChargeResponse;
import com.playtomic.tests.wallet.dto.RefundResponse;
import com.playtomic.tests.wallet.exception.WalletException;
import com.playtomic.tests.wallet.model.Payment;
import com.playtomic.tests.wallet.model.Wallet;
import com.playtomic.tests.wallet.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.playtomic.tests.wallet.exception.ErrorMessage.*;

@Slf4j
@Service
public class WalletService {
	private final WalletRepository walletRepository;
	private final StripeService stripeService;
	private final PaymentService paymentService;
	private final LockService lockService;

	public WalletService(WalletRepository walletRepository, StripeService stripeService, PaymentService paymentService, LockService lockService) {
		this.walletRepository = walletRepository;
		this.stripeService = stripeService;
		this.paymentService = paymentService;
		this.lockService = lockService;
	}

	public Wallet save(Wallet wallet) {
		return walletRepository.save(wallet);
	}

	public Optional<Wallet> findByUuid(UUID uuid) {
		return walletRepository.findByUuid(uuid);
	}

	public List<Wallet> findAll() {
		return walletRepository.findAll();
	}

	/**
	 * Perform payment for specified charge details for specified wallet
	 *
	 * @param chargeRequestDto payment details
	 * @param walletUuid       wallet uuid
	 * @throws StripeServiceException if any error occurred during strip api calls
	 * @throws WalletException        if specified wallet can not be found
	 */
	@Transactional
	public void charge(ChargeRequestDto chargeRequestDto, @NotBlank UUID walletUuid) throws StripeServiceException, WalletException {
		Optional<Wallet> opt = walletRepository.findByUuid(walletUuid);

		if (!opt.isPresent()) {
			throw new WalletException(WALLET_NOT_FOUND.getMessage());
		}

		Wallet wallet = opt.get();

		if (wallet.getBalance().compareTo(chargeRequestDto.getAmount()) < 0) {
			log.info("Wallet has not enough balance for amount : " + chargeRequestDto.getAmount());
			throw new WalletException(WALLET_DOES_NOT_HAVE_ENOUGH_BALANCE.getMessage());
		}

		if (lockService.isLock(walletUuid.toString())) {
			log.info("Payment process stopped because there is an active payment process");
			throw new WalletException(ACTIVE_PAYMENT_PROCESS.getMessage());
		}

		log.info("Payment process will be start for Wallet : " + walletUuid);

		// lock before charging start
		lockService.lock(wallet.getUuid().toString());

		try {
			ChargeResponse chargeResponse = stripeService.charge(chargeRequestDto.getCreditCardNumber(), chargeRequestDto.getAmount());

			if (chargeResponse != null && StringUtils.hasText(chargeResponse.getId())
						&& chargeRequestDto.getAmount().equals(chargeResponse.getAmount())) {

				log.info("Payment performed successfully for Wallet : " + walletUuid + " Payment id : " + chargeResponse.getId());

				// save charge payment
				Payment payment = Payment.builder()
										  .refund(false)
										  .amount(chargeResponse.getAmount())
										  .paymentId(chargeResponse.getId())
										  .wallet(wallet)
										  .build();

				paymentService.save(payment);

				// decrease and update wallet balance
				wallet.setBalance(wallet.getBalance().subtract(chargeRequestDto.getAmount()));
				walletRepository.save(wallet);
			} else {
				log.info("An error occurred during payment process, Wallet : " + walletUuid);
				throw new WalletException(PAYMENT_PROCESS_ERROR.getMessage());
			}
		} finally {
			lockService.unlock(wallet.getUuid().toString());
		}
	}

	/**
	 * Perform refund for specified payment
	 *
	 * @param paymentUuid charge payment uuid
	 * @throws StripeServiceException if any error occurred during strip api calls
	 * @throws WalletException        if specified wallet can not be found
	 */
	@Transactional
	public void refund(UUID paymentUuid) throws StripeServiceException, WalletException {
		Optional<Payment> opt = paymentService.findByUuid(paymentUuid);

		if (!opt.isPresent()) {
			throw new WalletException(PAYMENT_NOT_FOUND.getMessage());
		}

		if (lockService.isLock(paymentUuid.toString())) {
			log.info("refund process stopped because there is an active refund process");
			throw new WalletException(ACTIVE_PAYMENT_PROCESS.getMessage());
		}

		log.info("Refund process will be start for Payment : " + paymentUuid);

		Payment chargePayment = opt.get();
		ResponseEntity<RefundResponse> response;
		try {
			response = stripeService.refund(chargePayment.getPaymentId());

			if (response != null && response.getBody() != null && HttpStatus.OK == response.getStatusCode()) {
				RefundResponse refundResponse = response.getBody();
				log.info("Refund performed successfully. Payment id : " + refundResponse.getPaymentId());

				Wallet wallet = chargePayment.getWallet();

				// save refund payment
				Payment refundPayment = Payment.builder()
												.refund(true)
												.amount(refundResponse.getAmount())
												.paymentId(refundResponse.getPaymentId())
												.wallet(wallet)
												.build();

				paymentService.save(refundPayment);

				// increase and update wallet balance
				wallet.setBalance(wallet.getBalance().add(refundPayment.getAmount()));
				walletRepository.save(wallet);
			} else {
				log.info("An error occurred during refund process, Payment : " + paymentUuid);
				throw new WalletException(REFUND_PROCESS_ERROR.getMessage());
			}
		} catch (HttpClientErrorException e) {
			log.error("Payment not found for refund on stripe service");
			throw new WalletException(NOT_REFUNDABLE_PAYMENT.getMessage());
		} finally {
			lockService.unlock(paymentUuid.toString());
		}
	}
}
