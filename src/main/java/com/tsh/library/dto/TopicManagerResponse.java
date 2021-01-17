package com.tsh.library.dto;

import java.util.ArrayList;
import java.util.List;

public class TopicManagerResponse {

	List<TermTO> terms;
	List<GradeTO> grades;
	List<TopicManagerCourse> course;
	List<CourseTO> rCourse;
	List<WeekTO> week;

	public List<TermTO> getTerms() {
		return terms;
	}

	public void setTerms(List<TermTO> terms) {
		this.terms = terms;
	}

	public List<GradeTO> getGrades() {
		return grades;
	}

	public void setGrades(List<GradeTO> grades) {
		this.grades = grades;
	}

	public List<TopicManagerCourse> getCourse() {
		return course;
	}

	public void setCourse(List<TopicManagerCourse> course) {
		this.course = course;
	}

	public void addCourse(TopicManagerCourse c) {
		if (course == null)
			course = new ArrayList<>();
		course.add(c);
	}

	public List<CourseTO> getrCourse() {
		return rCourse;
	}

	public void setrCourse(List<CourseTO> rCourse) {
		this.rCourse = rCourse;
	}

	public List<WeekTO> getWeek() {
		return week;
	}

	public void setWeek(List<WeekTO> week) {
		this.week = week;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((terms == null) ? 0 : terms.hashCode());
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
		TopicManagerResponse other = (TopicManagerResponse) obj;
		if (terms == null) {
			if (other.terms != null)
				return false;
		} else if (!terms.equals(other.terms))
			return false;
		return true;
	}
}
