package com.project.teman_belajar.module.auth.exception.custom_exception;

public class WrongTokenTypeException extends RuntimeException {

	public WrongTokenTypeException(String message) {
		super(message);
	}
}
