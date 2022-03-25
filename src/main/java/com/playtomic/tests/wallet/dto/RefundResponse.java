package com.playtomic.tests.wallet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class RefundResponse {
	String id;
	@JsonProperty("payment_id")
	String paymentId;
	BigDecimal amount;
}
