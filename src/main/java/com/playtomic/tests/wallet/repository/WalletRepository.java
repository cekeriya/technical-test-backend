package com.playtomic.tests.wallet.repository;

import com.playtomic.tests.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
	Optional<Wallet> findByUuid(String uuid);
}
