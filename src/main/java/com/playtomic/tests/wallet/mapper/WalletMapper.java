package com.playtomic.tests.wallet.mapper;

import com.playtomic.tests.wallet.dto.WalletCreateDto;
import com.playtomic.tests.wallet.dto.WalletGetDto;
import com.playtomic.tests.wallet.model.Wallet;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WalletMapper {
	WalletGetDto toWalletGetDto(Wallet wallet);

	List<WalletGetDto> toWalletGetDtoList(List<Wallet> walletList);

	Wallet toWallet(WalletGetDto walletGetDto);

	Wallet toWallet(WalletCreateDto walletCreateDto);

	@InheritConfiguration
	@Mapping(target = "uuid", ignore = true)
	Wallet update(WalletGetDto walletGetDto, @MappingTarget Wallet wallet);
}
