package com.playtomic.tests.wallet.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.playtomic.tests.wallet.dto.ChargeResponse;
import com.playtomic.tests.wallet.dto.RefundResponse;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;


/**
 * Handles the communication with Stripe.
 * <p>
 * A real implementation would call to String using their API/SDK.
 * This dummy implementation throws an error when trying to charge less than 10€.
 */
@Service
public class StripeService {

	@NonNull
	private URI chargesUri;

	@NonNull
	private URI refundsUri;

	@NonNull
	private RestTemplate restTemplate;

	public StripeService(@Value("${stripe.simulator.charges-uri}") @NonNull URI chargesUri,
						 @Value("${stripe.simulator.refunds-uri}") @NonNull URI refundsUri,
						 @NotNull RestTemplateBuilder restTemplateBuilder) {
		this.chargesUri = chargesUri;
		this.refundsUri = refundsUri;
		this.restTemplate = restTemplateBuilder.errorHandler(new StripeRestTemplateResponseErrorHandler())
									.build();
	}

	/**
	 * Charges money in the credit card.
	 * <p>
	 * Ignore the fact that no CVC or expiration date are provided.
	 *
	 * @param creditCardNumber The number of the credit card
	 * @param amount           The amount that will be charged.
	 * @throws StripeServiceException
	 */
	public ChargeResponse charge(@NonNull String creditCardNumber, @NonNull BigDecimal amount) throws StripeServiceException {
		ChargeRequest body = new ChargeRequest(creditCardNumber, amount);
		return restTemplate.postForObject(chargesUri, body, ChargeResponse.class);
	}

	/**
	 * Refunds the specified payment.
	 */
	public ResponseEntity<RefundResponse> refund(@NonNull String paymentId) throws StripeServiceException {
		return restTemplate.postForEntity(refundsUri.toString().replace("%7Bpayment_id%7D", paymentId), null,
				RefundResponse.class, paymentId);
	}

	@AllArgsConstructor
	private static class ChargeRequest {

		@NonNull
		@JsonProperty("credit_card")
		String creditCardNumber;

		@NonNull
		@JsonProperty("amount")
		BigDecimal amount;
	}
}
