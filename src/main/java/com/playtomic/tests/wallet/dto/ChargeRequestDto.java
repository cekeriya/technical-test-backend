package com.playtomic.tests.wallet.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Getter
@Setter
public class ChargeRequestDto {
	@CreditCardNumber
	private String creditCardNumber;

	@Positive
	private BigDecimal amount;
}
