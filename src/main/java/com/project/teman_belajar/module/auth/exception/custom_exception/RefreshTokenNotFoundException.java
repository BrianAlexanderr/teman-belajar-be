package com.project.teman_belajar.module.auth.exception.custom_exception;

public class RefreshTokenNotFoundException extends RuntimeException {

	public RefreshTokenNotFoundException(String message) {
		super(message);
	}
}
