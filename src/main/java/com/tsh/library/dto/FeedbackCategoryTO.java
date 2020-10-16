package com.tsh.library.dto;

import java.util.ArrayList;
import java.util.List;

public class FeedbackCategoryTO implements Comparable<FeedbackCategoryTO>{

	private int id;
	
	private int category;
	private String description;
	private boolean active;
	private String teachersComment;
	private int order;
	
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

	public String getTeachersComment() {
		return teachersComment;
	}

	public void setTeachersComment(String teachersComment) {
		this.teachersComment = teachersComment;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + category;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedbackCategoryTO other = (FeedbackCategoryTO) obj;
		if (active != other.active)
			return false;
		if (category != other.category)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(FeedbackCategoryTO o) {
		System.out.println("Calling compare to");
		return this.getOrder() - o.getOrder();
	}
}
