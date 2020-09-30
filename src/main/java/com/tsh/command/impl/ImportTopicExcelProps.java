package com.tsh.command.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "excel")
public class ImportTopicExcelProps {

	private String uploadDir;
	private int fileSize;
	private String extension;
	private List<String> topicsFor = new ArrayList<>();
	private String sheetWithTopics;
	private final String contentType = "application/vnd.ms-excel.sheet.macroEnabled.12";
	
	public ImportTopicExcelProps() {
		if(this.fileSize == 0) this.fileSize = 1;
		if(this.extension == null) this.extension = ".xlsm";
		if(this.uploadDir== null) this.uploadDir = "uploads";
		if(this.sheetWithTopics == null) this.sheetWithTopics = "Topics";
	}
	
	public String getUploadDir() {
		return uploadDir;
	}

	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getContentType() {
		return contentType;
	}

	public List<String> getTopicsFor() {
		return topicsFor;
	}

	public void setTopicsFor(List<String> topicsFor) {
		this.topicsFor = topicsFor;
	}

	public void addTopicsFor(String topicsFor) {
		this.topicsFor.add(topicsFor);
	}
	
	public String getSheetWithTopics() {
		return sheetWithTopics;
	}

	public void setSheetWithTopics(String sheetWithTopics) {
		this.sheetWithTopics = sheetWithTopics;
	}
}
