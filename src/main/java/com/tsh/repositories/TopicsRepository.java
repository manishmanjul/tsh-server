package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.Course;
import com.tsh.entities.Grades;
import com.tsh.entities.Topics;

@Repository
public interface TopicsRepository extends JpaRepository<Topics, Integer> {

	public List<Topics> findByMappedId(String mappedId);

	public List<Topics> findAllByCourseAndGradeAndActive(Course course, Grades grade, boolean active);

	public List<Topics> findAllByActiveOrderByGradeId(boolean active);
}
