package com.tsh.entities;

import java.util.Calendar;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.tsh.utility.TshUtil;

@Entity
@Table(name="batch_details")
public class BatchDetails extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name="batch_id")
	private Batch batch;
	
	@Column(name="batch_name")
	private String batchName;
	
	@Column(name="start_date")
	private Date startDate;
	
	@Column(name="end_date")
	private Date endDate;
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name="teacher_id")
	private Teacher teacher;
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name="course_id")
	private Course course;
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name="grade_id")
	private Grades grade;

	private int performance;
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name="training_type_id")
	private TrainingType trainingType;
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name="term_id")
	private Term term;
	
	@Column(name="active")
	private boolean active;
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	private BatchDetails() {}
	
	public BatchDetails(Batch batch, Teacher teacher, Course course, Grades grade) {
		this();
		this.batch = batch;
		this.teacher = teacher;
		this.course = course;
		this.grade = grade;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Batch getBatch() {
		return batch;
	}

	public void setBatch(Batch batch) {
		this.batch = batch;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String subject, String timeSlot) {
		this.batchName = subject + ":" + timeSlot;
	}

	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setStartDate(Date batchDate) {
		this.startDate = batchDate;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Grades getGrade() {
		return grade;
	}

	public void setGrade(Grades grade) {
		this.grade = grade;
	}

	public int getPerformance() {
		return performance;
	}

	public void setPerformance(int performance) {
		this.performance = performance;
	}

	public TrainingType getTrainingType() {
		return trainingType;
	}

	public void setTrainingType(TrainingType trainingType) {
		this.trainingType = trainingType;
	}

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((batch == null) ? 0 : batch.hashCode());
		result = prime * result + ((course == null) ? 0 : course.hashCode());
		result = prime * result + ((grade == null) ? 0 : grade.hashCode());
		result = prime * result + id;
		result = prime * result + ((teacher == null) ? 0 : teacher.hashCode());
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BatchDetails other = (BatchDetails) obj;
		if (batch == null) {
			if (other.batch != null)
				return false;
		} else if (!batch.equals(other.batch))
			return false;
		if (course == null) {
			if (other.course != null)
				return false;
		} else if (!course.equals(other.course))
			return false;
		if (grade == null) {
			if (other.grade != null)
				return false;
		} else if (!grade.equals(other.grade))
			return false;
		if (id != other.id)
			return false;
		if (teacher == null) {
			if (other.teacher != null)
				return false;
		} else if (!teacher.equals(other.teacher))
			return false;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "BatchDetails [id=" + id + ", batch=" + batch + ", batchName=" + batchName + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", teacher=" + teacher + ", course=" + course + ", grade=" + grade
				+ ", trainingType=" + trainingType + ", term=" + term + ", active=" + active + "]";
	}

	public static BatchDetails getNewInstance(Batch batch, Teacher teacher, Course course, Grades grade) {
		BatchDetails batchDetails = new BatchDetails(batch, teacher, course, grade);
		
		batchDetails.setActive(true);
		batchDetails.setStartDate(Calendar.getInstance().getTime());
		batchDetails.setBatchName(course.getShortDescription(), batch.getTimeSlot().getName());
		return batchDetails;
	}
	
	public boolean isCLassToday() {
		return this.batch.getTimeSlot().getWeekday() == TshUtil.getTodaysWeekDay();
	}
}
