package com.project.teman_belajar.module.auth.exception.custom_exception;

public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(String message) {
		super(message);
	}
}
