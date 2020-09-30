package com.tsh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.Grades;

@Repository
public interface GradesRepository extends JpaRepository<Grades, Integer>{
	public Grades findByGrade(int grade);
}
