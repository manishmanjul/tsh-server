package com.tsh.service.impl;

import java.sql.Time;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
import com.tsh.entities.Week;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.FeedbackCategoryTO;
import com.tsh.library.dto.FeedbackTO;
import com.tsh.library.dto.ScheduleTO;
import com.tsh.library.dto.StudentTO;
import com.tsh.library.dto.TopicsTO;
import com.tsh.repositories.BatchDetailsRepository;
import com.tsh.repositories.BatchRepository;
import com.tsh.repositories.CourseRepository;
import com.tsh.repositories.GradesRepository;
import com.tsh.repositories.TeachersRepository;
import com.tsh.repositories.TermRepository;
import com.tsh.repositories.TimeSlotRepository;
import com.tsh.repositories.TrainingTypeRepository;
import com.tsh.repositories.WeekRepository;
import com.tsh.service.IBatchService;
import com.tsh.service.IFeedbackService;
import com.tsh.service.IProgressService;
import com.tsh.service.IStudentService;

@Service
@Scope("singleton")
public class BatchService implements IBatchService {

//	private static BatchService batchService= null;
	private List<TimeSlot> timeSlots;
	private List<Grades> grades;
	private List<Course> courses;
	private List<Teacher> teachers;
	private List<BatchDetails> batchDetails;
	private List<Batch> batches;
	private List<TrainingType> trainingTypes;
	private List<Term> terms;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BatchService() {checkAllRepos();}
	
	/**
	 * Singleton method. We don't need more than one instance. This call will leak the memory if multiple instances is allowed.
	 * @return
	 */
//	public static BatchService getInstance() {
//		if(batchService == null) batchService = new BatchService();
//		return batchService;
//	}

	@Autowired
	private TimeSlotRepository timeSlotRepo;
	@Autowired
	private GradesRepository gradeRepo;
	@Autowired
	private CourseRepository courseRepo;
	@Autowired
	private TeachersRepository teachersRepo;
	@Autowired
	private BatchDetailsRepository batchDetailRepo;
	@Autowired
	private BatchRepository batchRepo;
	@Autowired
	private TrainingTypeRepository trainingTypeRepo;
	@Autowired
	private TermRepository termRepo;
	@Autowired
	private WeekRepository weekRepo;
	@Autowired
	private IStudentService studentService;
	@Autowired
	private IFeedbackService feedbackService;
	@Autowired
	private IProgressService progressService;
	
	public Optional<TimeSlot> getTimeSlot(int weekDay, Time batchTime) {
		
		if(this.timeSlots == null || this.timeSlots.size() <=0) {
			logger.info("Retrieving all TimeSlots....");
			this.timeSlots = this.timeSlotRepo.findAll();		
		}
		return this.timeSlots.stream().filter(t-> t.getWeekday() == weekDay & t.getStartTime().getTime() == batchTime.getTime()).findFirst();
	}
	
	public Optional<Grades> getGrades(int grade) {
		if(this.grades == null || this.grades.size() <= 0) {
			logger.info("Retrieving all Grades....");
			this.grades = this.gradeRepo.findAll();
		}
		return this.grades.stream().filter(t-> t.getGrade() == grade).findFirst();
	}
	 
	public Optional<Course> getCourses(String shortDescription) {
		if(this.courses == null || this.courses.size() <= 0) {
			logger.info("Retrieving all Courses....");
			this.courses = this.courseRepo.findAll();
		}
		return courses.stream().filter(c -> c.getShortDescription().equals(shortDescription)).findFirst();
	}

   	public Optional<Teacher> getTeachers(String teacher) {
   		if(this.teachers == null || this.teachers.size() <= 0) {
   			logger.info("Retrieving all Teachers....");
   			this.teachers = this.teachersRepo.findAll();
   		}
		return teachers.stream().filter(t->t.getTeacherName().equals(teacher)).findFirst();
	}

   	public Optional<BatchDetails> getBatchDetails(Teacher teacher, Grades grade, Course course, TimeSlot timeslot, TrainingType trainingType, Term term){
   		this.batchDetails = this.batchDetailRepo.findByActive(true);
   		return batchDetails.stream().filter(bd -> bd.getTeacher().equals(teacher) && bd.getGrade().equals(grade) && 
   				bd.getCourse().equals(course) && bd.getBatch().getTimeSlot().equals(timeslot) && 
   				bd.getTrainingType().equals(trainingType) && bd.getTerm().equals(term)).findFirst();
   	}
   	
   	public Optional<Batch> getBatch(TimeSlot timeSlot){
   		this.batches = batchRepo.findAll();
   		return batches.stream().filter(b -> b.getTimeSlot().equals(timeSlot)).findFirst();
   	}
   	
   	public Optional<TrainingType> getTrainingType(String location){
   		if(this.trainingTypes == null || this.trainingTypes.size() <=0) {
   			logger.info("Retrieving all Training Types....");
   			this.trainingTypes = this.trainingTypeRepo.findAll();
   		}
   		return trainingTypes.stream().filter(tt -> tt.getType().equalsIgnoreCase(location)).findFirst();
   	}
   	
   	public Optional<Term> getCurrentTerm(){
   		if(this.terms == null || this.terms.size() <=0) {
   			logger.info("Retrieving current Term....");
   			this.terms = this.termRepo.findAll();
   		}
   		Date today = Calendar.getInstance().getTime();
   		return this.terms.stream().filter(t -> t.getStartDate().before(today) & t.getEndDate().after(today)).findFirst();
   	}
   	
   	public Term getTerm(int termNumber) {
   		return termRepo.findByTerm(termNumber).get(0);
   	}
   	
   	public Week getWeekByWeekNumber(int weekNumber) {
   		return weekRepo.findByWeekNumber(weekNumber).get(0);
   	}
   	
   	public List<BatchDetails> findAllOrphanBatchDetails(){
   		List<BatchDetails> batchDetails = null;
   		batchDetails = batchDetailRepo.findAllOrphans();
   		return batchDetails;
   	}
   	
   	@Transactional
    public void saveBatch(Batch batch) {
   		batchRepo.save(batch);
   	}
   	
   	@Transactional
   	public void saveBatch(List<Batch> batches) {
   		batchRepo.saveAll(batches);
   	}
   	
   	@Transactional
    public void saveBatchDetails(BatchDetails batcheDetail) {
   		batchDetailRepo.save(batcheDetail);
   	}
   	
   	@Transactional
   	public void saveBatchDetails(List<BatchDetails> batcheDetails) {
   		batchDetailRepo.saveAll(batcheDetails);
   	}
   	
   	private void checkAllRepos() {if(timeSlotRepo == null || gradeRepo == null || 
   				courseRepo ==null || teachersRepo == null || batchDetailRepo == null || batchRepo == null ||
   				trainingTypeRepo == null) {}}

	@Override
	public List<ScheduleTO> getSchedulesFor(Teacher teacher) throws ParseException, TSHException {
		
		List<ScheduleTO> schedules = new ArrayList<>();
		
		logger.info("Finding all batches for teacher : {}",teacher.getTeacherName());		
		
		List<BatchDetails> batches = batchDetailRepo.findAllByTeacherAndActive(teacher, true);
		
		for(BatchDetails batch : batches) {
			schedules.add(getBatchDetails(batch));
		}
		logger.info("{} batches fetched. Returning result",schedules.size());
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

		for(StudentBatches studentBatch : students) {
			StudentTO studentTO = new StudentTO();
			studentTO.setId(studentBatch.getId());
			studentTO.setName(studentBatch.getStudent().getStudentName());
			studentTO.setGrade(studentBatch.getStudent().getGrade().getGrade()+"");
			studentTO.setCourse(studentBatch.getCourse().getDescription());
			
			// Fetch the topic progress for last week based on startDate. This might return the the topic that might have been started last week but waS NOT COMPLETED.
			// Meaning -- A topic that was kept in progress would also be returned. Which is the correct behavior.
			TopicProgress topicProgress = progressService.getStudentLastTopicProgress(studentBatch.getStudent(), studentBatch.getCourse());			
			if(topicProgress != null) {
				studentTO.setPreviousTopic(topicProgress.getTopic().getDescription());
			
				List<StudentFeedback> feedbacks = feedbackService.getAllFeedbacks(studentBatch, topicProgress.getTopic());
				ModelMapper mapper = new ModelMapper();
			
				Teacher feedbackProvider = null;
				Calendar feedbackDate = Calendar.getInstance();
				
				if(feedbacks.size() > 0) {	//Feedbacks are sorted in descending order. The first one is the latest one.
					feedbackProvider = feedbacks.get(0).getTeacher();
					feedbackDate.setTime(feedbacks.get(0).getFeedbackDate());
				}

				// There can be multiple feedbacks provided for a topic by different teacher or by same teacher. 
				// Get only the latest feedbacks and provided by just one teacher.
				for(StudentFeedback studFeedback : feedbacks) {		
					Calendar fDate = Calendar.getInstance();
					fDate.setTime(studFeedback.getFeedbackDate());
					if(studFeedback.getTeacher().equals(feedbackProvider) && fDate.equals(feedbackDate)) {
						FeedbackCategoryTO categoryTO = mapper.map(studFeedback.getFeedback().getCategory(), FeedbackCategoryTO.class);
						categoryTO.setTeachersComment(studFeedback.getFeedbackText()); 			// For every feedback category the teachers comment is added to every student feedback record
						categoryTO.setFeedbacks(null); 											//We don't want the entire list of Feedbacks for every category here.
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
		
		currentTopic = mapper.map(progressService.getCurrentTopic(batch), TopicsTO.class);
		return currentTopic;
	}
	
	private TopicsTO getNextTopicOfBatch(BatchDetails batch) throws ParseException, TSHException {
		TopicsTO nextTopic = new TopicsTO();
		ModelMapper mapper = new ModelMapper();
		
		nextTopic = mapper.map(progressService.getNextTopic(batch), TopicsTO.class);
		return nextTopic;
	}
	
	private List<TopicsTO> fetchAllTopicsForCourse(BatchDetails batch){
		logger.info("Fetching all topics for the course :"  + batch.getCourse().getDescription() + " Grade : " + batch.getGrade().getGrade());
		List<TopicsTO> topicList = new ArrayList<>();
		ModelMapper mapper = new ModelMapper();
		topicList = feedbackService.getAllActiveTopicsForCourseAndGrade(batch.getCourse(), batch.getGrade()).stream().map(topic -> {
			TopicsTO topicTO = mapper.map(topic, TopicsTO.class);
			List<BatchProgress> batchProgress = progressService.getAllBatchProgress(batch);
			BatchProgress progress = batchProgress.stream().filter(bp -> bp.getTopic().equals(topic)).findFirst().orElse(null);
			if(progress != null) {
				topicTO.setStatus(progress.getStatus().getStatus());
				topicTO.setStartDate(progress.getStartDate());
				topicTO.setEndDate(progress.getEndDate());
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