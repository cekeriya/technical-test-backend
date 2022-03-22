package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.dto.WalletCreateDto;
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
	@RequestMapping()
	public ResponseEntity getWallet(@PathVariable @NotBlank String uuid) {
		Optional<Wallet> opt = walletService.findByUuid(uuid);

		if (opt.isPresent()) {
			return new ResponseEntity(walletMapper.toWalletGetDto(walletService.findByUuid(uuid).get()), HttpStatus.OK);
		} else {
			return new ResponseEntity("Wallet not found", HttpStatus.NOT_FOUND);
		}

	}

	@GetMapping
	public ResponseEntity getWallets() {
		return new ResponseEntity(walletMapper.toWalletGetDtoList(walletService.findAll()), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<?> createWallet(@RequestBody @Valid WalletCreateDto walletCreateDto) {
		walletService.save(walletMapper.toWallet(walletCreateDto));
		return new ResponseEntity(HttpStatus.CREATED);
	}
}
