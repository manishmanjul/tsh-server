package com.tsh.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.BatchProgress;
import com.tsh.entities.Course;
import com.tsh.entities.Student;
import com.tsh.entities.StudentBatches;
import com.tsh.entities.Teacher;
import com.tsh.entities.TopicProgress;
import com.tsh.entities.TopicStatus;
import com.tsh.entities.Topics;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.StudentFeedbackRequestTO;
import com.tsh.library.dto.StudentRequestTO;
import com.tsh.library.dto.TopicsTO;
import com.tsh.repositories.BatchProgressRepository;
import com.tsh.repositories.TopicProgressRepository;
import com.tsh.service.IGeneralService;
import com.tsh.service.IProgressService;
import com.tsh.service.IStudentService;
import com.tsh.service.ITopicService;
import com.tsh.utility.TshUtil;

@Service
public class ProgressService implements IProgressService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private BatchProgressRepository batchProgressRepo;
	@Autowired
	private TopicProgressRepository topicProgressRepo;
	@Autowired
	private IStudentService studentService;
	@Autowired
	private IGeneralService generalService;
	@Autowired
	private ITopicService topicService;

	@Override
	public TopicProgress getStudentLastTopicProgress(Student student, Course course) {
		List<TopicProgress> topicProgressList = topicProgressRepo.findByStudentAndCourseOrderByStartDateDesc(student,
				course);
		if (topicProgressList.size() > 0)
			return topicProgressList.get(0);
		else
			return null;
	}

	@Override
	public List<BatchProgress> getAllBatchProgress(BatchDetails batch) {
		return batchProgressRepo.findAllUniqueBatchProgressWithLastStatus(batch.getId());
	}

	@Override
	public BatchProgress addBatchProgress(BatchProgress batchProgress) throws TSHException {
		batchProgress = batchProgressRepo.save(batchProgress);
		if (batchProgress == null) {
			logger.error("Unable to save Batch Progress");
			throw new TSHException("Unable to save Batch Progress.");

		}
		return batchProgress;
	}

	@Override
	public BatchProgress manageCurrentBatchProgress(BatchDetails batchDetails, StudentFeedbackRequestTO inputData)
			throws TSHException {

		// Get topic status COMPLETED.
		TopicStatus completed = topicService.getTopicStatusByStatus("Completed");
		TopicStatus inProgress = topicService.getTopicStatusByStatus("In Progress");
		Course course = generalService.getCourse(inputData.getCourseId());

		Topics topic = topicService.getTopicById(inputData.getTodaysTopicId());
		if (topic == null)
			throw new TSHException("No Course found with Id : " + inputData.getCourseId());

		// Find an existing BatchProgress for today or the date of this batch's usual
		// class.
		// If a feedback is being entered randomly then do not populate the planned or
		// start date.
		logger.info("Checking for existing open batch Progress for BatchDetails : {}", batchDetails.getBatchName());
		BatchProgress batchProgress = batchProgressRepo.findByBatchDetailsAndPlannedStartDate(batchDetails,
				TshUtil.formatOz(TshUtil.getCurrentDate()));

		if (batchProgress == null) {
			batchProgress = new BatchProgress();
			batchProgress.setBatchDetails(batchDetails);
		}

		if (batchDetails.isCLassToday() || batchProgress != null) {
			batchProgress.setStartDate(TshUtil.getCurrentDate());
			batchProgress.setCanceled(false);
		}

		batchProgress.setTeacher(batchDetails.getTeacher());
		batchProgress.setTopic(topic);
		batchProgress.setCourse(course);

		if (batchProgress.getTopic().getId() == inputData.getNextTopicId()) {
			logger.info("Setting the batchProgress to In Progress as the next topic and current topic is same");
			batchProgress.setStatus(inProgress);
		} else {
			logger.info("Setting the batchProgress for this topic to Completed");
			batchProgress.setStatus(completed);
		}

		return batchProgress;
	}

	@Override
	public BatchProgress manageNextBatchProgress(BatchDetails batchDetails, StudentFeedbackRequestTO inputData)
			throws TSHException {

		TopicStatus planned = topicService.getTopicStatusByStatus("Planned");
		Course course = generalService.getCourse(inputData.getCourseId());
		Topics topic = topicService.getTopicById(inputData.getNextTopicId());
		if (topic == null)
			throw new TSHException("No Course found with Id : " + inputData.getCourseId());

		Date nextClass = TshUtil.nextClass(batchDetails.getBatch().getTimeSlot());

		BatchProgress batchProgress = batchProgressRepo.findByBatchDetailsAndPlannedStartDate(batchDetails, nextClass);

		if (batchProgress == null) {
			logger.info("Creating new Batch Progress for batchDetails : {}, Topic : {}", batchDetails.getBatchName(),
					inputData.getNextTopicDesc());
			batchProgress = new BatchProgress();
		} else {
			logger.info("Found an existing Batch Progress for batchDetails : {}, Topic : {}",
					batchDetails.getBatchName(), inputData.getNextTopicDesc());
		}

		batchProgress.setBatchDetails(batchDetails);
		batchProgress.setTopic(topic);
		batchProgress.setCourse(course);
		batchProgress.setTeacher(batchDetails.getTeacher());
		if (batchProgress.getPlannedStartDate() == null)
			batchProgress.setPlannedStartDate(TshUtil.nextClass(batchDetails.getBatch().getTimeSlot()));
		batchProgress.setStatus(planned);
		if (inputData.isPrintBooklet())
			batchProgress.printBooklet();
		else
			batchProgress.dontPrintBooklet();
		logger.info("Successfully created/updated new Batch Progress...");
		return batchProgress;
	}

	@Override
	public TopicProgress addTopicProgress(Student student, TopicProgress topicProgress) throws TSHException {
		TopicProgress topicProgress2 = topicProgressRepo.save(topicProgress);
		if (topicProgress2 == null) {
			logger.error("Unable to save Topic Progress for student : {}", student.getStudentName());
			throw new TSHException("Unable to save Topic Progress for student : " + student.getStudentName());
		}

		return topicProgress2;
	}

	@Override
	public TopicProgress manageCurrentAndNextTopicProgress(BatchDetails batchDetails,
			StudentFeedbackRequestTO inputData) throws TSHException {

		Topics currentTopic = topicService.getTopicById(inputData.getTodaysTopicId());
		Topics nextTopic = topicService.getTopicById(inputData.getNextTopicId());
		TopicStatus completed = topicService.getTopicStatusByStatus("Completed");
		TopicStatus inProgress = topicService.getTopicStatusByStatus("In Progress");
		TopicStatus planned = topicService.getTopicStatusByStatus("Planned");

		if (currentTopic == null) {
			logger.warn("Current topic : {} - not found in data store. Please check the selected topic",
					inputData.getTodaysTopicDesc());
			throw new TSHException("Current topic : " + inputData.getTodaysTopicDesc()
					+ " - not found in data store. Please check the selected topic");
		}

		for (StudentRequestTO studentTO : inputData.getStudents()) {
			logger.info("Adding topic progress for {} - ID : {}", studentTO.getName(), studentTO.getId());

			StudentBatches studentBatch = studentService.getStudentBatchesById(studentTO.getId());
			if (studentBatch == null) {
				logger.warn("No Batch record found for student {}. Skipping progress update for this student",
						studentTO.getName());
				continue;
			}

			// Is there a topic already in progress for the current topic selected. If yes
			// find that topic
			TopicProgress topicProgress = topicProgressRepo.findByStudentAndCourseAndTopicAndStatusNot(
					studentBatch.getStudent(), batchDetails.getCourse(), currentTopic, completed);
			if (topicProgress == null) {
				topicProgress = new TopicProgress();
			}

			topicProgress.setStudent(studentBatch.getStudent());
			topicProgress.setCourse(batchDetails.getCourse());
			topicProgress.setTopic(currentTopic);
			if (topicProgress.getStartDate() == null)
				topicProgress.setStartDate(TshUtil.getCurrentDate());

			// If the next topic and current topic are same. It means the same topic is
			// expected to continue next week so set the status as in progress
			// or else mark the current topic completed.
			if (inputData.getTodaysTopicId() == inputData.getNextTopicId())
				topicProgress.setStatus(inProgress);
			else {
				topicProgress.setEndDate(TshUtil.getCurrentDate());
				topicProgress.setStatus(completed);
			}

			topicProgress = this.addTopicProgress(studentBatch.getStudent(), topicProgress); // Save the current topic
																								// progress.

			if (topicProgress == null) {
				logger.warn("Could not save topic progress for topic : {} - Topic SKIPPED -",
						currentTopic.getDescription());
			} else {
				logger.info("Saved topic progress for topic : {}", currentTopic.getDescription());
			}

			// Now handle the topic for next batch.
			if (inputData.getTodaysTopicId() == inputData.getNextTopicId()) // Both topic are same. no need to add
																			// another topic progress for same topic.
				continue;

			logger.info("Adding Topic Progress for next class. Topic : {}", nextTopic.getDescription());

			TopicProgress nextTopicProgress = topicProgressRepo.findByStudentAndCourseAndTopicAndStatusNot(
					studentBatch.getStudent(), batchDetails.getCourse(), nextTopic, completed);
			if (nextTopicProgress == null) {
				nextTopicProgress = new TopicProgress();
			}

			// For the next weeks topic. Only set the planned date. Do not set the start
			// date as it is just planned for now.
			nextTopicProgress.setStudent(studentBatch.getStudent());
			nextTopicProgress.setCourse(batchDetails.getCourse());
			nextTopicProgress.setTopic(nextTopic);
			if (nextTopicProgress.getPlannedStartDate() == null)
				nextTopicProgress.setPlannedStartDate(TshUtil.nextClass(batchDetails.getBatch().getTimeSlot()));
			nextTopicProgress.setStatus(planned);

			nextTopicProgress = this.addTopicProgress(studentBatch.getStudent(), nextTopicProgress);

			if (nextTopicProgress == null) {
				logger.warn("Unable to save Topic Progress for : {} - Topic SKIPPED - ", nextTopic.getDescription());
			} else {
				logger.info("All Topic Progress added/Updated for : {} - ID : {}", studentTO.getName(),
						studentTO.getId());
			}
		}
		return null;
	}

	@Override
	public List<BatchProgress> getAllBatchProgressForStatus(BatchDetails batchDetails, TopicStatus status) {
		return batchProgressRepo.findByBatchDetailsAndStatusOrderByStartDateDesc(batchDetails, status);
	}

	@Override
	public List<TopicsTO> getAllTopicsProgress(Student student, Course course) {
		ModelMapper mapper = new ModelMapper();
		List<TopicProgress> topicProgressList = topicProgressRepo
				.findByStudentAndCourseCategoryOrderByStartDateDesc(student, course.getCategory());
		logger.info("{} topics found in complete state.", topicProgressList.size());

		List<TopicsTO> topicTOList = topicProgressList.stream().map(tp -> {
			TopicsTO topicTO = mapper.map(tp.getTopic(), TopicsTO.class);
			return topicTO;
		}).collect(Collectors.toList());

		return topicTOList;
	}

	public BatchProgress getBatchProgressAsOfToday(BatchDetails batch) throws TSHException {
		return batchProgressRepo.findByBatchDetailsAndPlannedStartDate(batch,
				TshUtil.formatOz(TshUtil.getCurrentDate()));
	}

	@Override
	public List<BatchProgress> getAllBatchProgressPlannedOn(Date plannedStartDate) {
		return batchProgressRepo.findAllByPlannedStartDate(plannedStartDate);
	}

	@Override
	public List<BatchProgress> getAllBatchProgressPlannedOn(String plannedStartDate) throws ParseException {
		return getAllBatchProgressPlannedOn(TshUtil.toDate(plannedStartDate));
	}

	@Override
	public BatchProgress getNextPlannedBatchProgress(BatchDetails batch) throws TSHException {
		List<BatchProgress> progressList = batchProgressRepo
				.findByBatchDetailsAndPlannedStartDateGreaterThanOrderByPlannedStartDate(batch,
						TshUtil.formatOz(TshUtil.getCurrentDate()));

		if (progressList.isEmpty()) {
			return null;
		} else {
			return progressList.get(0);
		}
	}

	@Override
	public void saveAllBatchProgress(List<BatchProgress> batches) {
		batchProgressRepo.saveAll(batches);

	}

	@Override
	public List<BatchProgress> getAllBatchProgressTodayAndAfter() throws TSHException {
		Date today = TshUtil.getCurrentDate();
		return batchProgressRepo.findAllMaxRecordsWithPlannedStartDateOrStartDateGreaterThan(today);
	}

	@Override
	public List<BatchProgress> getAllBatchProgressTodayAndAfter(Teacher teacher) throws TSHException {
		Date today = TshUtil.getCurrentDate();
		return batchProgressRepo.findMaxRecordsForTeacherWithPlannedStartDateOrStartDateGreaterThan(today,
				teacher.getId());
	}

}
