package com.tsh.library.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tsh.entities.StudentBatches;

public class FeedbackProvider {

	private TeacherTO teacher;
	private List<FeedbackCategoryTO> feedbackCategory;
	private Date feedbackDate;
	private StudentBatches studentBatch;

	public TeacherTO getTeacher() {
		return teacher;
	}

	public void setTeacher(TeacherTO teacher) {
		this.teacher = teacher;
	}

	public List<FeedbackCategoryTO> getFeedbackCategory() {
		return feedbackCategory;
	}

	public void setFeedbackCategory(List<FeedbackCategoryTO> feedbackCategory) {
		this.feedbackCategory = feedbackCategory;
	}

	public void addFeedbackCategory(FeedbackCategoryTO categoryTO) {
		if (feedbackCategory == null)
			feedbackCategory = new ArrayList<>();

		feedbackCategory.add(categoryTO);
	}

	public Date getFeedbackDate() {
		return feedbackDate;
	}

	public void setFeedbackDate(Date feedbackDate) {
		this.feedbackDate = feedbackDate;
	}

	public StudentBatches getStudentBatch() {
		return studentBatch;
	}

	public void setStudentBatch(StudentBatches studentBatch) {
		this.studentBatch = studentBatch;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((teacher == null) ? 0 : teacher.hashCode());
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
		FeedbackProvider other = (FeedbackProvider) obj;
		if (teacher == null) {
			if (other.teacher != null)
				return false;
		} else if (!teacher.equals(other.teacher))
			return false;
		return true;
	}

}
