package com.project.teman_belajar.module.auth.exception.custom_exception;

public class OtpNotValidException extends RuntimeException {

	public OtpNotValidException(String message) {
		super(message);
	}
}
