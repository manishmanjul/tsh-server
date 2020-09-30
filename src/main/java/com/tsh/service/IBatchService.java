package com.tsh.service;

import java.sql.Time;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tsh.entities.Batch;
import com.tsh.entities.BatchDetails;
import com.tsh.entities.Course;
import com.tsh.entities.Grades;
import com.tsh.entities.Teacher;
import com.tsh.entities.Term;
import com.tsh.entities.TimeSlot;
import com.tsh.entities.TrainingType;
import com.tsh.entities.Week;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.ScheduleTO;

@Service
public interface IBatchService extends TshService{

	List<BatchDetails> findAllOrphanBatchDetails();

	void saveBatchDetails(List<BatchDetails> batchDetails);

	Optional<TimeSlot> getTimeSlot(int batchWeekDay, Time batchStartTime);

	Optional<Grades> getGrades(int gradeNumber);

	Optional<Course> getCourses(String subject);

	Optional<Teacher> getTeachers(String teacher);

	Optional<TrainingType> getTrainingType(String location);

	Optional<Term> getCurrentTerm();

	Optional<BatchDetails> getBatchDetails(Teacher teacher, Grades grade, Course course, TimeSlot timeSlot,
			TrainingType trainingType, Term term);

	Optional<Batch> getBatch(TimeSlot timeSlot);
	
	List<ScheduleTO> getSchedulesFor(Teacher teacher) throws ParseException, TSHException;
	
	Week getWeekByWeekNumber(int weekNumber);
	
	Term getTerm(int termNumber);
	
	BatchDetails getBatchDetailsById(int id) throws TSHException;
}
