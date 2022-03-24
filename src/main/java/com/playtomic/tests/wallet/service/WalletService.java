package com.playtomic.tests.wallet.service;

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

	public WalletService(WalletRepository walletRepository, StripeService stripeService, PaymentService paymentService) {
		this.walletRepository = walletRepository;
		this.stripeService = stripeService;
		this.paymentService = paymentService;
	}

	public void save(Wallet wallet) {
		walletRepository.save(wallet);
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

		log.info("Wallet found and payment process will be start");

		// TODO: 23.03.2022 add lock before charge started

		ChargeResponse chargeResponse = stripeService.charge(chargeRequestDto.getCreditCardNumber(), chargeRequestDto.getAmount());
		// TODO: 23.03.2022 if any exception occurred after redis lock, do not forget to release lock

		if (chargeResponse != null && StringUtils.hasText(chargeResponse.getId())
					&& chargeRequestDto.getAmount().equals(chargeResponse.getAmount())) {

			log.info("Payment performed successfully. Payment id : " + chargeResponse.getId());

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

			// TODO: 24.03.2022 release redis lock
		} else {
			// TODO: 23.03.2022 error scenario
		}
	}

	/**
	 * Perform refund for specified payment
	 *
	 * @param uuid charge payment uuid
	 * @throws StripeServiceException if any error occurred during strip api calls
	 * @throws WalletException        if specified wallet can not be found
	 */
	public void refund(UUID uuid) throws StripeServiceException, WalletException {
		Optional<Payment> opt = paymentService.findByUuid(uuid);

		if (!opt.isPresent()) {
			throw new WalletException(PAYMENT_NOT_FOUND.getMessage());
		}

		// TODO: 23.03.2022 add lock before charge started

		Payment chargePayment = opt.get();
		ResponseEntity<RefundResponse> response;
		try {
			response = stripeService.refund(chargePayment.getPaymentId());
		} catch (HttpClientErrorException e) {
			log.error("Payment not found for refund on stripe service");
			throw e;
		} finally {
			// TODO: 23.03.2022 release redis lock
		}

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
			// TODO: 23.03.2022 error scenario
		}

		// TODO: 23.03.2022 if any exception occurred after redis lock, do not forget to release lock
	}
}
