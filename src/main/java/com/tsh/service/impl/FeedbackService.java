package com.tsh.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.tsh.entities.TopicStatus;
import com.tsh.entities.Topics;
import com.tsh.entities.Week;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.FeedbackResponseTO;
import com.tsh.library.dto.StudentFeedbackResponseTO;
import com.tsh.library.dto.StudentResponseTO;
import com.tsh.repositories.FeedbackCategoryRepository;
import com.tsh.repositories.FeedbackRepository;
import com.tsh.repositories.StudentFeedbackRepository;
import com.tsh.repositories.TopicProgressRepository;
import com.tsh.repositories.TopicStatusRepository;
import com.tsh.repositories.TopicsRepository;
import com.tsh.service.IBatchService;
import com.tsh.service.IFeedbackService;
import com.tsh.service.IStudentService;
import com.tsh.service.ITeacherService;
import com.tsh.utility.TshUtil;

@Service
public class FeedbackService implements IFeedbackService{
	
	@Autowired
	private TopicsRepository topicsRepo;
	@Autowired
	private TopicStatusRepository topicStatusRepo;
	@Autowired
	private FeedbackRepository feedbackRepo;
	@Autowired
	private FeedbackCategoryRepository feedbackCategoryRepo;
	@Autowired
	private StudentFeedbackRepository studentFeedbackRepo;
	@Autowired
	private TopicProgressRepository topicProgressRepo;
	@Autowired
	private IBatchService batchService;
	@Autowired
	private ITeacherService teacherService;
	@Autowired
	private IStudentService studentService;

	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
   	public Topics getTopicByMappedId(String mappedId) throws TSHException{
   		List<Topics> topicList = topicsRepo.findByMappedId(mappedId);
   		if(topicList.size() > 1) throw new TSHException("Multiple topics found with mapped ID :" + mappedId);
   		if(topicList.size() == 0) return null;
   		return topicList.get(0);
   	}

	@Override
	public List<Topics> saveAllTopics(List<Topics> topicList) {
		 return topicsRepo.saveAll(topicList);
	}

	@Override
	public Map<String, List<Topics>> validateAndSync(List<Topics> topicList) throws TSHException {
		logger.info("Validating and Synching all topics...");
		List<Topics> modifiedTopics = new ArrayList<>();
		Map<String, List<Topics>> returnMap = new HashMap<>();
		
		Iterator<Topics> itr = topicList.iterator();
		while(itr.hasNext()) {
			Topics topic = itr.next();
			
			Grades grade = batchService.getGrades(topic.getGrade().getGrade()).orElseThrow(() -> new TSHException("Grade not found : " + topic.getGrade()));
			Course course = batchService.getCourses(topic.getCourse().getShortDescription()).orElseThrow(()-> new TSHException("Course not found : " + topic.getCourse()));
			if(topic.getTerm() != null) {
				Term term = batchService.getTerm(topic.getTerm().getTerm());
				topic.setTerm(term);
			}
			if(topic.getWeek() != null) {
				Week week = batchService.getWeekByWeekNumber(topic.getWeek().getWeekNumber());
				topic.setWeek(week);
			}
			
			topic.setGrade(grade);
			topic.setCourse(course);
			
			Topics existingTopic = getTopicByMappedId(topic.getMappedId());
			if(existingTopic != null) {
				if(!existingTopic.getChapter().equalsIgnoreCase(topic.getChapter())) existingTopic.setChapter(topic.getChapter());
				if(existingTopic.getComplexity() != topic.getComplexity()) existingTopic.setComplexity(topic.getComplexity());
				if(!existingTopic.getDescription().equalsIgnoreCase(topic.getDescription())) existingTopic.setDescription(topic.getDescription());
				if(!existingTopic.getTopicName().equalsIgnoreCase(topic.getTopicName())) existingTopic.setTopicName(topic.getTopicName());
				if(!existingTopic.getTerm().equals(topic.getTerm())) existingTopic.setTerm(topic.getTerm());
				if(existingTopic.getHoursToComplete() != topic.getHoursToComplete()) existingTopic.setHoursToComplete(topic.getHoursToComplete());
				if(!existingTopic.getGrade().equals(topic.getGrade())) existingTopic.setGrade(topic.getGrade());
				if(!existingTopic.getCourse().equals(topic.getCourse())) existingTopic.setCourse(topic.getCourse());
				if(!existingTopic.getWeek().equals(topic.getWeek())) existingTopic.setWeek(topic.getWeek());
				
				modifiedTopics.add(existingTopic);
				itr.remove();
			}
		}
		
		returnMap.put("Modified", modifiedTopics);
		returnMap.put("New", topicList);
		logger.info("Validation result - {} existing topics modified. {} new topics to be added.", modifiedTopics.size(), topicList.size())	;
		return returnMap;
	}

	@Override
	public TopicStatus getTopicStatusByStatus(String status) {
		return topicStatusRepo.findByStatus(status);
	}

	@Override
	public Feedback getFeedbackByShortDescription(String shortDescription) {
		return feedbackRepo.findByShortDescription(shortDescription).get(0);
	}

	@Override
	public List<StudentFeedback> saveAllStudentFeedbacks(List<StudentFeedback> feedbacks) {
		logger.info("Saving {} feedbacks..",feedbacks.size());
		return studentFeedbackRepo.saveAll(feedbacks);
	}

	@Override
	public List<TopicProgress> saveAllTopicProgress(List<TopicProgress> progressList) {
		return topicProgressRepo.saveAll(progressList);
	}

	@Override
	public List<Topics> getAllActiveTopicsForCourseAndGrade(Course course, Grades grade) {		
		return topicsRepo.findAllByCourseAndGradeAndActive(course, grade, true);
	}

	@Override
	public List<StudentFeedback> getAllFeedbacks(StudentBatches studentBatches, Topics topic) {
		return studentFeedbackRepo.findByStudentBatchesAndTopic(studentBatches, topic);
	}

	@Override
	public Map<String, String> getDummyFeedbackMap() {
		Map<String, String> feedbackMap = new HashMap<>();
		feedbackMap.put("Revesion", "No feedback available");
		feedbackMap.put("Classwork", "No feedback available");
		feedbackMap.put("Homework", "No feedback available");
		feedbackMap.put("Assessment", "No feedback available");
		
		return feedbackMap;
	}

	@Override
	public List<FeedbackCategory> getAllActiveFeedbackCategories() {
		return feedbackCategoryRepo.findByActiveOrderByOrder(true);
	}

	@Override
	public void processStudentFeedback(BatchDetails batchDetails, StudentFeedbackResponseTO inputData) throws TSHException{
		
		logger.info("Initiating process feedback for requested students.");
		logger.info("Validating Teacher details");
		Teacher updatedBy = teacherService.findById(inputData.getUpdatedById());
		if(updatedBy == null) {
			logger.warn("No Teacher found with ID : {} - Select a Valid teacher and reinitiate. Aborting ProcessFeedback.", inputData.getUpdatedById());
			throw new TSHException("No Teacher found with ID : " + inputData.getUpdatedById() + " - Select a Valid teacher and reinitiate. Aborting ProcessFeedback.");
		}
		
		// There can be multiple students for same feedback. Update this feedback for all.
		ArrayList<StudentFeedback> studentFeedbacks = new ArrayList<>();
		for(StudentResponseTO student : inputData.getStudents()) {
			logger.info("Updating feedback for : {}", student.getName());
			
			StudentBatches studBatch = studentService.getStudentBatchesById(student.getId());
			if(studBatch == null) {
				logger.warn("Student not Found : {} -- SKIPPING add Feedback for this student",student.getName());
				continue;
			}
			
			Topics topic = this.getTopicById(inputData.getTodaysTopicId());
			if(topic == null) {
				logger.warn("Topic not Found : {} -- SKIPPING add Feedback for this student",inputData.getTodaysTopicDesc());
				continue;
			}
			
			logger.info("Aggregating all feedbacks for : {}",student.getName());
			
			for(FeedbackResponseTO feedbackTO : inputData.getFeedbacks()) {
				Feedback feedback = this.getFeedbackById(feedbackTO.getFeedbackId());
				if(feedback == null) {
					logger.warn("Feedbck : {} - not found. SKipping this feedback", feedbackTO.getDescription());
					continue;
				}
				
				StudentFeedback	studFeedback = new StudentFeedback();
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
	public StudentFeedback saveFeedback(StudentFeedback feedback) throws TSHException{
		StudentFeedback savedFeedback = studentFeedbackRepo.save(feedback);
		if(savedFeedback == null) {
			logger.warn("Unable to save feedback for : {}", feedback.getStudentBatches().getStudent().getStudentName());
			throw new TSHException("Could not save Feedback");
		}
		
		return savedFeedback;
	}

	@Override
	public Topics getTopicById(int topicId) {
		return topicsRepo.findById(topicId).orElse(null);
	}

	@Override
	public Feedback getFeedbackById(int feedbackId) {
		return feedbackRepo.findById(feedbackId).orElse(null);
	}	
}
