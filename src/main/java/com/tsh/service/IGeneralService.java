package com.tsh.service;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tsh.entities.Course;
import com.tsh.entities.Grades;
import com.tsh.entities.Term;
import com.tsh.entities.TimeSlot;
import com.tsh.entities.TrainingType;
import com.tsh.entities.Week;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.GradeTO;
import com.tsh.library.dto.TermTO;
import com.tsh.library.dto.TopicManagerCourse;

@Service
public interface IGeneralService {

	/********************************
	 * Term related Service *
	 ********************************/
	Term getTerm(int termNumber);

	List<Term> findAllTerms();

	public List<TermTO> findAllTermsAsTO();

	public Optional<Term> getCurrentTerm();

	/********************************
	 * Week related Service *
	 ********************************/
	List<Week> getAllWeekRange(int start, int end);

	Week getWeekByWeekNumber(int weekNumber);

	/********************************
	 * Grade related Service *
	 ********************************/
	List<Grades> findAllGrades();

	public List<GradeTO> findAllGradesAsTO();

	Optional<Grades> getGrades(int grade);

	/********************************
	 * Course related Service *
	 ********************************/
	List<Course> findAllCourse();

	Course getCourse(int id) throws TSHException;

	public List<TopicManagerCourse> findAllCourseTypes();

	Optional<Course> getCourses(String shortDescription);

	/********************************
	 * TimeSlot related Service *
	 ********************************/
	Optional<TimeSlot> getTimeSlot(int batchWeekDay, Time batchStartTime);

	/********************************
	 * TrainingType related Service *
	 ********************************/
	List<TrainingType> findAllTrainingTypes();

	public Optional<TrainingType> getTrainingType(String location);
}
