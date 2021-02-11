package com.tsh.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tsh.entities.Attendence;
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
import com.tsh.repositories.AttendenceRepository;
import com.tsh.repositories.StudentBatchesRepository;
import com.tsh.repositories.StudentRepository;
import com.tsh.service.IStudentService;
import com.tsh.utility.TshUtil;

@Service
public class StudentService implements IStudentService {

//	private static StudentService studentService = null;	
	private List<Student> students;
	private List<StudentBatches> studentBatches;

	private StudentService() {
		checkAllRepos();
	}

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private StudentRepository studentRepo;
	@Autowired
	private StudentBatchesRepository studentBatchesRepo;
	@Autowired
	private AttendenceRepository attendenceRepo;

	public Optional<Student> getStudent(ImportItem item) {
		logger.info("Finding Student...");
		this.students = this.studentRepo.findAll();
		return students.stream().filter(s -> s.getStudentName().equals(item.getName())
				&& s.getGrade().getGrade() == item.getGradeNumber() && s.isActive()).findFirst();
	}

	public Optional<StudentBatches> getActiveStudentBatchesForCourseCategory(Student student, Course course) {
		logger.info("Finding student's existing batches for course category {}", course.getCategoryString());
		this.studentBatches = this.studentBatchesRepo.findByStudent(student);
		return this.studentBatches.stream()
				.filter(sd -> sd.getCourse().getCategory() == course.getCategory() && sd.getEndDate() == null)
				.findFirst();
	}

	public Student getStudentByNameAndGrade(String name, Grades grade) {
		return studentRepo.findByStudentNameAndGrade(name, grade).get(0);
	}

	@Transactional(propagation = Propagation.NESTED)
	public int save(StudentBatches studentBatches) {
		StudentBatches returnedItem = studentBatchesRepo.save(studentBatches);
		return returnedItem == null ? 0 : 1;
	}

	private void checkAllRepos() {
		if (studentRepo == null || studentBatchesRepo == null) {
		}
	}

	@Override
	public List<StudentBatches> getStudentBatches(BatchDetails batch) {
		return studentBatchesRepo.findByBatchDetails(batch);
	}

	@Override
	public StudentBatches getStudentBatchesById(int studentBatchId) {
		return studentBatchesRepo.findById(studentBatchId).orElse(null);
	}

	@Override
	public Student saveStudent(Student student) {
		return studentRepo.save(student);
	}

	/**
	 * In case the logged in user is an admin. Return all students.
	 */
	@Override
	public List<StudentTO> getAllActiveStudentsForTeacher(Teacher teacher, User loggedinUser) {
		List<StudentTO> studentTOList = new ArrayList<>();
		List<StudentBatches> studentBatches = null;
		if (loggedinUser.isAdmin()) {
			studentBatches = getAllActiveStudentBatches();
		} else if (loggedinUser.isTeacher1() || loggedinUser.isTeacher2()) {
			studentBatches = studentBatchesRepo.findAllByBatchDetailsTeacherAndEndDateIsNull(teacher);
		}
		for (StudentBatches studBatch : studentBatches) {
			if (studBatch.getStudent().isActive() && studBatch.getEndDate() == null) {
				StudentTO studentTO = new StudentTO();
				studentTO.setId(studBatch.getId());
				studentTO.setName(studBatch.getStudent().getStudentName());
				studentTO.setGrade(studBatch.getBatchDetails().getGrade().getGrade() + "");
				studentTO.setCourse(studBatch.getCourse().getShortDescription());
				studentTOList.add(studentTO);
			}
		}
		return studentTOList;
	}

	@Override
	public List<StudentBatches> getAllActiveStudentBatches() {
		return studentBatchesRepo.findAllByEndDateIsNull();
	}

	@Override
	public List<StudentBatches> getAllActiveStudentBatches(Teacher teacher) {
		return studentBatchesRepo.findAllByBatchDetailsTeacherAndEndDateIsNull(teacher);
	}

	@Override
	public boolean markAbsent(StudentBatches studentBatch) throws TSHException {
		Attendence attnd = new Attendence();
		attnd.setStudent(studentBatch);
		attnd.setAbsenseDate(TshUtil.format(TshUtil.getCurrentDate()));

		attnd = attendenceRepo.save(attnd);
		if (attnd == null)
			return false;

		return true;
	}

	@Override
	public List<StudentBatches> getAllStudentBatchesWithBatchDetailsStatus(boolean status) {
		return studentBatchesRepo.findAllByBatchDetailsActive(status);
	}

	@Transactional
	public void saveStudentBatches(List<StudentBatches> studentBatches) {
		studentBatchesRepo.saveAll(studentBatches);
	}

	@Override
	public List<Student> getAllActiveStudents() {
		return studentRepo.findAllByActive(true);
	}

	@Override
	public boolean isEnrolledToABatch(Student student) {
		List<StudentBatches> batches = studentBatchesRepo.findAllByStudentAndEndDateIsNull(student);
		if (batches.size() > 0)
			return true;
		else
			return false;
	}

	@Override
	public void saveAllStudents(List<Student> students) {
		studentRepo.saveAll(students);
	}

}
