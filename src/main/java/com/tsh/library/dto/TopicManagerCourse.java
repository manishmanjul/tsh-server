package com.tsh.library.dto;

import java.util.ArrayList;
import java.util.List;

public class TopicManagerCourse {

	private String name;
	private List<TopicManagerSubject> subjects;
	
	public TopicManagerCourse(String name) {
		super();
		this.name = name;
	}
	public TopicManagerCourse() {
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<TopicManagerSubject> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<TopicManagerSubject> subjects) {
		this.subjects = subjects;
	}
	
	public void addSubject(TopicManagerSubject sub) {
		if(subjects == null) subjects = new ArrayList<>();
		subjects.add(sub);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		TopicManagerCourse other = (TopicManagerCourse) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "TopicManagerCourse [name=" + name + "]";
	}
}
