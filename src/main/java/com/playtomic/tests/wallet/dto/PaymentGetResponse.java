package com.playtomic.tests.wallet.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PaymentGetResponse {
	private UUID uuid;
	private BigDecimal amount;
	private Boolean refund;
	private Date createdDate;
	private Date lastModifiedDate;
}
