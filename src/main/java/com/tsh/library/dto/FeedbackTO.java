package com.tsh.library.dto;

public class FeedbackTO {

	private int id;

	private String description;

	private String shortDescription;

	private String criteria;
	private boolean active;
	private int category;

	public FeedbackTO() {
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public String getCriteria() {
		return criteria;
	}

	public boolean isActive() {
		return active;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

}
