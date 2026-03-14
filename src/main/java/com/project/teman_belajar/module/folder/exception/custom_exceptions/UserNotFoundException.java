package com.project.teman_belajar.module.folder.exception.custom_exceptions;

public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(String message) {
		super(message);
	}
}
