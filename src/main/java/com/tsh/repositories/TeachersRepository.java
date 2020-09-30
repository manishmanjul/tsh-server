package com.tsh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.Teacher;
import com.tsh.entities.User;

@Repository
public interface TeachersRepository extends JpaRepository<Teacher, Integer>{
	
	Teacher findByTeacherName(String name);
	Teacher findByUser(User user);
}
