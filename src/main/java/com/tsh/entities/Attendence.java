package com.tsh.entities;

import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="attendence")
public class Attendence extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name="student_id")
	private Student student;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name="teacher_id")
	private Teacher teacher;
	
	@Column(name="absense_date")
	private Date absenseDate;
	
	@Column(name="half_day")
	private boolean halfDay;
	
	public Attendence() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public Date getAbsenseDate() {
		return absenseDate;
	}

	public void setAbsenseDate(Date absenseDate) {
		this.absenseDate = absenseDate;
	}

	public boolean isHalfDay() {
		return halfDay;
	}

	public void setHalfDay(boolean halfDay) {
		this.halfDay = halfDay;
	}
}
