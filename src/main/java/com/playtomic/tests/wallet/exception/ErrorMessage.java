package com.playtomic.tests.wallet.exception;

public enum ErrorMessage {
	WALLET_NOT_FOUND("Wallet not found"),
	WALLET_DOES_NOT_HAVE_ENOUGH_BALANCE("Wallet does not have enough balance"),
	ACTIVE_PAYMENT_PROCESS("Active payment process found on current wallet"),
	PAYMENT_PROCESS_ERROR("An error occurred during processing payment"),
	REFUND_PROCESS_ERROR("An error occurred during processing payment"),
	NOT_REFUNDABLE_PAYMENT("Active payment can not found for refund"),
	PAYMENT_NOT_FOUND("Payment not found");

	private String message;

	ErrorMessage(String messages) {
		this.message = messages;
	}

	public String getMessage() {
		return message;
	}
}
