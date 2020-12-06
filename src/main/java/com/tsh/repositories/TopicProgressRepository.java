package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.Course;
import com.tsh.entities.Student;
import com.tsh.entities.TopicProgress;
import com.tsh.entities.TopicStatus;
import com.tsh.entities.Topics;

@Repository
public interface TopicProgressRepository extends JpaRepository<TopicProgress, Integer> {

	public List<TopicProgress> findByStudentAndCourseOrderByStartDateDesc(Student student, Course course);

	public TopicProgress findByStudentAndCourseAndTopicAndStatusNot(Student student, Course course, Topics topic,
			TopicStatus status);

	public List<TopicProgress> findByStudentAndCourseCategoryOrderByStartDateDesc(Student student, int category);
}
