package com.playtomic.tests.wallet.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ChargeResponse {
	String id;
	BigDecimal amount;
}
