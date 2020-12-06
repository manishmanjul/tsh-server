package com.tsh.library.dto;

public class DeleteFeedbackRequest {
	private int studentBatchId;
	private int topicId;
	private int teacherId;

	public int getStudentBatchId() {
		return studentBatchId;
	}

	public void setStudentBatchId(int studentBatchId) {
		this.studentBatchId = studentBatchId;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public int getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(int teacherId) {
		this.teacherId = teacherId;
	}
}
