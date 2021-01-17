package com.tsh.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.BatchProgress;
import com.tsh.entities.Course;
import com.tsh.entities.Student;
import com.tsh.entities.TopicProgress;
import com.tsh.entities.TopicStatus;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.StudentFeedbackRequestTO;
import com.tsh.library.dto.TopicsTO;

@Service
public interface IProgressService extends TshService {

	List<BatchProgress> getAllBatchProgressForStatus(BatchDetails batchDetails, TopicStatus status);

	public TopicProgress getStudentLastTopicProgress(Student student, Course course);

	public List<BatchProgress> getAllBatchProgress(BatchDetails batch);

	public BatchProgress getBatchProgressAsOfToday(BatchDetails batch) throws TSHException;

	public BatchProgress getNextPlannedBatchProgress(BatchDetails batch) throws TSHException;

	public BatchProgress addBatchProgress(BatchProgress batchProgress) throws TSHException;

	public void saveAllBatchProgress(List<BatchProgress> batches);

	public BatchProgress manageCurrentBatchProgress(BatchDetails batchDetails, StudentFeedbackRequestTO inputData)
			throws TSHException;

	public BatchProgress manageNextBatchProgress(BatchDetails batchDetails, StudentFeedbackRequestTO inputData)
			throws TSHException;

	public TopicProgress addTopicProgress(Student student, TopicProgress topicProgress) throws TSHException;

	public TopicProgress manageCurrentAndNextTopicProgress(BatchDetails batchDetails,
			StudentFeedbackRequestTO inputData) throws TSHException;

	public List<TopicsTO> getAllTopicsProgress(Student student, Course course);
}
