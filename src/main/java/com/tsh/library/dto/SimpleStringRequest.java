package com.tsh.library.dto;

public class SimpleStringRequest {
	private String request;

	public SimpleStringRequest(String request) {
		super();
		this.request = request;
	}

	public SimpleStringRequest() {
		super();
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}
}
