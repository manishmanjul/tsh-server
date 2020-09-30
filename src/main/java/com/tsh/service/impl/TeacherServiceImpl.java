package com.tsh.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsh.entities.Teacher;
import com.tsh.entities.User;
import com.tsh.repositories.TeachersRepository;
import com.tsh.service.ITeacherService;

@Service
public class TeacherServiceImpl implements ITeacherService{

	@Autowired
	private TeachersRepository teacherRepo;
	
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
}
