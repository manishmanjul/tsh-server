package com.tsh.library.dto;

import java.util.List;

public class TopicGenerationRequest {

	private List<TermTO> termResponse;
	private List<GradeTO> gradeResponse;
	private List<CourseGenTO> courseResponse;
	
	public List<TermTO> getTermResponse() {
		return termResponse;
	}
	public void setTermResponse(List<TermTO> termResponse) {
		this.termResponse = termResponse;
	}
	public List<GradeTO> getGradeResponse() {
		return gradeResponse;
	}
	public void setGradeResponse(List<GradeTO> gradeResponse) {
		this.gradeResponse = gradeResponse;
	}
	public List<CourseGenTO> getCourseResponse() {
		return courseResponse;
	}
	public void setCourseResponse(List<CourseGenTO> courseResponse) {
		this.courseResponse = courseResponse;
	}
	
	
}
