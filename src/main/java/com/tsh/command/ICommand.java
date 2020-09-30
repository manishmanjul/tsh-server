package com.tsh.command;

import java.util.List;

import com.tsh.exception.TSHException;

public interface ICommand<T extends Object> {
	
	public final int SUCCESS = 1;
	public final int FAILED = 0;
	
	public List<T> execute() throws TSHException;
}
