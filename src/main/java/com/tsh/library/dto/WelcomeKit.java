package com.tsh.library.dto;

import java.util.List;

public class WelcomeKit {
	
	private UserTO user;
	private List<FeaturesTO> features;
	private TeacherTO teacher;

	public List<FeaturesTO> getFeatures() {
		return features;
	}

	public void setFeatures(List<FeaturesTO> features) {
		this.features = features;
	}

	public UserTO getUser() {
		return user;
	}

	public void setUser(UserTO user) {
		this.user = user;
	}

	public TeacherTO getTeacher() {
		return teacher;
	}

	public void setTeacher(TeacherTO teacher) {
		this.teacher = teacher;
	}	
}
