package com.playtomic.tests.wallet.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Getter
@Setter
public class WalletCreateDto {
	@NotNull
	@PositiveOrZero
	private BigDecimal balance;

	@NotNull
	private Boolean enable;
}