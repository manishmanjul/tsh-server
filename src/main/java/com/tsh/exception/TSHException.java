package com.tsh.exception;

import javassist.NotFoundException;

public class TSHException extends NotFoundException {

	private static final long serialVersionUID = 1L;

	public TSHException(String msg) {
		super(msg);		
	}

}
