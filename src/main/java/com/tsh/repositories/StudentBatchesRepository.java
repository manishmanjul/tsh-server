package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.Course;
import com.tsh.entities.Student;
import com.tsh.entities.StudentBatches;
import com.tsh.entities.Teacher;

@Repository
public interface StudentBatchesRepository extends JpaRepository<StudentBatches, Integer> {

	public List<StudentBatches> findByStudent(Student student);

	public List<StudentBatches> findByBatchDetails(BatchDetails batchDetails);

	public List<StudentBatches> findByStudentAndCourse(Student stud, Course course);

	public List<StudentBatches> findAllByBatchDetailsTeacherAndEndDateIsNull(Teacher teacher);

	public List<StudentBatches> findAllByEndDateIsNull();

	public List<StudentBatches> findAllByBatchDetailsActive(boolean active);

	public List<StudentBatches> findAllByStudentAndEndDateIsNull(Student student);

	public List<StudentBatches> findAllByBatchDetailsAndEndDateIsNull(BatchDetails batchDetails);

}
