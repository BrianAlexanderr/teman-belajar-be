package com.project.teman_belajar.module.auth.exception.custom_exception;

public class TokenRefreshException extends RuntimeException {

	public TokenRefreshException(String token) {
		super(String.format("Invalid token for token %s", token));
	}
}
