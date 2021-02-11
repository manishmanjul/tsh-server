package com.tsh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.Teacher;
import com.tsh.entities.TeacherDetails;

@Repository
public interface TeacherDetailsRepository extends JpaRepository<TeacherDetails, Integer> {

	public TeacherDetails findByTeacher(Teacher teacher);
}
