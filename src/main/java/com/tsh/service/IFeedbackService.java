package com.tsh.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.Feedback;
import com.tsh.entities.FeedbackCategory;
import com.tsh.entities.StudentBatches;
import com.tsh.entities.StudentFeedback;
import com.tsh.entities.Topics;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.StudentFeedbackRequestTO;

@Service
public interface IFeedbackService {

	Map<String, List<Topics>> validateAndSync(List<Topics> topicList) throws TSHException;

	Feedback getFeedbackByShortDescription(String shortDescription);

	List<StudentFeedback> saveAllStudentFeedbacks(List<StudentFeedback> feedbacks);

	List<StudentFeedback> getAllFeedbacks(StudentBatches studentBatches, Topics topic);

	public Feedback getFeedbackById(int feedbackId);

	List<FeedbackCategory> getAllActiveFeedbackCategories();

	Map<String, String> getDummyFeedbackMap();

	public void processStudentFeedback(BatchDetails batchDetails, StudentFeedbackRequestTO inputData)
			throws TSHException;

	public StudentFeedback saveFeedback(StudentFeedback feedback) throws TSHException;

}
