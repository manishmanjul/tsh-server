package com.tsh.library.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StudentTO {
	private int id;
	private String name;
	private String course;
	private String grade;
	private String previousTopic;
	private int feedbackId;
	private Map<String, FeedbackCategoryTO> feedbacks = new HashMap<>();
	List<FeedbackCategoryTO> sortedFeedback = new ArrayList<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getPreviousTopic() {
		return previousTopic;
	}
	public void setPreviousTopic(String previousTopic) {
		this.previousTopic = previousTopic;
	}
	public int getFeedbackId() {
		return feedbackId;
	}
	public void setFeedbackId(int feedbackId) {
		this.feedbackId = feedbackId;
	}
	public Map<String, FeedbackCategoryTO> getFeedback() {
		return feedbacks;
	}
	public void setFeedback(Map<String, FeedbackCategoryTO> feedbacks) {
		this.feedbacks = feedbacks;
	}
	
	public void addFeedback(FeedbackCategoryTO category, FeedbackTO feedback) {
		if(this.feedbacks.containsKey(category.getOrder() + category.getDescription())) {
			this.feedbacks.get(category.getOrder() + category.getDescription()).addFedback(feedback);;
		}else {
			category.addFedback(feedback);
			this.feedbacks.put(category.getOrder() + category.getDescription(), category);
		}
	}
	
	
	public List<FeedbackCategoryTO> getSortedFeedback() {
		return sortedFeedback;
	}
	public void setSortedFeedback(List<FeedbackCategoryTO> sortedFeedback) {
		this.sortedFeedback = sortedFeedback;
	}
	public void addToSortedMap() {
		if(!this.feedbacks.isEmpty()) {
			Map<String, FeedbackCategoryTO> temp = new TreeMap<>();
			temp.putAll(this.feedbacks);
			for(String key : temp.keySet()) {
				this.sortedFeedback.add(temp.get(key));
			}
			
			this.feedbacks = null;
		}else {
			this.sortedFeedback = getDummyFeedbackMap();
		}
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}	
	
	private List<FeedbackCategoryTO> getDummyFeedbackMap() {
		List<FeedbackCategoryTO> feedbackList = new ArrayList<>();
		FeedbackCategoryTO cat1 = new FeedbackCategoryTO();
		cat1.setDescription("REVISION");
		cat1.setFeedbacks(null);		
		feedbackList.add(cat1);
		cat1 = new FeedbackCategoryTO();
		cat1.setDescription("CLASSWORK");
		cat1.setFeedbacks(null);		
		feedbackList.add(cat1);
		cat1 = new FeedbackCategoryTO();
		cat1.setDescription("HOMEWORK");
		cat1.setFeedbacks(null);		
		feedbackList.add(cat1);
		cat1 = new FeedbackCategoryTO();
		cat1.setDescription("ASSESSMENT");
		cat1.setFeedbacks(null);		
		feedbackList.add(cat1);
		
		return feedbackList;
	}
}
