package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.cache.LockService;
import com.playtomic.tests.wallet.dto.ChargeRequestDto;
import com.playtomic.tests.wallet.dto.ChargeResponse;
import com.playtomic.tests.wallet.dto.RefundResponse;
import com.playtomic.tests.wallet.exception.WalletException;
import com.playtomic.tests.wallet.model.Payment;
import com.playtomic.tests.wallet.model.Wallet;
import com.playtomic.tests.wallet.repository.WalletRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static com.playtomic.tests.wallet.exception.ErrorMessage.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class WalletServiceTest {
	@Autowired
	private WalletService walletService;

	@MockBean
	private WalletRepository mockWalletRepository;

	@MockBean
	private LockService mockLockService;

	@MockBean
	private StripeService mockStripeService;

	@MockBean
	private PaymentService mockPaymentService;

	private Wallet wallet;
	private List<Wallet> walletList;
	private ChargeRequestDto chargeRequestDto;
	private ChargeResponse chargeResponse;
	private RefundResponse refundResponse;
	private Payment payment;

	@BeforeEach
	void setUp() {
		this.wallet = createWalletObject();
		this.walletList = Arrays.asList(wallet);
		this.chargeRequestDto = createChargeRequestDtoObject();
		this.chargeResponse = createChargeResponseObject();
		this.refundResponse = createRefundResponseObject();
		this.payment = createPaymentObject();
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void save_success() {
		// mock
		Mockito.when(mockWalletRepository.save(wallet)).thenReturn(wallet);

		// operation
		Wallet foundWallet = walletService.save(wallet);

		// test
		assertSame(wallet, foundWallet);
	}

	@Test
	void findByUuid_success() {
		// mock
		Mockito.when(mockWalletRepository.findByUuid(any(UUID.class))).thenReturn(Optional.of(wallet));

		// operation
		Optional<Wallet> opt = walletService.findByUuid(wallet.getUuid());

		// test
		assertTrue(opt.isPresent());
		assertSame(wallet, opt.get());
	}

	@Test
	void findAll() {
		// mock
		Mockito.when(mockWalletRepository.findAll()).thenReturn(walletList);

		// operation
		List<Wallet> foundWalletList = walletService.findAll();

		// test
		assertNotNull(foundWalletList);
		assertArrayEquals(foundWalletList.toArray(), walletList.toArray());
	}

	@Test
	void charge_success() {
		// mock
		Mockito.when(mockWalletRepository.save(wallet)).thenReturn(wallet);
		Mockito.when(mockWalletRepository.findByUuid(any(UUID.class))).thenReturn(Optional.of(wallet));
		Mockito.when(mockLockService.isLock(anyString())).thenReturn(false);
		Mockito.when(mockStripeService.charge(anyString(), any(BigDecimal.class))).thenReturn(chargeResponse);
		Mockito.when(mockPaymentService.save(any(Payment.class))).thenReturn(payment);

		Mockito.doNothing().when(mockLockService).lock(anyString());
		Mockito.doNothing().when(mockLockService).unlock(anyString());

		// operation
		walletService.charge(chargeRequestDto, wallet.getUuid());

		// test
		verify(mockPaymentService,times(1)).save(any(Payment.class));

		verify(mockWalletRepository,times(1)).save(any(Wallet.class));
		verify(mockWalletRepository,times(1)).findByUuid(any(UUID.class));

		verify(mockStripeService,times(1)).charge(anyString(), any(BigDecimal.class));

		verify(mockLockService,times(1)).isLock(anyString());
		verify(mockLockService,times(1)).lock(anyString());
		verify(mockLockService,times(1)).unlock(anyString());
	}

	@Test
	void charge_walletException_walletNotFound() {
		// mock
		Mockito.when(mockWalletRepository.findByUuid(any(UUID.class))).thenReturn(Optional.empty());

		// test
		Exception exception = assertThrows(WalletException.class, () -> {
			walletService.charge(chargeRequestDto, UUID.randomUUID());
		});

		assertTrue(WALLET_NOT_FOUND.getMessage().equals(exception.getMessage()));
	}

	@Test
	void charge_walletException_walletDoesNotHaveEnoughBalance() {
		// prepare
		ChargeRequestDto chargeRequestDto = createChargeRequestDtoObject();
		chargeRequestDto.setAmount(new BigDecimal(100));

		Wallet wallet = createWalletObject();
		wallet.setBalance(new BigDecimal(0));

		// mock
		Mockito.when(mockWalletRepository.findByUuid(any(UUID.class))).thenReturn(Optional.of(wallet));

		// test
		Exception exception = assertThrows(WalletException.class, () -> {
			walletService.charge(chargeRequestDto, UUID.randomUUID());
		});

		assertTrue(WALLET_DOES_NOT_HAVE_ENOUGH_BALANCE.getMessage().equals(exception.getMessage()));
	}

	@Test
	void charge_walletException_activePaymentProcess() {
		// mock
		Mockito.when(mockWalletRepository.findByUuid(any(UUID.class))).thenReturn(Optional.of(wallet));
		Mockito.when(mockLockService.isLock(anyString())).thenReturn(true);

		// test
		Exception exception = assertThrows(WalletException.class, () -> {
			walletService.charge(chargeRequestDto, UUID.randomUUID());
		});

		assertTrue(ACTIVE_PAYMENT_PROCESS.getMessage().equals(exception.getMessage()));
	}

	@Test
	void refund_success() {
		//prepare
		ResponseEntity refundResponseEntity = new ResponseEntity(refundResponse, HttpStatus.OK);

		// mock
		Mockito.when(mockWalletRepository.save(wallet)).thenReturn(wallet);
		Mockito.when(mockLockService.isLock(anyString())).thenReturn(false);
		Mockito.when(mockStripeService.refund(anyString())).thenReturn(refundResponseEntity);
		Mockito.when(mockPaymentService.findByUuid(any(UUID.class))).thenReturn(Optional.of(payment));
		Mockito.when(mockPaymentService.save(any(Payment.class))).thenReturn(payment);

		Mockito.doNothing().when(mockLockService).lock(anyString());
		Mockito.doNothing().when(mockLockService).unlock(anyString());

		// operation
		walletService.refund(UUID.randomUUID());

		// test
		verify(mockWalletRepository,times(1)).save(any(Wallet.class));

		verify(mockPaymentService,times(1)).save(any(Payment.class));
		verify(mockPaymentService,times(1)).findByUuid(any(UUID.class));

		verify(mockStripeService,times(1)).refund(anyString());

		verify(mockLockService,times(1)).isLock(anyString());
		verify(mockLockService,times(1)).lock(anyString());
		verify(mockLockService,times(1)).unlock(anyString());
	}

	private Wallet createWalletObject() {
		return Wallet.builder()
					   .createdDate(new Date())
					   .lastModifiedDate(new Date())
					   .enable(false)
					   .balance(new BigDecimal(100))
					   .uuid(UUID.randomUUID())
					   .build();
	}

	private ChargeRequestDto createChargeRequestDtoObject() {
		return ChargeRequestDto.builder()
					   .creditCardNumber("5555555555554444")
					   .amount(new BigDecimal(10))
					   .build();
	}

	private ChargeResponse createChargeResponseObject() {
		return ChargeResponse.builder()
					   .id("id")
					   .amount(new BigDecimal(10))
					   .build();
	}

	private RefundResponse createRefundResponseObject() {
		return RefundResponse.builder()
					   .id("id")
					   .paymentId("payment_id")
					   .amount(new BigDecimal(10))
					   .build();
	}

	private Payment createPaymentObject() {
		return Payment.builder()
					   .refund(false)
					   .amount(chargeResponse.getAmount())
					   .paymentId(chargeResponse.getId())
					   .wallet(wallet)
					   .build();
	}
}