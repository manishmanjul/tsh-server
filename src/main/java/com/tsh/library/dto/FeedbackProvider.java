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
	private UserTO userTO;
	private TopicProgressTO topicProgress;
	private UserTO updatedBy;
	private String updatedOn;

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

	public UserTO getUserTO() {
		return userTO;
	}

	public void setUserTO(UserTO userTO) {
		this.userTO = userTO;
	}

	public TopicProgressTO getTopicProgress() {
		return topicProgress;
	}

	public void setTopicProgress(TopicProgressTO topicProgress) {
		this.topicProgress = topicProgress;
	}

	public UserTO getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(UserTO updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((topicProgress == null) ? 0 : topicProgress.hashCode());
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
		if (topicProgress == null) {
			if (other.topicProgress != null)
				return false;
		} else if (!topicProgress.equals(other.topicProgress))
			return false;
		return true;
	}

}
