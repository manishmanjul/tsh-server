package com.tsh.library.dto;

import java.util.ArrayList;

public class StudentFeedbackResponseTO {
	
	public StudentFeedbackResponseTO() {	
	}
	
	int todaysTopicId;
	String todaysTopicDesc;
	int nextTopicId;
	String nextTopicDesc;
	int batchDetailId;
	int courseId;
	String classDate;
	boolean printBooklet;
	int updatedById;
	ArrayList<FeedbackResponseTO> feedbacks;
	ArrayList<StudentResponseTO> students;
	
	public int getTodaysTopicId() {
		return todaysTopicId;
	}
	public void setTodaysTopicId(int todaysTopicId) {
		this.todaysTopicId = todaysTopicId;
	}
	public String getTodaysTopicDesc() {
		return todaysTopicDesc;
	}
	public void setTodaysTopicDesc(String todaysTopicDesc) {
		this.todaysTopicDesc = todaysTopicDesc;
	}
	public int getNextTopicId() {
		return nextTopicId;
	}
	public void setNextTopicId(int nextTopicId) {
		this.nextTopicId = nextTopicId;
	}
	public String getNextTopicDesc() {
		return nextTopicDesc;
	}
	public void setNextTopicDesc(String nextTopicDesc) {
		this.nextTopicDesc = nextTopicDesc;
	}
	public int getBatchDetailId() {
		return batchDetailId;
	}
	public void setBatchDetailId(int batchDetailId) {
		this.batchDetailId = batchDetailId;
	}
	public int getCourseId() {
		return courseId;
	}
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}
	public String getClassDate() {
		return classDate;
	}
	public void setClassDate(String classDate) {
		this.classDate = classDate;
	}
	public boolean isPrintBooklet() {
		return printBooklet;
	}
	public void setPrintBooklet(boolean printBooklet) {
		this.printBooklet = printBooklet;
	}
	public int getUpdatedById() {
		return updatedById;
	}
	public void setUpdatedById(int updatedById) {
		this.updatedById = updatedById;
	}
	public ArrayList<FeedbackResponseTO> getFeedbacks() {
		return feedbacks;
	}
	public void setFeedbacks(ArrayList<FeedbackResponseTO> feedbacks) {
		this.feedbacks = feedbacks;
	}
	public ArrayList<StudentResponseTO> getStudents() {
		return students;
	}
	public void setStudents(ArrayList<StudentResponseTO> students) {
		this.students = students;
	}
}
