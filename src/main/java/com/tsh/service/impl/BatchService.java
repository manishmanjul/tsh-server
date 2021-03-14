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
import org.springframework.cache.annotation.Cacheable;
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
import com.tsh.entities.User;
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
import com.tsh.service.IGeneralService;
import com.tsh.service.IProgressService;
import com.tsh.service.IStudentService;
import com.tsh.service.ITopicService;
import com.tsh.utility.TshUtil;

@Service
//@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
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
	@Autowired
	private IGeneralService generalService;

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
	public List<ScheduleTO> getSchedulesFor(Teacher teacher, User loggedInUser) throws ParseException, TSHException {

		List<ScheduleTO> schedules = new ArrayList<>();
		if (teacher != null)
			logger.info("Finding all batches for teacher : {}", teacher.getTeacherName());
		else
			logger.info("Finding all batches for Admin...");
		List<BatchDetails> batches = null;

		// In case the logged in user is neither a teacher or admin. Load as if it was a
		// teacher. WIll update other scenarios later.
		if (loggedInUser.isAdmin()) {
			batches = getAllActiveBatches(false);
		} else if (loggedInUser.isTeacher1() || loggedInUser.isTeacher1()) {
			batches = batchDetailRepo.findAllByTeacherAndActive(teacher, true);
		} else {
			batches = batchDetailRepo.findAllByTeacherAndActive(teacher, true);
		}

		for (BatchDetails batch : batches) {
			BatchProgress batchProgress = progressService.getBatchProgressAsOfToday(batch);
			if (batch.isCLassToday() && !isMyBatch(batch, batchProgress)
					&& (loggedInUser.isTeacher1() || loggedInUser.isTeacher2())) {
				continue;
			}

			ScheduleTO sch = getBatchInfoToRender(batch);
			if (batchProgress != null) {
				if (batchProgress.getPlannedTime() != null)
					sch.setStartTime(batchProgress.getPlannedTime().toString());

				if (batchProgress.getTeacher() != null)
					sch.setTeacherName(batchProgress.getTeacher().getTeacherName());

				if (batchProgress.isCanceled())
					sch.cancelBatch();
			}
			schedules.add(sch);
		}
		logger.info("{} batches fetched. Returning result", schedules.size());
		return schedules;
	}

	@Override
	public List<BatchDetails> getAllBatchDetailsForUser(User loggedInUser, Teacher teacher) throws TSHException {

		if (teacher != null)
			logger.info("Finding all batches for teacher : {}", teacher.getTeacherName());
		else
			logger.info("Finding all batches for Admin...");

		List<BatchDetails> batches = null;
		List<BatchProgress> batchProgressList = null;
		List<StudentBatches> studentBatchList = null;

		// In case the logged in user is neither a teacher or admin. Load as if it was a
		// teacher. WIll update other scenarios later.
		if (loggedInUser.isAdmin()) {
			batches = getAllActiveBatches(false);
			batchProgressList = progressService.getAllBatchProgressTodayAndAfter();
			studentBatchList = studentService.getAllActiveStudentBatches();
		} else if (loggedInUser.isTeacher1() || loggedInUser.isTeacher1()) {
			batches = batchDetailRepo.findAllByTeacherAndActive(teacher, true);
			batchProgressList = progressService.getAllBatchProgressTodayAndAfter(teacher);
			studentBatchList = studentService.getAllActiveStudentBatches(teacher);
		} else {
			batches = batchDetailRepo.findAllByTeacherAndActive(teacher, true);
			batchProgressList = progressService.getAllBatchProgressTodayAndAfter(teacher);
			studentBatchList = studentService.getAllActiveStudentBatches(teacher);
		}

		for (BatchDetails batch : batches) {
			BatchProgress batchProgress = this.getAssociatedBatchProgress(batch, batchProgressList);
			batch.setBatchProgress(batchProgress);
			batchProgressList.remove(batchProgress);
			batch.setStudentBatchList(this.getBatchStudents(batch, studentBatchList));
		}

		List<Integer> batchDetailIds = new ArrayList<>();
		for (BatchProgress batchProgress : batchProgressList) {
			batchDetailIds.add(new Integer(batchProgress.getBatchDetails().getId()));
		}

		List<BatchDetails> extraClasses = batchDetailRepo.findByIdIn(batchDetailIds);
		for (BatchDetails bp : extraClasses) {
			bp.setBatchProgress(this.getAssociatedBatchProgress(bp, batchProgressList));
			bp.setStudentBatchList(this.getBatchStudents(bp, studentBatchList));
		}

		batches.addAll(extraClasses);

		return batches;
	}

	/**
	 * Find the BatchProgress with same batch details id. Return if found else
	 * return null;
	 * 
	 * @param batchDetails
	 * @param batchProgressList
	 * @return
	 */
	private BatchProgress getAssociatedBatchProgress(BatchDetails batchDetails, List<BatchProgress> batchProgressList) {
		List<BatchProgress> filteredBatchProgress = batchProgressList.stream()
				.filter(p -> p.getBatchDetails().getId() == batchDetails.getId()).collect(Collectors.toList());
		if (filteredBatchProgress.size() > 0)
			return filteredBatchProgress.get(0);
		else
			return null;
	}

	private List<StudentBatches> getBatchStudents(BatchDetails batch, List<StudentBatches> studentBatchList) {
		List<StudentBatches> filteredStudentBatchList = studentBatchList.stream()
				.filter(s -> s.getBatchDetails().getId() == batch.getId()).collect(Collectors.toList());
		return filteredStudentBatchList;
	}

	private boolean isMyBatch(BatchDetails batch, BatchProgress progress) {
		if (progress == null)
			return true;
		if (progress.getTeacher() == batch.getTeacher())
			return true;
		else
			return false;
	}

	@Override
	public ScheduleTO getBatchInfoToRender(BatchDetails batchDetails) throws TSHException, ParseException {
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
		Term term = generalService.getCurrentTerm().orElse(new Term(0));
		schedule.setTerm(term.getTerm() + "");
		schedule.setAttendies(fetchAllStudentsData(batchDetails));
//		schedule.setTopics(fetchAllTopicsForCourse(batchDetails));
		schedule.setCurrentTopic(getCurrentTopicOfBatch(batchDetails));
		schedule.setNextTopic(getNextTopicOfBatch(batchDetails));

		return schedule;
	}

	@Cacheable("TshCache")
	private List<StudentTO> fetchAllStudentsData(BatchDetails batch) {
		List<StudentBatches> students = studentService.getAllActiveStudentBatches(batch);
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
//					if (studFeedback.getTeacher().equals(feedbackProvider) && fDate.equals(feedbackDate)) {
					if (fDate.equals(feedbackDate)) {
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
			if (studentTO.getSortedFeedback().size() <= 0)
				studentTO.setSortedFeedback(
						feedbackService.getEmptyFeedback(studentBatch.getStudent().getGrade().getGrade()));
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

	public List<TopicsTO> getBAtchTopics(BatchDetails batch) {
		logger.info("Fetching all topics for the course :" + batch.getCourse().getDescription() + " Grade : "
				+ batch.getGrade().getGrade());
		List<TopicsTO> topicList = new ArrayList<>();
		ModelMapper mapper = new ModelMapper();
		List<BatchProgress> batchProgress = progressService.getAllBatchProgress(batch);
		topicList = topicService.getAllActiveTopicsForCourseAndGrade(batch.getCourse(), batch.getGrade()).stream()
				.map(topic -> {
					TopicsTO topicTO = mapper.map(topic, TopicsTO.class);
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

	@Override
	public List<BatchDetails> getAllBatchDetailsOn(String batchDate) throws ParseException {
		int weekDay = TshUtil.getWeekDayOf(batchDate);
		List<BatchDetails> batchDetails = batchDetailRepo.findAllActiveBatchesForWeekday(weekDay);
		return batchDetails;
	}

	@Cacheable("TshCache")
	public List<BatchDetails> getAllActiveBatches(boolean refresh) {

		this.batchDetails = batchDetailRepo.findByActive(true);
		return this.batchDetails;
	}

	@Override
	public List<BatchDetails> getAllActiveBatchDetails() {
		return batchDetailRepo.findAllByActive(true);
	}

}