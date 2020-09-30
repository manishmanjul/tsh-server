package com.tsh.command.impl;

import java.util.List;

import com.tsh.command.ICommand;
import com.tsh.exception.TSHException;

public class BaseCommand<T extends Object> implements ICommand<T>{

	@Override
	public List<T> execute() throws TSHException {
		// TODO Auto-generated method stub
		return null;
	}

}
