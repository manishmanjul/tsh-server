package com.tsh.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.tsh.entities.Topics;
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
import com.tsh.library.dto.TopicsTO;
import com.tsh.repositories.FeedbackCategoryRepository;
import com.tsh.repositories.FeedbackRepository;
import com.tsh.repositories.StudentFeedbackRepository;
import com.tsh.service.IFeedbackService;
import com.tsh.service.IGeneralService;
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
	public Map<String, String> getDummyFeedbackMap() {
		Map<String, String> feedbackMap = new HashMap<>();
		feedbackMap.put("Revision", "No feedback available");
		feedbackMap.put("Classwork", "No feedback available");
		feedbackMap.put("Homework", "No feedback available");
		feedbackMap.put("Assessment", "No feedback available");

		return feedbackMap;
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
	public void processStudentFeedback(BatchDetails batchDetails, StudentFeedbackRequestTO inputData)
			throws TSHException {

		logger.info("Initiating process feedback for requested students.");
		logger.info("Validating Teacher details");
		Teacher updatedBy = teacherService.findById(inputData.getUpdatedById());
		if (updatedBy == null) {
			logger.warn(
					"No Teacher found with ID : {} - Select a Valid teacher and reinitiate. Aborting ProcessFeedback.",
					inputData.getUpdatedById());
			throw new TSHException("No Teacher found with ID : " + inputData.getUpdatedById()
					+ " - Select a Valid teacher and reinitiate. Aborting ProcessFeedback.");
		}

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
				studFeedback.setTeacher(updatedBy);
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
					.findByStudentBatchesAndTopicIdOrderByTeacher(studentBatches, topic.getId());
			for (StudentFeedback feedback : studFeedbacks) {
				TeacherTO teacherTO = mapper.map(feedback.getTeacher(), TeacherTO.class);
				provider = new FeedbackProvider();
				provider.setTeacher(teacherTO);
				provider.setFeedbackDate(feedback.getFeedbackDate());
				provider.setStudentBatch(studentBatches);

				int index = providers.indexOf(provider);

				// Is the provider with this teacher already in the list. Get that or else
				// create a new one.
				if (index < 0) {
					providers.add(provider);
				} else {
					provider = providers.get(index);
				}

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
	public void deleteFeedback(DeleteFeedbackRequest request) {
		StudentBatches studentBatch = studentService.getStudentBatchesById(request.getStudentBatchId());
		Topics topic = topicService.getTopicById(request.getTopicId());
		Teacher teacher = teacherService.findById(request.getTeacherId());
		List<StudentFeedback> feedbacks = getStudentFeedbackByBatchTopicAndTeacher(studentBatch, topic, teacher);

		studentFeedbackRepo.deleteAll(feedbacks);
	}

	@Override
	public List<StudentFeedback> getStudentFeedbackByBatchTopicAndTeacher(StudentBatches studentBatches, Topics topic,
			Teacher teacher) {
		return studentFeedbackRepo.findByStudentBatchesAndTopicAndTeacher(studentBatches, topic, teacher);
	}
}
