package com.tsh.command.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.tsh.command.ICommand;
import com.tsh.command.Processor;
import com.tsh.entities.Topics;
import com.tsh.exception.TSHException;

@Component
public class CommandProcessor implements Processor{

	@Autowired
	private ImportTopicExcelProps excelProperties;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private String command;
	private List<Object> params;
	
	private CommandProcessor() {}
	
	public void setParams(String command, List<Object> params) {
		this.command = command;
		this.params = params;
	}
	
	public ICommand<Topics> findCommandProcessor() throws TSHException {
		ImportTopicExcelCommand commandProcessor = null;
		logger.info("Validating command...");
		
		switch (command) {
		case ImportTopicExcelCommand.COMMAND_NAME:
			commandProcessor = new ImportTopicExcelCommand((MultipartFile)params.get(0), excelProperties);
			break;
		default:
			throw new TSHException("Invalid Command : " + command);
		}
		return commandProcessor;
	}

	public ImportTopicExcelProps getExcelProperties() {
		return excelProperties;
	}

	public void setExcelProperties(ImportTopicExcelProps excelProperties) {
		this.excelProperties = excelProperties;
	}
}
