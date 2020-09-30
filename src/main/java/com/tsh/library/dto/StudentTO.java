package com.tsh.library.dto;

import java.util.HashMap;
import java.util.Map;

public class StudentTO {
	private int id;
	private String name;
	private String course;
	private String grade;
	private String previousTopic;
	private int feedbackId;
	private Map<String, String> feedback = new HashMap<>();
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
	public Map<String, String> getFeedback() {
		return feedback;
	}
	public void setFeedback(Map<String, String> feedback) {
		this.feedback = feedback;
	}
	
	public void addFeedback(String key, String val) {
		this.feedback.put(key, val);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}	
}
