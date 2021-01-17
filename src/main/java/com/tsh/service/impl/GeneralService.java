package com.tsh.service.impl;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.tsh.entities.Course;
import com.tsh.entities.Grades;
import com.tsh.entities.Term;
import com.tsh.entities.TimeSlot;
import com.tsh.entities.TrainingType;
import com.tsh.entities.Week;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.CourseTO;
import com.tsh.library.dto.GradeTO;
import com.tsh.library.dto.TermTO;
import com.tsh.library.dto.TopicManagerCourse;
import com.tsh.library.dto.TopicManagerSubject;
import com.tsh.library.dto.WeekTO;
import com.tsh.repositories.CourseRepository;
import com.tsh.repositories.GradesRepository;
import com.tsh.repositories.TermRepository;
import com.tsh.repositories.TimeSlotRepository;
import com.tsh.repositories.TrainingTypeRepository;
import com.tsh.repositories.WeekRepository;
import com.tsh.service.IGeneralService;
import com.tsh.utility.TshUtil;

@Service
public class GeneralService implements IGeneralService {

	private List<TimeSlot> timeSlots;
	private List<Course> courses;
	private List<Grades> grades;
	private List<TrainingType> trainingTypes;
	private List<Term> terms;

	@Autowired
	private TermRepository termRepo;
	@Autowired
	private WeekRepository weekRepo;
	@Autowired
	private GradesRepository gradeRepo;
	@Autowired
	private CourseRepository courseRepo;
	@Autowired
	private TimeSlotRepository timeSlotRepo;
	@Autowired
	private TrainingTypeRepository trainingTypeRepo;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public Term getTerm(int termNumber) {
		return termRepo.findByTerm(termNumber).get(0);
	}

	public Week getWeekByWeekNumber(int weekNumber) {
		return weekRepo.findByWeekNumber(weekNumber).get(0);
	}

	@Cacheable("TshCache")
	@Override
	public List<Term> findAllTerms() {
		return termRepo.findAll();
	}

	@Override
	public List<Week> getAllWeekRange(int start, int end) {
		return weekRepo.findByWeekNumberBetweenOrderByWeekNumber(start, end);
	}

	@Cacheable("TshCache")
	@Override
	public List<Grades> findAllGrades() {
		return gradeRepo.findAll();
	}

	@Cacheable("TshCache")
	@Override
	public List<Course> findAllCourse() {
		return courseRepo.findAll();
	}

	@Override
	public Optional<TimeSlot> getTimeSlot(int weekDay, Time batchTime, Time batchEndTime) {

		if (this.timeSlots == null || this.timeSlots.size() <= 0) {
			logger.info("Retrieving all TimeSlots....");
			this.timeSlots = this.timeSlotRepo.findAll();
		}
		return this.timeSlots.stream()
				.filter(t -> t.getWeekday() == weekDay & t.getStartTime().getTime() == batchTime.getTime()
						& t.getEndTime().getTime() == batchEndTime.getTime())
				.findFirst();
	}

	@Override
	public Optional<Course> getCourses(String shortDescription) {
		if (this.courses == null || this.courses.size() <= 0) {
			logger.info("Retrieving all Courses....");
			this.courses = this.findAllCourse();
		}
		return courses.stream().filter(c -> c.getShortDescription().equals(shortDescription)).findFirst();
	}

	@Override
	public Course getCourse(int id) throws TSHException {
		return courseRepo.findById(id).orElseThrow(() -> new TSHException("No Course found with Id : " + id));
	}

	public Optional<Grades> getGrades(int grade) {
		if (this.grades == null || this.grades.size() <= 0) {
			logger.info("Retrieving all Grades....");
			this.grades = this.findAllGrades();
		}
		return this.grades.stream().filter(t -> t.getGrade() == grade).findFirst();
	}

	public Optional<TrainingType> getTrainingType(String location) {
		if (this.trainingTypes == null || this.trainingTypes.size() <= 0) {
			logger.info("Retrieving all Training Types....");
			this.trainingTypes = this.findAllTrainingTypes();
		}
		return trainingTypes.stream().filter(tt -> tt.getType().equalsIgnoreCase(location)).findFirst();
	}

	public Optional<Term> getCurrentTerm() {
		if (this.terms == null || this.terms.size() <= 0) {
			logger.info("Retrieving current Term....");
			this.terms = this.findAllTerms();
		}
		Date today = Calendar.getInstance().getTime();
		return this.terms.stream().filter(t -> t.getStartDate().before(today) & t.getEndDate().after(today))
				.findFirst();
	}

	@Cacheable("TshCache")
	@Override
	public List<TrainingType> findAllTrainingTypes() {
		return trainingTypeRepo.findAll();
	}

	/**
	 * Fetch All terms from the database and set the current term as true. If today
	 * is between the term start and end date, then it is the current term. Return a
	 * list of TermTO.
	 */
	@Override
	public List<TermTO> findAllTermsAsTO() {
		List<TermTO> returnList = null;
		List<Term> termList = null;
		termList = this.findAllTerms();
		logger.info("Fetched {} records for term.", termList.size());
		ModelMapper mapper = new ModelMapper();

		returnList = termList.stream().map(term -> {
			TermTO termTO = mapper.map(term, TermTO.class);
			if (TshUtil.isTodayInRange(term.getStartDate(), term.getEndDate())) {
				logger.info("Current term is : Term-{}", term.getTerm());
				termTO.setCurrent(true);
			}
			return termTO;
		}).collect(Collectors.toList());

		return returnList;
	}

	/**
	 * Fetch all grades from the database. Returns a list of GradeTO.
	 */
	@Override
	public List<GradeTO> findAllGradesAsTO() {
		logger.info("Fetching all Grades.");
		List<Grades> gradeList = this.findAllGrades();
		logger.info("Found {} grades.", gradeList.size());

		ModelMapper mapper = new ModelMapper();
		List<GradeTO> returnList = gradeList.stream().map(grades -> {
			GradeTO gradeTo = mapper.map(grades, GradeTO.class);
			return gradeTo;
		}).collect(Collectors.toList());

		return returnList;
	}

	/**
	 * At this point we do not have a way to find all course types from the DB. So
	 * this method for now will return a list of Strings. A list of hard coded
	 * course types
	 */
	@Override
	public List<TopicManagerCourse> findAllCourseTypes() {
		logger.info("Fetching all course details...");
		List<Course> courseTypes = new ArrayList<>();
		courseTypes = this.findAllCourse();
		List<TopicManagerCourse> courseCatList = new ArrayList<>();

		for (Course c : courseTypes) {
			String desc = c.getDescription();
			String shortDesc = c.getShortDescription();
			String courseCat = "";
			String shortCat = "";
			String subjectStr = "";
			StringTokenizer token = new StringTokenizer(desc, " ");
			StringTokenizer shortToken = new StringTokenizer(shortDesc, " ");

			if (shortToken.countTokens() == 1) {
				courseCat = "Standard";
				shortCat = "Std";
				subjectStr = desc;
			} else if (shortToken.countTokens() == 2) {
				int tokenCount = token.countTokens();
				for (int i = 1; i < tokenCount; i++) {
					subjectStr = subjectStr + " " + token.nextToken();
				}
				courseCat = token.nextToken();
				shortToken.nextToken();
				shortCat = shortToken.nextToken();
				subjectStr = subjectStr.trim();
			}

			TopicManagerCourse cc = new TopicManagerCourse(courseCat, shortCat);
			TopicManagerSubject subject = new TopicManagerSubject(subjectStr);
			subject.setId(c.getId());

			if (courseCatList.contains(cc)) {
				int courseIdx = courseCatList.indexOf(cc);
				courseCatList.get(courseIdx).addSubject(subject);
			} else {
				cc.addSubject(subject);
				courseCatList.add(cc);
			}

			courseCatList.get(courseCatList.indexOf(cc)).getSubjects().sort(subject);
		}
		logger.info("{} - courses fetched.", courseTypes.size());
		return courseCatList;
	}

	@Override
	public List<WeekTO> getAllWeekRangeAsTO(int start, int end) {
		List<Week> weeks = getAllWeekRange(start, end);
		ModelMapper mapper = new ModelMapper();

		List<WeekTO> weeksTO = weeks.stream().map(w -> {
			WeekTO weekTO = mapper.map(w, WeekTO.class);
			return weekTO;
		}).collect(Collectors.toList());

		logger.info("{} weeks fetched", weeksTO.size());
		return weeksTO;
	}

	@Override
	public List<CourseTO> findAllCourseAsTO() {
		ModelMapper mapper = new ModelMapper();
		List<Course> courseList = findAllCourse();
		List<CourseTO> courseListTO = courseList.stream().map(c -> {
			CourseTO courseTO = mapper.map(c, CourseTO.class);
			return courseTO;
		}).collect(Collectors.toList());

		logger.info("{} courses fetched", courseListTO.size());
		return courseListTO;
	}

}
