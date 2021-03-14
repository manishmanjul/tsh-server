package com.tsh.library.dto;

import java.util.List;

public class UpdateFeedbackRequest {

	private int studentBatchId;
	private String studentName;
	private List<FeedbackCategoryTO> feedbacks;
	private int teacherId;
	private String userName;
	private int topicId;
	private String topicName;
	private String topicChapter;
	private String topicDescription;
	private int topicProgressId;

	public int getStudentBatchId() {
		return studentBatchId;
	}

	public void setStudentBatchId(int studentBatchId) {
		this.studentBatchId = studentBatchId;
	}

	public List<FeedbackCategoryTO> getFeedbacks() {
		return feedbacks;
	}

	public void setFeedbacks(List<FeedbackCategoryTO> feedbacks) {
		this.feedbacks = feedbacks;
	}

	public int getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(int teacherId) {
		this.teacherId = teacherId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getTopicChapter() {
		return topicChapter;
	}

	public void setTopicChapter(String topicChapter) {
		this.topicChapter = topicChapter;
	}

	public String getTopicDescription() {
		return topicDescription;
	}

	public void setTopicDescription(String topicDescription) {
		this.topicDescription = topicDescription;
	}

	public int getTopicProgressId() {
		return topicProgressId;
	}

	public void setTopicProgressId(int topicProgressId) {
		this.topicProgressId = topicProgressId;
	}

}
