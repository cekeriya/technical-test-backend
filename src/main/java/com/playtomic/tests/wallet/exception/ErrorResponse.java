package com.playtomic.tests.wallet.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ErrorResponse {

	@NonNull
	private int code;

	@NonNull
	private String status;

	private String message;

	@NonNull
	private Date timestamp;

	public ErrorResponse(String status, int code, String message) {
		this.timestamp = new Date();
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
