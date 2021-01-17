package com.tsh.entities;

import java.sql.Time;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "batch_progress")
public class BatchProgress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "batch_detail_id")
	private BatchDetails batchDetails;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "topic_id")
	private Topics topic;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "course_id")
	private Course course;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "teacher_id")
	private Teacher teacher;

	@Column(name = "startdate")
	private Date startDate;

	@Column(name = "enddate")
	private Date endDate;

	@Column(name = "planned_startdate")
	private Date plannedStartDate;

	@Column(name = "planned_enddate")
	private Date plannedEndDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status")
	private TopicStatus status;

	@Column(name = "print_booklet")
	private boolean printBooklet;

	@Column(name = "planned_time")
	private Time plannedTime;

	@Column(name = "canceled")
	private boolean canceled = false;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BatchDetails getBatchDetails() {
		return batchDetails;
	}

	public void setBatchDetails(BatchDetails batchDetails) {
		this.batchDetails = batchDetails;
	}

	public Topics getTopic() {
		return topic;
	}

	public void setTopic(Topics topic) {
		this.topic = topic;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getPlannedStartDate() {
		return plannedStartDate;
	}

	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	public Date getPokannedEndDate() {
		return plannedEndDate;
	}

	public void setPokannedEndDate(Date pokannedEndDate) {
		plannedEndDate = pokannedEndDate;
	}

	public TopicStatus getStatus() {
		return status;
	}

	public void setStatus(TopicStatus status) {
		this.status = status;
	}

	public Date getPlannedEndDate() {
		return plannedEndDate;
	}

	public void setPlannedEndDate(Date plannedEndDate) {
		this.plannedEndDate = plannedEndDate;
	}

	public boolean isPrintBooklet() {
		return printBooklet;
	}

	public void setPrintBooklet(boolean printBooklet) {
		this.printBooklet = printBooklet;
	}

	public void printBooklet() {
		this.printBooklet = true;
	}

	public void dontPrintBooklet() {
		this.printBooklet = false;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public Time getPlannedTime() {
		return plannedTime;
	}

	public void setPlannedTime(Time plannedTime) {
		this.plannedTime = plannedTime;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((batchDetails == null) ? 0 : batchDetails.hashCode());
		result = prime * result + ((course == null) ? 0 : course.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + id;
		result = prime * result + ((plannedEndDate == null) ? 0 : plannedEndDate.hashCode());
		result = prime * result + ((plannedStartDate == null) ? 0 : plannedStartDate.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((topic == null) ? 0 : topic.hashCode());
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
		BatchProgress other = (BatchProgress) obj;
		if (batchDetails == null) {
			if (other.batchDetails != null)
				return false;
		} else if (!batchDetails.equals(other.batchDetails))
			return false;
		if (course == null) {
			if (other.course != null)
				return false;
		} else if (!course.equals(other.course))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (id != other.id)
			return false;
		if (plannedEndDate == null) {
			if (other.plannedEndDate != null)
				return false;
		} else if (!plannedEndDate.equals(other.plannedEndDate))
			return false;
		if (plannedStartDate == null) {
			if (other.plannedStartDate != null)
				return false;
		} else if (!plannedStartDate.equals(other.plannedStartDate))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (topic == null) {
			if (other.topic != null)
				return false;
		} else if (!topic.equals(other.topic))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BatchProgress [id=" + id + ", batchDetail=" + batchDetails + ", topic=" + topic + ", course=" + course
				+ ", startDate=" + startDate + ", endDate=" + endDate + ", plannedStartDate=" + plannedStartDate
				+ ", plannedEndDate=" + plannedEndDate + ", status=" + status + "]";
	}
}
