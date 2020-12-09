package com.tsh.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "attendence")
public class Attendence extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "student_batch_id")
	private StudentBatches studentBatch;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "teacher_id")
	private Teacher teacher;

	@Column(name = "absence_date")
	private Date absenseDate;

	@Column(name = "half_day")
	private boolean halfDay;

	public Attendence() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public StudentBatches getStudent() {
		return studentBatch;
	}

	public void setStudent(StudentBatches studentBatch) {
		this.studentBatch = studentBatch;
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
