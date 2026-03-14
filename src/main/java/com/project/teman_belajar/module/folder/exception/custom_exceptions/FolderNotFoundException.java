package com.project.teman_belajar.module.folder.exception.custom_exceptions;

public class FolderNotFoundException extends RuntimeException {

	public FolderNotFoundException(String message) {
		super(message);
	}
}
