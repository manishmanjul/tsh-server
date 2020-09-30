package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.Course;
import com.tsh.entities.Student;
import com.tsh.entities.StudentBatches;

@Repository
public interface StudentBatchesRepository extends JpaRepository<StudentBatches, Integer>{
	
	public List<StudentBatches> findByStudent(Student student);
	
	public List<StudentBatches> findByBatchDetails(BatchDetails batchDetails);
	
	public List<StudentBatches> findByStudentAndCourse(Student stud, Course course);
}
