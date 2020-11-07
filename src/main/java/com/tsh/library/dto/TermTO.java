package com.tsh.library.dto;

import java.util.Date;

public class TermTO  {

	private int id;	
	private int term;
	private String description;
	private Date startDate;
	private Date endDate;
	private boolean isCurrent;
	
	private TermTO() {}

	public TermTO(int term) {
		this();
		this.term = term;
		this.description = "Term " + term;
	}
	
	public int getId() {
		return id;
	}

	public int getTerm() {
		return term;
	}

	public String getDescription() {
		return description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public boolean isCurrent() {
		return isCurrent;
	}

	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTerm(int term) {
		this.term = term;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}

