package com.tsh.service;

import org.springframework.stereotype.Service;

import com.tsh.entities.Teacher;
import com.tsh.entities.User;

@Service
public interface ITeacherService extends TshService{

	Teacher findByName(String name);
	Teacher updateTeacher(Teacher teacher);
	Teacher findByUser(User user);	
	Teacher findById(int teacherId);
}
