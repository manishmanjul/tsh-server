package com.tsh.service.impl;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tsh.entities.Batch;
import com.tsh.entities.BatchDetails;
import com.tsh.entities.BatchProgress;
import com.tsh.entities.Course;
import com.tsh.entities.Grades;
import com.tsh.entities.StudentBatches;
import com.tsh.entities.StudentFeedback;
import com.tsh.entities.Teacher;
import com.tsh.entities.Term;
import com.tsh.entities.TimeSlot;
import com.tsh.entities.TopicProgress;
import com.tsh.entities.TrainingType;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.FeedbackCategoryTO;
import com.tsh.library.dto.FeedbackTO;
import com.tsh.library.dto.ScheduleTO;
import com.tsh.library.dto.StudentTO;
import com.tsh.library.dto.TopicsTO;
import com.tsh.repositories.BatchDetailsRepository;
import com.tsh.repositories.BatchRepository;
import com.tsh.service.IBatchService;
import com.tsh.service.IFeedbackService;
import com.tsh.service.IProgressService;
import com.tsh.service.IStudentService;
import com.tsh.service.ITopicService;
import com.tsh.utility.TshUtil;

@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BatchService implements IBatchService {

	private List<BatchDetails> batchDetails;
	private List<Batch> batches;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public BatchService() {
	}

	@Autowired
	private BatchDetailsRepository batchDetailRepo;
	@Autowired
	private BatchRepository batchRepo;
	@Autowired
	private IStudentService studentService;
	@Autowired
	private IFeedbackService feedbackService;
	@Autowired
	private IProgressService progressService;
	@Autowired
	private ITopicService topicService;

	public Optional<BatchDetails> getBatchDetails(Teacher teacher, Grades grade, Course course, TimeSlot timeslot,
			TrainingType trainingType, Term term) {
		this.batchDetails = this.batchDetailRepo.findByActive(true);
		return batchDetails.stream()
				.filter(bd -> bd.getTeacher().equals(teacher) && bd.getGrade().equals(grade)
						&& bd.getCourse().equals(course) && bd.getBatch().getTimeSlot().equals(timeslot)
						&& bd.getTerm().equals(term))
				.findFirst();
	}

	public Optional<Batch> getBatch(TimeSlot timeSlot) {
		this.batches = batchRepo.findAll();
		return batches.stream().filter(b -> b.getTimeSlot().equals(timeSlot)).findFirst();
	}

	public List<BatchDetails> findAllOrphanBatchDetails() {
		List<BatchDetails> batchDetails = null;
		batchDetails = batchDetailRepo.findAllOrphans();
		return batchDetails;
	}

	@Transactional
	@Override
	public Batch saveBatch(Batch batch) {
		return batchRepo.save(batch);
	}

	@Transactional
	public void saveBatch(List<Batch> batches) {
		batchRepo.saveAll(batches);
	}

	@Transactional
	@Override
	public BatchDetails saveBatchDetails(BatchDetails batcheDetail) {
		return batchDetailRepo.save(batcheDetail);
	}

	@Transactional
	public void saveBatchDetails(List<BatchDetails> batcheDetails) {
		batchDetailRepo.saveAll(batcheDetails);
	}

	@Override
	public List<ScheduleTO> getSchedulesFor(Teacher teacher) throws ParseException, TSHException {

		List<ScheduleTO> schedules = new ArrayList<>();

		logger.info("Finding all batches for teacher : {}", teacher.getTeacherName());

		List<BatchDetails> batches = batchDetailRepo.findAllByTeacherAndActive(teacher, true);

		for (BatchDetails batch : batches) {
			schedules.add(getBatchDetails(batch));
		}
		logger.info("{} batches fetched. Returning result", schedules.size());
		return schedules;
	}

	@Override
	public ScheduleTO getBatchDetails(BatchDetails batchDetails) throws TSHException, ParseException {
		ScheduleTO schedule = new ScheduleTO();
		schedule.setKey(batchDetails.getId() + "");
		schedule.setCourseId(batchDetails.getCourse().getId());
		schedule.setCourse(batchDetails.getCourse().getShortDescription());
		schedule.setCourseDescription(batchDetails.getCourse().getDescription());
		schedule.setDay(DayOfWeek.of(batchDetails.getBatch().getTimeSlot().getWeekday()).minus(1).toString());
		schedule.setStartTime(batchDetails.getBatch().getTimeSlot().getStartTime().toString());
		schedule.setEndTime(batchDetails.getBatch().getTimeSlot().getEndTime().toString());
		schedule.setGrade(batchDetails.getGrade().getGrade());
		schedule.setTeacherName(batchDetails.getTeacher().getTeacherName());
		schedule.setTerm("4");
		schedule.setAttendies(fetchAllStudentsData(batchDetails));
		schedule.setTopics(fetchAllTopicsForCourse(batchDetails));
		schedule.setCurrentTopic(getCurrentTopicOfBatch(batchDetails));
		schedule.setNextTopic(getNextTopicOfBatch(batchDetails));

		return schedule;
	}

	private List<StudentTO> fetchAllStudentsData(BatchDetails batch) {
		List<StudentBatches> students = studentService.getStudentBatches(batch);
		List<StudentTO> studentList = new ArrayList<>();

		for (StudentBatches studentBatch : students) {
			StudentTO studentTO = new StudentTO();
			studentTO.setId(studentBatch.getId());
			studentTO.setName(studentBatch.getStudent().getStudentName());
			studentTO.setGrade(studentBatch.getStudent().getGrade().getGrade() + "");
			studentTO.setCourse(studentBatch.getCourse().getDescription());

			// Fetch the topic progress for last week based on startDate. This might return
			// the the topic that might have been started last week but waS NOT COMPLETED.
			// Meaning -- A topic that was kept in progress would also be returned. Which is
			// the correct behavior.
			TopicProgress topicProgress = progressService.getStudentLastTopicProgress(studentBatch.getStudent(),
					studentBatch.getCourse());
			if (topicProgress != null) {
				studentTO.setPreviousTopic(topicProgress.getTopic().getTopicFullName());

				List<StudentFeedback> feedbacks = feedbackService.getAllFeedbacks(studentBatch,
						topicProgress.getTopic());
				ModelMapper mapper = new ModelMapper();

				Teacher feedbackProvider = null;
				Calendar feedbackDate = Calendar.getInstance();

				if (feedbacks.size() > 0) { // Feedbacks are sorted in descending order. The first one is the latest
											// one.
					feedbackProvider = feedbacks.get(0).getTeacher();
					feedbackDate.setTime(feedbacks.get(0).getFeedbackDate());
				}

				// There can be multiple feedbacks provided for a topic by different teacher or
				// by same teacher.
				// Get only the latest feedbacks and provided by just one teacher.
				for (StudentFeedback studFeedback : feedbacks) {
					Calendar fDate = Calendar.getInstance();
					fDate.setTime(studFeedback.getFeedbackDate());
					if (studFeedback.getTeacher().equals(feedbackProvider) && fDate.equals(feedbackDate)) {
						FeedbackCategoryTO categoryTO = mapper.map(studFeedback.getFeedback().getCategory(),
								FeedbackCategoryTO.class);
						categoryTO.setTeachersComment(studFeedback.getFeedbackText()); // For every feedback category
																						// the teachers comment is added
																						// to every student feedback
																						// record
						categoryTO.setFeedbacks(null); // We don't want the entire list of Feedbacks for every category
														// here.
						FeedbackTO feedbackTO = mapper.map(studFeedback.getFeedback(), FeedbackTO.class);
						studentTO.addFeedback(categoryTO, feedbackTO);
					}
				}
			}
			studentTO.addToSortedMap();
			studentList.add(studentTO);
		}

		return studentList;
	}

	private TopicsTO getCurrentTopicOfBatch(BatchDetails batch) throws ParseException, TSHException {
		TopicsTO currentTopic = new TopicsTO();
		ModelMapper mapper = new ModelMapper();

		currentTopic = mapper.map(topicService.getCurrentTopic(batch), TopicsTO.class);
		return currentTopic;
	}

	private TopicsTO getNextTopicOfBatch(BatchDetails batch) throws ParseException, TSHException {
		TopicsTO nextTopic = new TopicsTO();
		ModelMapper mapper = new ModelMapper();

		nextTopic = mapper.map(topicService.getNextTopic(batch), TopicsTO.class);
		return nextTopic;
	}

	private List<TopicsTO> fetchAllTopicsForCourse(BatchDetails batch) {
		logger.info("Fetching all topics for the course :" + batch.getCourse().getDescription() + " Grade : "
				+ batch.getGrade().getGrade());
		List<TopicsTO> topicList = new ArrayList<>();
		ModelMapper mapper = new ModelMapper();
		topicList = topicService.getAllActiveTopicsForCourseAndGrade(batch.getCourse(), batch.getGrade()).stream()
				.map(topic -> {
					TopicsTO topicTO = mapper.map(topic, TopicsTO.class);
					List<BatchProgress> batchProgress = progressService.getAllBatchProgress(batch);
					BatchProgress progress = batchProgress.stream().filter(bp -> bp.getTopic().equals(topic))
							.findFirst().orElse(null);
					if (progress != null) {
						topicTO.setStatus(progress.getStatus().getStatus());
						try {
							if (progress.getStartDate() != null)
								topicTO.setStartDate(TshUtil.format(progress.getStartDate()));
							if (progress.getEndDate() != null)
								topicTO.setEndDate(TshUtil.format(progress.getEndDate()));
							if (progress.getPlannedStartDate() != null)
								topicTO.setPlannedStartDate(progress.getPlannedStartDate());
							if (progress.getPlannedEndDate() != null)
								topicTO.setPlannedEndDate(progress.getPlannedEndDate());
						} catch (TSHException e) {
							logger.error(e.getMessage());
						}
					}
					return topicTO;
				}).collect(Collectors.toList());

		return topicList;
	}

	@Override
	public BatchDetails getBatchDetailsById(int id) throws TSHException {
		return batchDetailRepo.findById(id);
	}

}