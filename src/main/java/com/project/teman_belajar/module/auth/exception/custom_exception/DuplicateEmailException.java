package com.project.teman_belajar.module.auth.exception.custom_exception;

public class DuplicateEmailException extends RuntimeException {

	public DuplicateEmailException(String message) {
		super(message);
	}
}
