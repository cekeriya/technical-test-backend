package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.dto.ChargeRequestDto;
import com.playtomic.tests.wallet.dto.WalletCreateDto;
import com.playtomic.tests.wallet.exception.ErrorMessage;
import com.playtomic.tests.wallet.exception.ErrorResponse;
import com.playtomic.tests.wallet.mapper.WalletMapper;
import com.playtomic.tests.wallet.model.Wallet;
import com.playtomic.tests.wallet.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/wallets")
public class WalletController {
	private final WalletService walletService;

	private final WalletMapper walletMapper;

	public WalletController(WalletService walletService, WalletMapper walletMapper) {
		this.walletService = walletService;
		this.walletMapper = walletMapper;
	}

	@GetMapping("/{uuid}")
	public ResponseEntity getWallet(@PathVariable @NotBlank UUID uuid) {
		Optional<Wallet> opt = walletService.findByUuid(uuid);

		if (opt.isPresent()) {
			return new ResponseEntity(walletMapper.toWalletGetDto(opt.get()), HttpStatus.OK);
		} else {
			HttpStatus status = HttpStatus.NOT_FOUND;
			return new ResponseEntity<>(new ErrorResponse(status.name(), status.value(), ErrorMessage.WALLET_NOT_FOUND.getMessage()), status);
		}
	}

	@GetMapping
	public ResponseEntity getWallets() {
		return new ResponseEntity(walletMapper.toWalletGetDtoList(walletService.findAll()), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity createWallet(@RequestBody @Valid WalletCreateDto walletCreateDto) {
		Wallet wallet = walletService.save(walletMapper.toWallet(walletCreateDto));
		return new ResponseEntity(walletMapper.toWalletGetDto(wallet), HttpStatus.CREATED);
	}

	@PostMapping("/{uuid}/charge")
	public ResponseEntity charge(@RequestBody @Valid ChargeRequestDto chargeRequestDto, @PathVariable @NotBlank UUID uuid) {
		walletService.charge(chargeRequestDto, uuid);
		return new ResponseEntity(HttpStatus.OK);
	}
}
