package com.tsh.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.BatchProgress;
import com.tsh.entities.Course;
import com.tsh.entities.Student;
import com.tsh.entities.TopicProgress;
import com.tsh.entities.Topics;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.StudentFeedbackResponseTO;

@Service
public interface IProgressService extends TshService{
	
	public Topics getPreviousTopic(BatchDetails batchDetails) throws ParseException, TSHException;
	
	public Topics getCurrentTopic(BatchDetails batchDetails) throws ParseException, TSHException;
	
	public Topics getNextTopic(BatchDetails batchDetails) throws ParseException, TSHException;
	
	public TopicProgress getStudentLastTopicProgress(Student student, Course course);
	
	public List<BatchProgress> getAllBatchProgress(BatchDetails batch);
	
	public BatchProgress addBatchProgress(BatchProgress batchProgress) throws TSHException;
	
	public BatchProgress manageCurrentBatchProgress(BatchDetails batchDetails, StudentFeedbackResponseTO inputData)
			throws TSHException;
	
	public BatchProgress manageNextBatchProgress(BatchDetails batchDetails, StudentFeedbackResponseTO inputData) throws TSHException;
	
	public TopicProgress addTopicProgress(Student student, TopicProgress topicProgress) throws TSHException;
	
	public TopicProgress manageCurrentAndNextTopicProgress(BatchDetails batchDetails, StudentFeedbackResponseTO inputData) throws TSHException;
}
