package com.tsh.command.impl;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.tsh.command.ICommand;
import com.tsh.entities.Topics;
import com.tsh.exception.TSHException;
import com.tsh.utility.ExcelReader;

public class ImportTopicExcelCommand extends BaseCommand<Topics> implements ICommand<Topics>{

	public static final String COMMAND_NAME= "Import Topic Excel";
	private MultipartFile file;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private ImportTopicExcelProps props;
	
	public ImportTopicExcelCommand(MultipartFile file, ImportTopicExcelProps props) {
		this.file = file;
		this.props = props;
	}
	
	public List<Topics> execute() throws TSHException {
		validate();
		Path targetFile = storeFile();
		return startImportingFromExcel(targetFile);
	}
	
	private Path storeFile() throws TSHException {
		logger.info("File " + this.file.getOriginalFilename() + " passed validation. Storing file.");
		Path targetLocation = null;
		targetLocation = Paths.get(this.props.getUploadDir()).toAbsolutePath().normalize();
		if(!Files.exists(targetLocation)) {
			try {
				Files.createDirectories(targetLocation);
			} catch (IOException e) {
				throw new TSHException("Could not store file. Cause: Could not create upload directory.");
			}
		}
		Path targetFile = targetLocation.resolve(this.generateNextFileName());
		try {
			Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new TSHException("Unable to store file due to : " + e.getMessage());
		}
		logger.info("File successfully saved to the {} folder.",this.props.getUploadDir());
		return targetFile;
	}
	
	private void validate() throws TSHException {
		logger.info("Validating received file...");	
		try {
			String originalFileName = StringUtils.cleanPath(this.file.getOriginalFilename());
			if(originalFileName.contains("..")) {
				throw new TSHException("File " + originalFileName + " contains invalid characters.");
			}
			String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
			if(!extension.contains(this.props.getExtension())) {
				throw new TSHException("Extension " + extension + " not supported for Import Topic from Excel");
			}
			if(this.file.getSize() > this.props.getFileSize()*1024*1024) {
				throw new TSHException("File size more than " + this.props.getFileSize() + " MB not allowed. Current size in MB: " + (file.getSize()/1024)/1024);
			}
			if(!this.file.getContentType().equalsIgnoreCase(this.props.getContentType())) {
				throw new TSHException("Content type : " + this.file.getContentType() + " not sopported. Should be : " + this.props.getContentType());
			}
		}catch(Exception e) {
			throw new TSHException("Internal Error - " + e.getMessage());
		}
	}
	
	private List<Topics> startImportingFromExcel(Path targetFile) throws TSHException{
		ExcelReader reader = new ExcelReader(targetFile.toFile(), props);
		return reader.consume();
	}
	
	public String generateNextFileName() {
		return "Topic_" + Calendar.getInstance().getTimeInMillis() + this.props.getExtension();
	}
}
