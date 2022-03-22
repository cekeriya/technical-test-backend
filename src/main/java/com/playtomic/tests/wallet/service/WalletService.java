package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.model.Wallet;
import com.playtomic.tests.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WalletService {
	private final WalletRepository walletRepository;

	public WalletService(WalletRepository walletRepository) {
		this.walletRepository = walletRepository;
	}

	public void save(Wallet wallet) {
		walletRepository.save(wallet);
	}

	public Optional<Wallet> findByUuid(String uuid){
		return walletRepository.findByUuid(uuid);
	}

	public List<Wallet> findAll() {
		return walletRepository.findAll();
	}
}
