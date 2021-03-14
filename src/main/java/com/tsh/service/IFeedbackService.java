package com.tsh.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.Feedback;
import com.tsh.entities.FeedbackCategory;
import com.tsh.entities.StudentBatches;
import com.tsh.entities.StudentFeedback;
import com.tsh.entities.Teacher;
import com.tsh.entities.TopicProgress;
import com.tsh.entities.Topics;
import com.tsh.entities.User;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.DeleteFeedbackRequest;
import com.tsh.library.dto.FeedbackCategoryTO;
import com.tsh.library.dto.FeedbackTO;
import com.tsh.library.dto.StudentFeedbackRequestTO;
import com.tsh.library.dto.TopicsTO;
import com.tsh.library.dto.UpdateFeedbackRequest;

@Service
public interface IFeedbackService {

	Map<String, List<Topics>> validateAndSync(List<Topics> topicList) throws TSHException;

	Feedback getFeedbackByShortDescription(String shortDescription);

	List<StudentFeedback> saveAllStudentFeedbacks(List<StudentFeedback> feedbacks);

	List<StudentFeedback> getStudentFeedbackByBatchTopicAndTeacher(StudentBatches studentBatches, Topics topic,
			Teacher teacher);

	List<StudentFeedback> getAllFeedbacks(StudentBatches studentBatches, Topics topic);

	List<StudentFeedback> getAllFeedbacks(TopicProgress topicProgress, StudentBatches studentBatches);

	public Feedback getFeedbackById(int feedbackId);

	List<FeedbackCategory> getAllActiveFeedbackCategories(int grade);

	List<FeedbackCategory> getAllFeedbackCategories(int grade);

	public List<FeedbackCategoryTO> getEmptyFeedback(int grade);

	public void processStudentFeedback(BatchDetails batchDetails, StudentFeedbackRequestTO inputData, User loggedinUser,
			List<TopicProgress> currTopicProgress) throws TSHException;

	public StudentFeedback saveFeedback(StudentFeedback feedback) throws TSHException;

	public void updateAndAddStudentFeedback(UpdateFeedbackRequest request, StudentBatches studentBatches,
			User loggedInUser) throws TSHException;

	public List<TopicsTO> populateAllFeedbacksWithProviders(List<TopicsTO> topicTOList, StudentBatches studentBatches);

	public void deleteFeedback(DeleteFeedbackRequest request, User loggedinUser);

	public FeedbackCategoryTO addFeedbackCategory(FeedbackCategoryTO categoryTO);

	public FeedbackCategoryTO findFeedbackCategoryById(int id);

	public FeedbackCategoryTO toggleFeedbackCategoryState(FeedbackCategoryTO category) throws TSHException;

	public FeedbackTO toggleFeedbackItemState(FeedbackTO feedback) throws TSHException;

	public FeedbackTO addFeedbackItem(FeedbackTO feedbackTO) throws TSHException;

	public void clearFeedbacks(TopicProgress topicProgress, User loggedInUser, StudentBatches studentBatches);

}
