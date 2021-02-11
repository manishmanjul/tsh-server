package com.tsh.library.dto;

import java.util.ArrayList;
import java.util.List;

public class DashboardItems {

	private int batchDetailId;
	private String startTime;
	private int roomNumber;
	private String course;
	private TeacherTO teacher;
	private String grade;
	private String bgColor;
	private String fontColor;
	private boolean cancelled;
	private List<StudentTO> studentList;

	public int getBatchDetailId() {
		return batchDetailId;
	}

	public void setBatchDetailId(int batchDetailId) {
		this.batchDetailId = batchDetailId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public TeacherTO getTeacher() {
		return teacher;
	}

	public void setTeacher(TeacherTO teacher) {
		this.teacher = teacher;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public List<StudentTO> getStudentList() {
		return studentList;
	}

	public void setStudentList(List<StudentTO> studentList) {
		this.studentList = studentList;
	}

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	public void addStudentTO(StudentTO studentTO) {
		if (studentList == null)
			studentList = new ArrayList<>();
		studentList.add(studentTO);
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
