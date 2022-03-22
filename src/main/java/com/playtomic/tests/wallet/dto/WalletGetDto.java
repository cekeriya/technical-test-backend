package com.playtomic.tests.wallet.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class WalletGetDto {
	private UUID uuid;
	private BigDecimal balance;
	private Date createdDate;
	private Date lastModifiedDate;
	private Boolean enable;
}
