package com.tsh.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.tsh.entities.Teacher;
import com.tsh.entities.TeacherDetails;
import com.tsh.entities.User;
import com.tsh.repositories.TeacherDetailsRepository;
import com.tsh.repositories.TeachersRepository;
import com.tsh.service.ITeacherService;

@Service
public class TeacherServiceImpl implements ITeacherService {

	private List<Teacher> teachers;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TeachersRepository teacherRepo;
	@Autowired
	private TeacherDetailsRepository teacherDetailsRepo;

	@Override
	public Teacher findByName(String name) {
		return teacherRepo.findByTeacherName(name);
	}

	@Override
	public Teacher updateTeacher(Teacher teacher) {
		return teacherRepo.save(teacher);
	}

	@Override
	public Teacher findByUser(User user) {
		return teacherRepo.findByUser(user);
	}

	@Override
	public Teacher findById(int teacherId) {
		return teacherRepo.findById(teacherId).orElse(null);
	}

	@Cacheable("TshCache")
	@Override
	public List<Teacher> findAllTeachers() {
		return teacherRepo.findAll();
	}

	public Optional<Teacher> getTeachers(String teacher) {
		if (this.teachers == null || this.teachers.size() <= 0) {
			logger.info("Retrieving all Teachers....");
			this.teachers = this.findAllTeachers();
		}
		return teachers.stream().filter(t -> t.getTeacherName().equals(teacher)).findFirst();
	}

	@Override
	public TeacherDetails getTeacherDetails(Teacher teacher) {
		return teacherDetailsRepo.findByTeacher(teacher);
	}
}
