package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.Grades;
import com.tsh.entities.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
	public List<Student> findByStudentNameAndGrade(String studName, Grades grade);

	public List<Student> findAllByActive(boolean active);
}
