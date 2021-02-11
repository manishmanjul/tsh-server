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
import com.tsh.entities.Teacher;
import com.tsh.entities.User;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.StudentTO;

@Service
public interface IStudentService extends TshService {

	Optional<Student> getStudent(ImportItem item);

	public List<Student> getAllActiveStudents();

	Optional<StudentBatches> getActiveStudentBatchesForCourseCategory(Student student, Course course);

	List<StudentBatches> getStudentBatches(BatchDetails batch);

	List<StudentTO> getAllActiveStudentsForTeacher(Teacher teacher, User loggedinUser);

	List<StudentBatches> getAllActiveStudentBatches();

	public List<StudentBatches> getAllActiveStudentBatches(Teacher teacher);

	Student getStudentByNameAndGrade(String name, Grades grade);

	public StudentBatches getStudentBatchesById(int studentBatchId);

	int save(StudentBatches newStudentBatch);

	Student saveStudent(Student student);

	public void saveStudentBatches(List<StudentBatches> studentBatches);

	public void saveAllStudents(List<Student> students);

	public boolean markAbsent(StudentBatches studentBatch) throws TSHException;

	public List<StudentBatches> getAllStudentBatchesWithBatchDetailsStatus(boolean status);

	public boolean isEnrolledToABatch(Student student);

}
