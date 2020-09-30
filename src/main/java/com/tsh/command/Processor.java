package com.tsh.command;

import java.util.List;

import com.tsh.entities.Topics;
import com.tsh.exception.TSHException;

public interface Processor {

	void setParams(String command, List<Object> params);

	ICommand<Topics> findCommandProcessor() throws TSHException;

}
