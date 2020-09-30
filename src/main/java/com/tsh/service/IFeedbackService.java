package com.tsh.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.Course;
import com.tsh.entities.Feedback;
import com.tsh.entities.FeedbackCategory;
import com.tsh.entities.Grades;
import com.tsh.entities.StudentBatches;
import com.tsh.entities.StudentFeedback;
import com.tsh.entities.TopicProgress;
import com.tsh.entities.TopicStatus;
import com.tsh.entities.Topics;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.StudentFeedbackResponseTO;

@Service
public interface IFeedbackService {

	Topics getTopicByMappedId(String mappedId) throws TSHException;
	
	public Topics getTopicById(int topicId);
	
	List<Topics> saveAllTopics(List<Topics> topicList);
	
	Map<String, List<Topics>> validateAndSync(List<Topics> topicList) throws TSHException;
	
	TopicStatus getTopicStatusByStatus(String status);
	
	Feedback getFeedbackByShortDescription(String shortDescription) ;
	
	List<StudentFeedback> saveAllStudentFeedbacks(List<StudentFeedback> feedbacks);
	
	List<TopicProgress> saveAllTopicProgress(List<TopicProgress> progressList);
	
	List<Topics> getAllActiveTopicsForCourseAndGrade(Course course, Grades grade);

	List<StudentFeedback> getAllFeedbacks(StudentBatches studentBatches, Topics topic);
	
	public Feedback getFeedbackById(int feedbackId);
	
	List<FeedbackCategory> getAllActiveFeedbackCategories();
	
	Map<String, String> getDummyFeedbackMap();
	
	public void processStudentFeedback(BatchDetails batchDetails, StudentFeedbackResponseTO inputData) throws TSHException; 
	
	public StudentFeedback saveFeedback(StudentFeedback feedback) throws TSHException;
	
}

