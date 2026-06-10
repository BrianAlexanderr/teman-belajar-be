package com.project.teman_belajar.module.auth.exception.custom_exception;

public class TokenNotValid extends RuntimeException {

	public TokenNotValid(String message) {
		super(message);
	}
}
