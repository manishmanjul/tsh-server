package com.tsh.library.dto;

import java.util.ArrayList;
import java.util.List;

public class ScheduleTO {
	
	private String key;
	private String day;
	private String date;
	private String startTime;
	private String endTime;
	private String teacherName;
	private int grade;
	private String term;
	private int courseId;
	private String course;
	private String courseDescription;
	private List<StudentTO> attendies = new ArrayList<>();
	private List<TopicsTO> topics = new ArrayList<>();
	private TopicsTO currentTopic;
	private TopicsTO nextTopic;
	
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public int getGrade() {
		return grade;
	}
	public void setGrade(int grade) {
		this.grade = grade;
	}
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	public List<StudentTO> getAttendies() {
		return attendies;
	}
	public void setAttendies(List<StudentTO> attendies) {
		this.attendies = attendies;
	}
	public void addAttendies(StudentTO attendee) {
		this.attendies.add(attendee);
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public ScheduleTO() {
		super();
	}
	public String getCourseDescription() {
		return courseDescription;
	}
	public void setCourseDescription(String courseDescription) {
		this.courseDescription = courseDescription;
	}
	public List<TopicsTO> getTopics() {
		return topics;
	}
	public void setTopics(List<TopicsTO> topics) {
		this.topics = topics;
	}
	public TopicsTO getCurrentTopic() {
		return currentTopic;
	}
	public void setCurrentTopic(TopicsTO previousTopic) {
		this.currentTopic = previousTopic;
	}
	public TopicsTO getNextTopic() {
		return nextTopic;
	}
	public void setNextTopic(TopicsTO nextTopic) {
		this.nextTopic = nextTopic;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public int getCourseId() {
		return courseId;
	}
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}	
	
}
