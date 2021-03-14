package com.tsh.library.dto;

public class ImportStatistics {

	private String importDesc;
	private int status;
	private int count;

	public String getImportDesc() {
		return importDesc;
	}

	public void setImportDesc(String importDesc) {
		this.importDesc = importDesc;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
