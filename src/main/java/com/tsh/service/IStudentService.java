package com.tsh.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.Course;
import com.tsh.entities.Grades;
import com.tsh.entities.ImportItem;
import com.tsh.entities.Student;
import com.tsh.entities.StudentBatches;

@Service
public interface IStudentService extends TshService{

	Optional<Student> getStudent(ImportItem item);

	Optional<StudentBatches> getStudentBatches(Student student, Course course);
	
	List<StudentBatches> getStudentBatches(BatchDetails batch);

	Student getStudentByNameAndGrade(String name, Grades grade);
	
	public StudentBatches getStudentBatchesById(int studentBatchId);
	
	int save(StudentBatches newStudentBatch);

}
