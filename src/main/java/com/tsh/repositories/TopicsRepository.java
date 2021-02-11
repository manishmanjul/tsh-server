package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.Course;
import com.tsh.entities.Grades;
import com.tsh.entities.Term;
import com.tsh.entities.Topics;
import com.tsh.entities.Week;

@Repository
public interface TopicsRepository extends JpaRepository<Topics, Integer> {

	public List<Topics> findByMappedId(String mappedId);

	public List<Topics> findAllByCourseAndGradeAndTermAndActive(Course course, Grades grade, Term term, boolean active);

	public List<Topics> findAllByActiveOrderByGradeId(boolean active);

	public List<Topics> findByGradeAndCourseAndTermAndWeekAndActive(Grades grade, Course course, Term term, Week week,
			boolean active);
}
