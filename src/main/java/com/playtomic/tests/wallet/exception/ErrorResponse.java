package com.playtomic.tests.wallet.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

	@NonNull
	private int code;

	@NonNull
	private String status;

	private String message;

	public ErrorResponse(String status, int code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
