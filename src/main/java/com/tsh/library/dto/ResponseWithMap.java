package com.tsh.library.dto;

import java.util.List;
import java.util.Map;

import com.tsh.entities.BaseEntity;

public class ResponseWithMap<T extends BaseEntity>{
	private ResponseMessage responseMessage;
	
	private Map<String, List<T>> data;

	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}

	public Map<String, List<T>> getData() {
		return data;
	}

	public void setData(Map<String, List<T>> data) {
		this.data = data;
	}
}
