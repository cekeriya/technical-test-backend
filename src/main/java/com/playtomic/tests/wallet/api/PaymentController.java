package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.mapper.PaymentMapper;
import com.playtomic.tests.wallet.service.PaymentService;
import com.playtomic.tests.wallet.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/payments")
public class PaymentController {
	private final WalletService walletService;
	private final PaymentService paymentService;
	private final PaymentMapper paymentMapper;

	public PaymentController(WalletService walletService, PaymentService paymentService, PaymentMapper paymentMapper) {
		this.walletService = walletService;
		this.paymentService = paymentService;
		this.paymentMapper = paymentMapper;
	}

	@GetMapping
	public ResponseEntity getWallets() {
		return new ResponseEntity(paymentMapper.toPaymentGetResponseList(paymentService.findAll()), HttpStatus.OK);
	}

	@PostMapping("/{uuid}/refund")
	public ResponseEntity refund(@PathVariable @NotBlank UUID uuid) {
		walletService.refund(uuid);
		return new ResponseEntity(HttpStatus.OK);
	}
}
