package com.project.teman_belajar.module.auth.exception.custom_exception;

public class TokenExpiredException extends RuntimeException {

	public TokenExpiredException(String message) {
		super(message);
	}
}
