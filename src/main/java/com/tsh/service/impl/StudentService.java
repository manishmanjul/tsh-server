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

import com.tsh.entities.BatchDetails;
import com.tsh.entities.Course;
import com.tsh.entities.Grades;
import com.tsh.entities.ImportItem;
import com.tsh.entities.Student;
import com.tsh.entities.StudentBatches;
import com.tsh.entities.Teacher;
import com.tsh.library.dto.StudentTO;
import com.tsh.repositories.StudentBatchesRepository;
import com.tsh.repositories.StudentRepository;
import com.tsh.service.IStudentService;

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

	public Optional<Student> getStudent(ImportItem item) {
		logger.info("Finding Student...");
		this.students = this.studentRepo.findAll();
		return students.stream().filter(s -> s.getStudentName().equals(item.getName())
				&& s.getGrade().getGrade() == item.getGradeNumber() && s.isActive()).findFirst();
	}

	public Optional<StudentBatches> getStudentBatches(Student student, Course course) {
		logger.info("Finding student's existing batches...");
		this.studentBatches = this.studentBatchesRepo.findByStudent(student);
		return this.studentBatches.stream().filter(sd -> sd.getCourse().equals(course)).findFirst();
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

	@Override
	public List<StudentTO> getStudentsForTeacher(Teacher teacher) {
		List<StudentTO> studentTOList = new ArrayList<>();
		List<StudentBatches> studentBatches = studentBatchesRepo.findAllByBatchDetailsTeacher(teacher);
		for (StudentBatches studBatch : studentBatches) {
			if (studBatch.getStudent().isActive()) {
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
}
