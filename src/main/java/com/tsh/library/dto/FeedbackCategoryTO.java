package com.tsh.library.dto;

import java.util.ArrayList;
import java.util.List;

public class FeedbackCategoryTO {

	private int id;
	
	private int category;
	private String description;
	private boolean active;
	
	private List<FeedbackTO> feedbacks;
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public FeedbackCategoryTO() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public List<FeedbackTO> getFeedbacks() {
		return feedbacks;
	}

	public void setFeedbacks(List<FeedbackTO> feedbacks) {
		this.feedbacks = feedbacks;
	}
	public void addFedback(FeedbackTO feedback) {
		if(feedbacks == null)
			feedbacks = new ArrayList<>();
		
		feedbacks.add(feedback);
	}
}
