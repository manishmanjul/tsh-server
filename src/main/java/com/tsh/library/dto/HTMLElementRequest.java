package com.tsh.library.dto;

public class HTMLElementRequest {
	private String element;
	private int studentBatchId;

	public HTMLElementRequest(String element, int studentBatchId) {
		this.element = element;
		this.studentBatchId = studentBatchId;
	}

	public HTMLElementRequest() {
		super();
	}

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public int getStudentBatchId() {
		return studentBatchId;
	}

	public void setStudentBatchId(int studentBatchId) {
		this.studentBatchId = studentBatchId;
	}

}
