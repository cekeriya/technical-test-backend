package com.playtomic.tests.wallet.exception;

public enum ErrorMessage {
	WALLET_NOT_FOUND("Wallet not found"),
	WALLET_DOES_NOT_HAVE_ENOUGH_BALANCE("Wallet does not have enough balance"),
	PAYMENT_NOT_FOUND("Payment not found");

	private String message;

	ErrorMessage(String messages) {
		this.message = messages;
	}

	public String getMessage() {
		return message;
	}
}
