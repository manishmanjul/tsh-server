package com.tsh.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.BatchProgress;
import com.tsh.entities.Course;
import com.tsh.entities.Student;
import com.tsh.entities.StudentBatches;
import com.tsh.entities.TopicProgress;
import com.tsh.entities.TopicStatus;
import com.tsh.entities.Topics;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.StudentFeedbackResponseTO;
import com.tsh.library.dto.StudentResponseTO;
import com.tsh.repositories.BatchProgressRepository;
import com.tsh.repositories.CourseRepository;
import com.tsh.repositories.StudentBatchesRepository;
import com.tsh.repositories.TopicProgressRepository;
import com.tsh.repositories.TopicStatusRepository;
import com.tsh.repositories.TopicsRepository;
import com.tsh.service.IProgressService;
import com.tsh.utility.TshUtil;

@Service
public class ProgressService implements IProgressService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private BatchProgressRepository batchProgressRepo;
	@Autowired
	private TopicProgressRepository topicProgressRepo;	
	@Autowired
	private TopicStatusRepository topicStatusRepo;
	@Autowired
	private CourseRepository courseRepo;
	@Autowired
	private TopicsRepository topicRepo;
	@Autowired
	private StudentBatchesRepository studentBatchRepo;
	
	@Override
	public Topics getPreviousTopic(BatchDetails batchDetails) throws ParseException, TSHException {
		
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, -7);
		
		Date startOfWeek = TshUtil.getFirstDayOfWeek(now.getTime());
		Date endOfWeek = TshUtil.getLastDayOfWeek(now.getTime());
		return getBatchProgressForWeek(batchDetails, startOfWeek, endOfWeek);
	}

	@Override
	public Topics getCurrentTopic(BatchDetails batchDetails) throws ParseException, TSHException {
				
		Date startOfWeek = TshUtil.getFirstDayOfCurrentWeek();
		Date endOfWeek = TshUtil.getLastDayOfCurrentWeek();
		return getBatchProgressForWeek(batchDetails, startOfWeek, endOfWeek);
	}

	@Override
	public Topics getNextTopic(BatchDetails batchDetails) throws ParseException, TSHException {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, 7);
		
		Date startOfWeek = TshUtil.getFirstDayOfWeek(now.getTime());
		Date endOfWeek = TshUtil.getLastDayOfWeek(now.getTime());
		return getBatchProgressForWeek(batchDetails, startOfWeek, endOfWeek);
	}

	private Topics getBatchProgressForWeek(BatchDetails batchDetails, Date startDate, Date endDate) throws TSHException{
		Topics topic = new Topics();
		BatchProgress currentBatchProgress = null;
		topic.setDescription("Data not available or First batch.");
		
		TopicStatus inProgress = topicStatusRepo.findByStatus("In Progress");
		List<BatchProgress> inProgressBatchProgress = batchProgressRepo.findByBatchDetailsAndStatusOrderByStartDateDesc(batchDetails, inProgress);
		if(inProgressBatchProgress.size() > 0)
			currentBatchProgress = inProgressBatchProgress.get(0);
		
		TopicStatus planned = topicStatusRepo.findByStatus("Planned");		
		List<BatchProgress> plannedBatchProgress = batchProgressRepo.findByBatchDetailsAndStatusOrderByPlannedStartDateDesc(batchDetails, planned);
		for(BatchProgress progress : plannedBatchProgress) {
			Date plannedDate = TshUtil.format(progress.getPlannedStartDate());
			if((plannedDate.equals(TshUtil.format(startDate)) || plannedDate.after(TshUtil.format(startDate))) & (plannedDate.equals(TshUtil.format(endDate)) || plannedDate.before(TshUtil.format(endDate))))
				currentBatchProgress = progress;				
		}
		
		if(currentBatchProgress != null)
			topic = currentBatchProgress.getTopic();
		
		return topic;
	}
	
	private Topics getPlannedBatchProgressForWeek(BatchDetails batchDetails, Date startDate, Date endDate) {
		Topics topic = new Topics();
		topic.setDescription("Data not available or First batch.");
		
		BatchProgress batchProgress = batchProgressRepo.findByBatchDetailsAndPlannedStartDateBetween(batchDetails, startDate, endDate);
		if(batchProgress != null)
			topic = batchProgress.getTopic();
		
		return topic;
	}

	@Override
	public TopicProgress getStudentLastTopicProgress(Student student, Course course) {
		List<TopicProgress> topicProgressList = topicProgressRepo.findByStudentAndCourseOrderByStartDateDesc(student, course);
		if(topicProgressList.size() > 0)
			return topicProgressList.get(0);
		else 
			return null;
	}

	@Override
	public List<BatchProgress> getAllBatchProgress(BatchDetails batch) {
		return batchProgressRepo.findAllByBatchDetails(batch);
	}

	@Override
	public BatchProgress addBatchProgress(BatchProgress batchProgress) throws TSHException{
		batchProgress = batchProgressRepo.save(batchProgress);
		if(batchProgress == null) {
			logger.error("Unable to save Batch Progress");
			throw new TSHException("Unable to save Batch Progress.");
			
		}
		return batchProgress;
	}

	@Override
	public BatchProgress manageCurrentBatchProgress(BatchDetails batchDetails, StudentFeedbackResponseTO inputData) throws TSHException {
		
		// Get topic status COMPLETED.
		TopicStatus completed = topicStatusRepo.findByStatus("Completed");
		TopicStatus inProgress = topicStatusRepo.findByStatus("In Progress");
		Course course = courseRepo.findById(inputData.getCourseId()).orElseThrow(() -> new TSHException("No Course found with Id : " + inputData.getCourseId()));
		Topics topic = topicRepo.findById(inputData.getTodaysTopicId()).orElseThrow(() -> new TSHException("Noi Course found with Id : " + inputData.getCourseId()));
		
		// Find a batch progress record for this batchDetails id where course, topic matches and also the status should not be completed
		logger.info("Checking for existing open batch Progress for BatchDetails : {}", batchDetails.getBatchName());
		BatchProgress batchProgress = batchProgressRepo.findByBatchDetailsAndTopicAndCourseAndStatusNot(batchDetails, topic, course, completed);
		
		// If a batch progress is found. update this record or else create a new Batch Progress.
		// If the next topic ID if the same as current. It means this batch progress remain in progress else mark completed.
		if(batchProgress != null) {			
			logger.info("Found an existing batch progress for this batch. startDate : {}, Status : {}", batchProgress.getStartDate(), batchProgress.getStatus().getStatus());
			if(batchProgress.getStartDate() == null)
				batchProgress.setStartDate(TshUtil.getCurrentDate());
			
			if(batchProgress.getTopic().getId() == inputData.getNextTopicId()) {				
				logger.info("Setting the batchProgress to In Progress as the next topic and current topic is same");
				batchProgress.setStatus(inProgress);	
				if(inputData.isPrintBooklet()) batchProgress.printBooklet(); else batchProgress.dontPrintBooklet();
			}else {
				logger.info("Setting the batchProgress for this topic to Completed");
				batchProgress.setEndDate(TshUtil.getCurrentDate());
				batchProgress.dontPrintBooklet();
				batchProgress.setStatus(completed);
			}
			
			logger.info("Updated the existing batch progress. Status : {}", batchProgress.getStatus().getStatus());
		}else {
			logger.info("No existing batch Progress found for this topic : {}",topic.getDescription());
			logger.info("Creating new Batch Progress...");
			batchProgress = new BatchProgress();
			batchProgress.setBatchDetails(batchDetails);
			batchProgress.setTopic(topic);
			batchProgress.setCourse(course);
			batchProgress.setStartDate(TshUtil.getCurrentDate());
			batchProgress.setPlannedStartDate(TshUtil.getCurrentDate());
			
			if(inputData.isPrintBooklet()) batchProgress.printBooklet(); else batchProgress.dontPrintBooklet();
			
			if(batchProgress.getTopic().getId() == inputData.getNextTopicId()) {				
				logger.info("Setting the batchProgress to In Progress as the next topic and current topic is same");
				batchProgress.setStatus(inProgress);				
			}else {
				logger.info("Setting the batchProgress for this topic to Completed");
				batchProgress.setEndDate(TshUtil.getCurrentDate());
				batchProgress.setStatus(completed);
			}			
		}
		
		return batchProgress;
	}

	@Override
	public BatchProgress manageNextBatchProgress(BatchDetails batchDetails, StudentFeedbackResponseTO inputData)
			throws TSHException {
		
		logger.info("Evaluating need for new next batch progress");
		// If the current topic is same as next topic, then the BatchProgress for current topic is already in progress.
		// No need to create another batch progress for the same topic
		if(inputData.getTodaysTopicId() == inputData.getNextTopicId()) {
			logger.info("The current topic and next topic is same. New batch progress creation will be skipped.");
			return null;
		}
		
		
		TopicStatus planned = topicStatusRepo.findByStatus("Planned");
		TopicStatus completed = topicStatusRepo.findByStatus("Completed");
		Course course = courseRepo.findById(inputData.getCourseId()).orElseThrow(() -> new TSHException("No Course found with Id : " + inputData.getCourseId()));
		Topics topic = topicRepo.findById(inputData.getNextTopicId()).orElseThrow(() -> new TSHException("No Course found with Id : " + inputData.getCourseId()));
		BatchProgress batchProgress = batchProgressRepo.findByBatchDetailsAndTopicAndCourseAndStatusNot(batchDetails, topic, course, completed);
		
		if(batchProgress == null) {
			logger.info("Creating new Batch Progress for batchDetails : {}, Topic : {}", batchDetails.getBatchName(), inputData.getNextTopicDesc());
			batchProgress = new BatchProgress();
		}else {
			logger.info("Found an existing Batch Progress for batchDetails : {}, Topic : {}", batchDetails.getBatchName(), inputData.getNextTopicDesc());
		}
		
		batchProgress.setBatchDetails(batchDetails);
		batchProgress.setTopic(topic);
		batchProgress.setCourse(course);		
		if(batchProgress.getPlannedStartDate() == null) batchProgress.setPlannedStartDate(TshUtil.nextClass(batchDetails.getBatch().getTimeSlot()));
		batchProgress.setStatus(planned);
		if(inputData.isPrintBooklet()) batchProgress.printBooklet(); else batchProgress.dontPrintBooklet();
		logger.info("Successfully created/updated new Batch Progress...");
		return batchProgress;
	}

	@Override
	public TopicProgress addTopicProgress(Student student, TopicProgress topicProgress) throws TSHException{
		TopicProgress topicProgress2 = topicProgressRepo.save(topicProgress);
		if(topicProgress2 == null) {
			logger.error("Unable to save Topic Progress for student : {}", student.getStudentName());
			throw new TSHException("Unable to save Topic Progress for student : " + student.getStudentName());
		}
			
		return topicProgress2;
	}

	@Override
	public TopicProgress manageCurrentAndNextTopicProgress(BatchDetails batchDetails, StudentFeedbackResponseTO inputData) throws TSHException{

		Topics currentTopic = topicRepo.findById(inputData.getTodaysTopicId()).orElse(null);
		Topics nextTopic = topicRepo.findById(inputData.getNextTopicId()).orElse(null);
		TopicStatus completed = topicStatusRepo.findByStatus("Completed");
		TopicStatus inProgress = topicStatusRepo.findByStatus("In Progress");
		TopicStatus planned = topicStatusRepo.findByStatus("Planned");
		
		if(currentTopic == null) {
			logger.warn("Current topic : {} - not found in data store. Please check the selected topic", inputData.getTodaysTopicDesc());
			throw new TSHException("Current topic : " + inputData.getTodaysTopicDesc() + " - not found in data store. Please check the selected topic");
		}
		
		for(StudentResponseTO studentTO : inputData.getStudents()) {
			logger.info("Adding topic progress for {}", studentTO.getName());
		
			StudentBatches studentBatch = studentBatchRepo.findById(studentTO.getId()).orElse(null);
			if(studentBatch == null) {
				logger.warn("No Batch record found for student {}. Skipping progress update for this student", studentTO.getName());
				continue;
			}
			
			
			TopicProgress topicProgress = topicProgressRepo.findByStudentAndCourseAndTopicAndStatusNot(studentBatch.getStudent(), batchDetails.getCourse(), currentTopic, completed);
			if(topicProgress == null) {
				topicProgress = new TopicProgress();
			}
			
			topicProgress.setStudent(studentBatch.getStudent());
			topicProgress.setCourse(batchDetails.getCourse());
			topicProgress.setTopic(currentTopic);
			if(topicProgress.getStartDate() == null) topicProgress.setStartDate(TshUtil.getCurrentDate());
			if(inputData.getTodaysTopicId() == inputData.getNextTopicId()) topicProgress.setStatus(inProgress); else {
				topicProgress.setEndDate(TshUtil.getCurrentDate());
				topicProgress.setStatus(completed);
			}
			
			topicProgress = this.addTopicProgress(studentBatch.getStudent(), topicProgress); 	//Save the current topic progress.
			
			if(topicProgress == null) {
				logger.warn("Could not save topic progress for topic : {} - Topic SKIPPED -", currentTopic.getDescription());
			}else {
				logger.info("Saved topic progress for topic : {}", currentTopic.getDescription());
			}
			
			// Now handle the topic for next batch.
			if(inputData.getTodaysTopicId() == inputData.getNextTopicId()) 	//Both topic are same. no need to add another topic progress for same topic. 
				continue;
			
			logger.info("Adding Topic Progress for next class. Topic : {}", nextTopic.getDescription());
			
			TopicProgress nextTopicProgress = topicProgressRepo.findByStudentAndCourseAndTopicAndStatusNot(studentBatch.getStudent(), batchDetails.getCourse(), nextTopic, completed);
			if(nextTopicProgress == null) {
				nextTopicProgress = new TopicProgress();
			}
			
			nextTopicProgress.setStudent(studentBatch.getStudent());
			nextTopicProgress.setCourse(batchDetails.getCourse());
			nextTopicProgress.setTopic(nextTopic);
			if(nextTopicProgress.getPlannedStartDate() == null) nextTopicProgress.setPlannedStartDate(TshUtil.nextClass(batchDetails.getBatch().getTimeSlot()));
			nextTopicProgress.setStatus(planned);
			
			nextTopicProgress = this.addTopicProgress(studentBatch.getStudent(), nextTopicProgress);
			
			if(nextTopicProgress == null) {
				logger.warn("Unable to save Topic Progress for : {} - Topic SKIPPED - ", nextTopic.getDescription());
			} else {
				logger.info("All Topic Progress added/Updated for : {}", studentTO.getName());
			}
		}
		return null;
	}

}
