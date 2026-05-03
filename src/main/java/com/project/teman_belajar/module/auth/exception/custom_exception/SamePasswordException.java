package com.project.teman_belajar.module.auth.exception.custom_exception;

public class SamePasswordException extends RuntimeException {

	public SamePasswordException(String message) {
		super(message);
	}
}
