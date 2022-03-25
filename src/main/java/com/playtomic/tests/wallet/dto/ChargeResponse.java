package com.playtomic.tests.wallet.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ChargeResponse {
	String id;
	BigDecimal amount;
}
