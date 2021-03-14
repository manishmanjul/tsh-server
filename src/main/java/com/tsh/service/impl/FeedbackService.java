package com.tsh.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.Course;
import com.tsh.entities.Feedback;
import com.tsh.entities.FeedbackCategory;
import com.tsh.entities.Grades;
import com.tsh.entities.StudentBatches;
import com.tsh.entities.StudentFeedback;
import com.tsh.entities.Teacher;
import com.tsh.entities.Term;
import com.tsh.entities.TopicProgress;
import com.tsh.entities.Topics;
import com.tsh.entities.User;
import com.tsh.entities.Week;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.DeleteFeedbackRequest;
import com.tsh.library.dto.FeedbackCategoryTO;
import com.tsh.library.dto.FeedbackProvider;
import com.tsh.library.dto.FeedbackRequestTO;
import com.tsh.library.dto.FeedbackTO;
import com.tsh.library.dto.StudentFeedbackRequestTO;
import com.tsh.library.dto.StudentRequestTO;
import com.tsh.library.dto.TeacherTO;
import com.tsh.library.dto.TopicProgressTO;
import com.tsh.library.dto.TopicsTO;
import com.tsh.library.dto.UpdateFeedbackRequest;
import com.tsh.library.dto.UserTO;
import com.tsh.repositories.FeedbackCategoryRepository;
import com.tsh.repositories.FeedbackRepository;
import com.tsh.repositories.StudentFeedbackRepository;
import com.tsh.service.IFeedbackService;
import com.tsh.service.IGeneralService;
import com.tsh.service.IProgressService;
import com.tsh.service.IStudentService;
import com.tsh.service.ITeacherService;
import com.tsh.service.ITopicService;
import com.tsh.utility.TshUtil;

@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class FeedbackService implements IFeedbackService {

	@Autowired
	private FeedbackRepository feedbackRepo;
	@Autowired
	private FeedbackCategoryRepository feedbackCategoryRepo;
	@Autowired
	private StudentFeedbackRepository studentFeedbackRepo;
	@Autowired
	private ITeacherService teacherService;
	@Autowired
	private IStudentService studentService;
	@Autowired
	private IGeneralService generalService;
	@Autowired
	private ITopicService topicService;
	@Autowired
	private IProgressService progressService;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public FeedbackService() {
	}

	@Override
	public Map<String, List<Topics>> validateAndSync(List<Topics> topicList) throws TSHException {
		logger.info("Validating and Synching all topics...");
		List<Topics> modifiedTopics = new ArrayList<>();
		Map<String, List<Topics>> returnMap = new HashMap<>();

		Iterator<Topics> itr = topicList.iterator();
		while (itr.hasNext()) {
			Topics topic = itr.next();

			Grades grade = generalService.getGrades(topic.getGrade().getGrade())
					.orElseThrow(() -> new TSHException("Grade not found : " + topic.getGrade()));
			Course course = generalService.getCourses(topic.getCourse().getShortDescription())
					.orElseThrow(() -> new TSHException("Course not found : " + topic.getCourse()));
			if (topic.getTerm() != null) {
				Term term = generalService.getTerm(topic.getTerm().getTerm());
				topic.setTerm(term);
			}
			if (topic.getWeek() != null) {
				Week week = generalService.getWeekByWeekNumber(topic.getWeek().getWeekNumber());
				topic.setWeek(week);
			}

			topic.setGrade(grade);
			topic.setCourse(course);

			Topics existingTopic = topicService.getTopicByMappedId(topic.getMappedId());
			if (existingTopic != null) {
				if (!existingTopic.getChapter().equalsIgnoreCase(topic.getChapter()))
					existingTopic.setChapter(topic.getChapter());
				if (existingTopic.getComplexity() != topic.getComplexity())
					existingTopic.setComplexity(topic.getComplexity());
				if (!existingTopic.getDescription().equalsIgnoreCase(topic.getDescription()))
					existingTopic.setDescription(topic.getDescription());
				if (!existingTopic.getTopicName().equalsIgnoreCase(topic.getTopicName()))
					existingTopic.setTopicName(topic.getTopicName());
				if (!existingTopic.getTerm().equals(topic.getTerm()))
					existingTopic.setTerm(topic.getTerm());
				if (existingTopic.getHoursToComplete() != topic.getHoursToComplete())
					existingTopic.setHoursToComplete(topic.getHoursToComplete());
				if (!existingTopic.getGrade().equals(topic.getGrade()))
					existingTopic.setGrade(topic.getGrade());
				if (!existingTopic.getCourse().equals(topic.getCourse()))
					existingTopic.setCourse(topic.getCourse());
				if (!existingTopic.getWeek().equals(topic.getWeek()))
					existingTopic.setWeek(topic.getWeek());

				modifiedTopics.add(existingTopic);
				itr.remove();
			}
		}

		returnMap.put("Modified", modifiedTopics);
		returnMap.put("New", topicList);
		logger.info("Validation result - {} existing topics modified. {} new topics to be added.",
				modifiedTopics.size(), topicList.size());
		return returnMap;
	}

	@Override
	public Feedback getFeedbackByShortDescription(String shortDescription) {
		return feedbackRepo.findByShortDescription(shortDescription).get(0);
	}

	@Override
	public List<StudentFeedback> saveAllStudentFeedbacks(List<StudentFeedback> feedbacks) {
		logger.info("Saving {} feedbacks..", feedbacks.size());
		return studentFeedbackRepo.saveAll(feedbacks);
	}

	@Override
	public List<StudentFeedback> getAllFeedbacks(StudentBatches studentBatches, Topics topic) {
		return studentFeedbackRepo.findByStudentBatchesAndTopic(studentBatches, topic);
	}

	@Override
	public List<FeedbackCategory> getAllActiveFeedbackCategories(int grade) {
		List<FeedbackCategory> returnList = null;

		returnList = feedbackCategoryRepo.findByGradeAndActiveOrderByOrder(grade, true);
		if (returnList == null || returnList.size() == 0)
			returnList = feedbackCategoryRepo.findByGradeAndActiveOrderByOrder(0, true);

		return returnList;
	}

	@Override
	public List<FeedbackCategory> getAllFeedbackCategories(int grade) {
		List<FeedbackCategory> returnList = null;

		returnList = feedbackCategoryRepo.findByGradeOrderByOrder(grade);
		if (returnList == null || returnList.size() == 0)
			returnList = feedbackCategoryRepo.findByGradeOrderByOrder(0);

		return returnList;
	}

	@Override
	public void processStudentFeedback(BatchDetails batchDetails, StudentFeedbackRequestTO inputData, User loggedinUser,
			List<TopicProgress> currTopicProgressList) throws TSHException {

		logger.info("Initiating process feedback for requested students.");
		logger.info("Validating Teacher details");

		Teacher teacher = teacherService.findById(inputData.getUpdatedById());

		// There can be multiple students for same feedback. Update this feedback for
		// all.
		ArrayList<StudentFeedback> studentFeedbacks = new ArrayList<>();
		for (StudentRequestTO student : inputData.getStudents()) {
			logger.info("Updating feedback for : {} - ID : {}", student.getName(), student.getId());

			StudentBatches studBatch = studentService.getStudentBatchesById(student.getId());
			if (studBatch == null) {
				logger.warn("Student not Found : {} -- SKIPPING add Feedback for this student", student.getName());
				continue;
			}

			Topics topic = topicService.getTopicById(inputData.getTodaysTopicId());
			if (topic == null) {
				logger.warn("Topic not Found : {} -- SKIPPING add Feedback for this student",
						inputData.getTodaysTopicDesc());
				continue;
			}

			TopicProgress currentTopicProgress = this.getCurrentTopicProgress(currTopicProgressList, studBatch);
			if (currentTopicProgress == null) {
				logger.error("No Topic Progress found for student : {}", student.getName());
				throw new TSHException("No Topic Progress found for student : " + student.getName());
			}

			logger.info("Find any exiting feedback for {} on topic : {} provided by user : {}", student.getName(),
					topic.getDescription(), loggedinUser.getName());
			this.clearFeedbacks(currentTopicProgress, loggedinUser, studBatch);

			logger.info("Aggregating all feedbacks for : {} = ID : {}", student.getName(), student.getId());
			for (FeedbackRequestTO feedbackTO : inputData.getFeedbacks()) {
				Feedback feedback = this.getFeedbackById(feedbackTO.getFeedbackId());
				if (feedback == null) {
					logger.warn("Feedbck : {} - not found. SKipping this feedback", feedbackTO.getDescription());
					continue;
				}

				StudentFeedback studFeedback = new StudentFeedback();
				studFeedback.setStudentBatches(studBatch);
				studFeedback.setFeedback(feedback);
				studFeedback.setFeedbackDate(TshUtil.getCurrentDate());
				studFeedback.setTeacher(teacher);
				studFeedback.setUser(loggedinUser);
				studFeedback.setUpdatedBy(null);
				studFeedback.setTopic(topic);
				studFeedback.setFeedbackText(feedbackTO.getComment());

				studentFeedbacks.add(studFeedback);
			}

			this.saveAllStudentFeedbacks(studentFeedbacks);
			studentFeedbacks.clear();
		}
	}

	@Override
	public StudentFeedback saveFeedback(StudentFeedback feedback) throws TSHException {
		StudentFeedback savedFeedback = studentFeedbackRepo.save(feedback);
		if (savedFeedback == null) {
			logger.warn("Unable to save feedback for : {}", feedback.getStudentBatches().getStudent().getStudentName());
			throw new TSHException("Could not save Feedback");
		}

		return savedFeedback;
	}

	@Override
	public Feedback getFeedbackById(int feedbackId) {
		return feedbackRepo.findById(feedbackId).orElse(null);
	}

	@Override
	public List<TopicsTO> populateAllFeedbacksWithProviders(List<TopicsTO> topicTOList, StudentBatches studentBatches) {
		logger.info("Constructing feedback data for all completed topics");

		ModelMapper mapper = new ModelMapper();
		List<TopicsTO> returnList = new ArrayList<>();

		for (TopicsTO topic : topicTOList) {
			List<FeedbackProvider> providers = new ArrayList<>();
			FeedbackProvider provider = null;

			List<StudentFeedback> studFeedbacks = studentFeedbackRepo
					.findByStudentBatchesAndTopicIdOrderByFeedbackDate(studentBatches, topic.getId());
			for (StudentFeedback feedback : studFeedbacks) {
				TeacherTO teacherTO = null;
				UserTO userTO = null;
				UserTO updatedBy = null;
				TopicProgressTO topicProgress = null;
				if (feedback.getTeacher() != null)
					teacherTO = mapper.map(feedback.getTeacher(), TeacherTO.class);
				if (feedback.getUser() != null)
					userTO = mapper.map(feedback.getUser(), UserTO.class);
				if (feedback.getUpdatedBy() != null)
					updatedBy = mapper.map(feedback.getUpdatedBy(), UserTO.class);
				if (feedback.getTopicProgress() != null)
					topicProgress = mapper.map(feedback.getTopicProgress(), TopicProgressTO.class);
				provider = new FeedbackProvider();
				provider.setTeacher(teacherTO);
				provider.setUserTO(userTO);
				provider.setUpdatedBy(updatedBy);
				provider.setTopicProgress(topicProgress);
				provider.setFeedbackDate(feedback.getFeedbackDate());
				provider.setStudentBatch(studentBatches);

				provider.setUpdatedOn(feedback.getUpdateDate().toString());

				// Is the provider with this user already in the list. Get that or else
				// create a new one.
				int index = providers.indexOf(provider);
				if (index < 0) {
					providers.add(provider);
				} else {
					provider = providers.get(index);
				}

				if (provider.getUpdatedBy() == null && feedback.getUpdatedBy() != null)
					provider.setUpdatedBy(mapper.map(feedback.getUpdatedBy(), UserTO.class));

				// Now lets see if a feedback category is already there in this provider.
				FeedbackCategoryTO feedbackCategoryTO = mapper.map(feedback.getFeedback().getCategory(),
						FeedbackCategoryTO.class);
				feedbackCategoryTO.setFeedbacks(null);
				if (provider.getFeedbackCategory() != null)
					index = provider.getFeedbackCategory().indexOf(feedbackCategoryTO);
				else
					index = -1;

				if (index < 0) {
					provider.addFeedbackCategory(feedbackCategoryTO);
				} else {
					feedbackCategoryTO = provider.getFeedbackCategory().get(index);
				}

				FeedbackTO feedbackTO = mapper.map(feedback.getFeedback(), FeedbackTO.class);
				feedbackCategoryTO.setTeachersComment(feedback.getFeedbackText());
				feedbackCategoryTO.addFedback(feedbackTO);
			}
			if (providers != null && providers.size() > 0) {
				topic.setProviders(providers);
				returnList.add(topic);
			}
		}
		return returnList;
	}

	@Override
	public void deleteFeedback(DeleteFeedbackRequest request, User loggedinUser) {
		StudentBatches studentBatch = studentService.getStudentBatchesById(request.getStudentBatchId());
		TopicProgress topicProgress = progressService.getTopicProgress(request.getTopicProgressId());
		clearFeedbacks(topicProgress, loggedinUser, studentBatch);
	}

	@Override
	public List<StudentFeedback> getStudentFeedbackByBatchTopicAndTeacher(StudentBatches studentBatches, Topics topic,
			Teacher teacher) {
		return studentFeedbackRepo.findByStudentBatchesAndTopicAndTeacher(studentBatches, topic, teacher);
	}

	@Override
	public FeedbackCategoryTO addFeedbackCategory(FeedbackCategoryTO categoryTO) {

		FeedbackCategory category = new FeedbackCategory();
		category.setActive(true);
		category.setDescription(categoryTO.getDescription());
		category.setGrade(categoryTO.getGrade());
		category.setOrder(categoryTO.getOrder());

		category = feedbackCategoryRepo.save(category);
		if (category == null)
			categoryTO = null;
		return categoryTO;
	}

	@Override
	public FeedbackTO addFeedbackItem(FeedbackTO feedbackTO) throws TSHException {
		Feedback feedback = new Feedback();
		feedback.setActive(true);
		feedback.setCriteria(feedbackTO.getCriteria());
		feedback.setDescription(feedbackTO.getDescription());
		feedback.setShortDescription(feedbackTO.getShortDescription());
		FeedbackCategory feedbackCategory = feedbackCategoryRepo.findById(feedbackTO.getCategory()).orElse(null);
		if (feedbackCategory == null)
			throw new TSHException("No Feedback Category found with ID: " + feedbackTO.getCategory());

		feedback.setCategory(feedbackCategory);
		feedback = feedbackRepo.saveAndFlush(feedback);
		if (feedback == null)
			feedbackTO = null;

		return feedbackTO;
	}

	@Override
	public FeedbackCategoryTO toggleFeedbackCategoryState(FeedbackCategoryTO category) throws TSHException {
		FeedbackCategoryTO returnResponse = null;
		FeedbackCategory fCategory = feedbackCategoryRepo.findById(category.getId()).orElse(null);
		if (fCategory == null)
			throw new TSHException("No Feedback Category found wit ID: " + category.getId());

		fCategory.setActive(!fCategory.isActive());
		for (Feedback f : fCategory.getFeedbacks()) {
			f.setActive(fCategory.isActive());
		}

		fCategory = feedbackCategoryRepo.saveAndFlush(fCategory);
		if (fCategory != null)
			returnResponse = category;

		logger.info("Feedback Category with id: {} and its associated Feedback Items state was changed to {}",
				category.getId(), fCategory.isActive());

		return returnResponse;
	}

	@Override
	public FeedbackTO toggleFeedbackItemState(FeedbackTO feedback) throws TSHException {
		FeedbackTO returnResponse = null;

		Feedback f = getFeedbackById(feedback.getId());
		if (f == null)
			throw new TSHException("No Feedback item found with ID: " + feedback.getId());

		f.setActive(!f.isActive());
		f = feedbackRepo.saveAndFlush(f);
		if (f != null)
			returnResponse = feedback;

		logger.info("Feedback item with id: {} state has been changed to : {}", feedback.getId(), feedback.isActive());
		return returnResponse;
	}

	@Override
	public FeedbackCategoryTO findFeedbackCategoryById(int id) {
		FeedbackCategory fCategory = feedbackCategoryRepo.findById(id).orElse(null);
		FeedbackCategoryTO fCategoryTO = null;
		ModelMapper mapper = new ModelMapper();
		fCategoryTO = mapper.map(fCategory, FeedbackCategoryTO.class);
		return fCategoryTO;
	}

	@Override
	public List<FeedbackCategoryTO> getEmptyFeedback(int grade) {
		List<FeedbackCategoryTO> returnList = new ArrayList<>();
		List<FeedbackCategory> categoryList = getAllActiveFeedbackCategories(grade);

		returnList = categoryList.stream().map(c -> {
			FeedbackCategoryTO catTO = new FeedbackCategoryTO();
			catTO.setDescription(c.getDescription());
			catTO.setFeedbacks(null);
			return catTO;
		}).collect(Collectors.toList());

		return returnList;
	}

	@Override
	public void updateAndAddStudentFeedback(UpdateFeedbackRequest request, StudentBatches studentBatches,
			User loggedInUser) throws TSHException {

		List<StudentFeedback> updateList = new ArrayList<>();
		TopicProgress topicProgress = progressService.getTopicProgress(request.getTopicProgressId());
		Topics topic = topicService.getTopicById(request.getTopicId());
		List<StudentFeedback> feedbacks = getAllFeedbacks(topicProgress, studentBatches);
		for (FeedbackCategoryTO cat : request.getFeedbacks()) {
			for (FeedbackTO feedbackTO : cat.getFeedbacks()) {
				StudentFeedback sf = findFeedback(request, feedbackTO, feedbacks);
				if (sf != null) {
					String oldFeedbackText, newFeedbackText;
					if (sf.getFeedbackText() == null)
						oldFeedbackText = "";
					else
						oldFeedbackText = sf.getFeedbackText();

					if (cat.getTeachersComment() == null)
						newFeedbackText = "";
					else
						newFeedbackText = cat.getTeachersComment();

					if (!oldFeedbackText.equalsIgnoreCase(newFeedbackText)) {
						sf.setFeedbackText(cat.getTeachersComment());
						sf.setUpdatedBy(loggedInUser);
						updateList.add(sf);
					}

				} else {
					StudentFeedback fb = new StudentFeedback();
					Feedback newFb = getFeedbackById(feedbackTO.getId());
					fb.setFeedback(newFb);
					fb.setFeedbackDate(TshUtil.getCurrentDate());
					fb.setFeedbackText(cat.getTeachersComment());
					fb.setStudentBatches(studentBatches);
					Teacher teacher = teacherService.findById(request.getTeacherId());
					fb.setTeacher(teacher);
					fb.setTopic(topic);
					fb.setUser(loggedInUser);
					fb.setTopicProgress(topicProgress);
					fb.setUpdatedBy(loggedInUser);
					updateList.add(fb);
				}
				feedbacks.remove(sf);
			}
		}

		this.saveAllStudentFeedbacks(updateList);

		// All remaining feedbacks must have been deleted. So delete them.
		studentFeedbackRepo.deleteAll(feedbacks);
	}

	private StudentFeedback findFeedback(UpdateFeedbackRequest request, FeedbackTO feedbackTO,
			List<StudentFeedback> feedbacks) {
		StudentFeedback returnFeedback = null;

		for (StudentFeedback fb : feedbacks) {

			if (feedbackTO.getId() == fb.getFeedback().getId()
					&& request.getTopicProgressId() == fb.getTopicProgress().getId()) {
				returnFeedback = fb;
				break;
			}
		}

		return returnFeedback;
	}

	private TopicProgress getCurrentTopicProgress(List<TopicProgress> currTopicProgressList, StudentBatches studBatch) {

		List<TopicProgress> postFilter = currTopicProgressList.stream()
				.filter(sf -> sf.getStudent().equals(studBatch.getStudent())).collect(Collectors.toList());
		if (postFilter.size() > 0)
			return postFilter.get(0);
		else
			return null;
	}

	@Override
	public void clearFeedbacks(TopicProgress topicProgress, User loggedInUser, StudentBatches studentBatches) {
		List<StudentFeedback> existingFeedbacks = studentFeedbackRepo
				.findByTopicProgressAndUserAndStudentBatches(topicProgress, loggedInUser, studentBatches);
		if (existingFeedbacks.size() > 0) {
			studentFeedbackRepo.deleteAll(existingFeedbacks);
		}
	}

	@Override
	public List<StudentFeedback> getAllFeedbacks(TopicProgress topicProgress, StudentBatches studentBatches) {
		return studentFeedbackRepo.findByTopicProgressAndStudentBatches(topicProgress, studentBatches);
	}

}
