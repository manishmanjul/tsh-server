package com.tsh.library.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.tsh.entities.Course;
import com.tsh.entities.Grades;
import com.tsh.entities.Term;
import com.tsh.entities.Week;

public class TopicsTO {

	private int id;
	private String chapter;
	private String description;
	private String topicName;
	private int complexity;
	private double hoursToComplete;
	private Term term;
	private Grades grade;
	private Course course;
	private Week week;
	private boolean active;
	private String status;
	private String startDate;
	private String endDate;
	private String plannedStartDate;
	private String plannedEndDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public double getHoursToComplete() {
		return hoursToComplete;
	}

	public void setHoursToComplete(double hoursToComplete) {
		this.hoursToComplete = hoursToComplete;
	}

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	public Grades getGrade() {
		return grade;
	}

	public void setGrade(Grades grade) {
		this.grade = grade;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Week getWeek() {
		return week;
	}

	public void setWeek(Week week) {
		this.week = week;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		this.startDate = formatter.format(startDate);
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		this.endDate = formatter.format(endDate);
	}

	public String getPlannedStartDate() {
		return plannedStartDate;
	}

	public void setPlannedStartDate(Date plannedStartDate) {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		this.plannedStartDate = formatter.format(plannedStartDate);
	}

	public String getPlannedEndDate() {
		return plannedEndDate;
	}

	public void setPlannedEndDate(Date plannedEndDate) {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		this.plannedEndDate = formatter.format(plannedEndDate);
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

}
