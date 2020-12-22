package com.tsh.service;

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
import com.tsh.exception.TSHException;
import com.tsh.library.dto.ScheduleTO;
import com.tsh.library.dto.TopicsTO;

@Service
public interface IBatchService extends TshService {

	List<BatchDetails> findAllOrphanBatchDetails();

	void saveBatchDetails(List<BatchDetails> batchDetails);

	Batch saveBatch(Batch batch);

	BatchDetails saveBatchDetails(BatchDetails batchDetails);

	Optional<BatchDetails> getBatchDetails(Teacher teacher, Grades grade, Course course, TimeSlot timeSlot,
			TrainingType trainingType, Term term);

	Optional<Batch> getBatch(TimeSlot timeSlot);

	List<ScheduleTO> getSchedulesFor(Teacher teacher) throws ParseException, TSHException;

	public List<TopicsTO> getBAtchTopics(BatchDetails batch);

	ScheduleTO getBatchDetails(BatchDetails batchDetails) throws TSHException, ParseException;

	BatchDetails getBatchDetailsById(int id) throws TSHException;

}
