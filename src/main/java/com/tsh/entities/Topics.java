package com.tsh.entities;

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
@Table(name = "topics")
public class Topics extends BaseEntity {

	public final static double DEFAULT_ESTIMATED_HOURS = 1.5;
	public final static int DEFAULT_COMPLEXITY = 3;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "chapter")
	private String chapter;

	@Column(name = "description")
	private String description;

	@Column(name = "complexity")
	private int complexity;

	@Column(name = "estimated_hours")
	private double hoursToComplete;

	@Column(name = "topic_name")
	private String topicName;

	@Column(name = "mapped_id")
	private String mappedId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "term_id")
	private Term term;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "grade_id")
	private Grades grade;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "course_id")
	private Course course;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "week_id")
	private Week week;

	@Column(name = "active")
	private boolean active;

	public Topics() {
		setDefaultEstimatedHours();
		setDefaultComplexity();
		this.active = true;
	}

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

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
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

	public String getMappedId() {
		return mappedId;
	}

	public void setMappedId(String mappedId) {
		this.mappedId = mappedId;
	}

	public Week getWeek() {
		return week;
	}

	public void setWeek(Week week) {
		this.week = week;
	}

	public void setDefaultEstimatedHours() {
		this.hoursToComplete = Topics.DEFAULT_ESTIMATED_HOURS;
	}

	public void setDefaultComplexity() {
		this.complexity = Topics.DEFAULT_COMPLEXITY;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getTopicFullName() {
		String topicDescription = getTopicName();
		if (topicDescription.length() > 0)
			topicDescription = topicDescription + " : " + getDescription();
		else
			topicDescription = getDescription();

		if (topicDescription.length() > 0)
			topicDescription = topicDescription + " : " + getChapter();
		else
			topicDescription = getChapter();

		return topicDescription;
	}

	@Override
	public String toString() {
		return "Topics [chapter=" + chapter + ", description=" + description + ", topicName=" + topicName
				+ ", mappedId=" + mappedId + ", term=" + term + ", grade=" + grade + ", course=" + course + ", week="
				+ week + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chapter == null) ? 0 : chapter.hashCode());
		result = prime * result + complexity;
		result = prime * result + ((course == null) ? 0 : course.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((grade == null) ? 0 : grade.hashCode());
		long temp;
		temp = Double.doubleToLongBits(hoursToComplete);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + id;
		result = prime * result + ((mappedId == null) ? 0 : mappedId.hashCode());
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		result = prime * result + ((topicName == null) ? 0 : topicName.hashCode());
		result = prime * result + ((week == null) ? 0 : week.hashCode());
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
		Topics other = (Topics) obj;
		if (chapter == null) {
			if (other.chapter != null)
				return false;
		} else if (!chapter.equals(other.chapter))
			return false;
		if (complexity != other.complexity)
			return false;
		if (course == null) {
			if (other.course != null)
				return false;
		} else if (!course.equals(other.course))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (grade == null) {
			if (other.grade != null)
				return false;
		} else if (!grade.equals(other.grade))
			return false;
		if (Double.doubleToLongBits(hoursToComplete) != Double.doubleToLongBits(other.hoursToComplete))
			return false;
		if (id != other.id)
			return false;
		if (mappedId == null) {
			if (other.mappedId != null)
				return false;
		} else if (!mappedId.equals(other.mappedId))
			return false;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		if (topicName == null) {
			if (other.topicName != null)
				return false;
		} else if (!topicName.equals(other.topicName))
			return false;
		if (week == null) {
			if (other.week != null)
				return false;
		} else if (!week.equals(other.week))
			return false;
		return true;
	}

}
