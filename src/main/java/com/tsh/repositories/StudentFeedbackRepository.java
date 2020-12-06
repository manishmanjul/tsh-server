package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.StudentBatches;
import com.tsh.entities.StudentFeedback;
import com.tsh.entities.Teacher;
import com.tsh.entities.Topics;

@Repository
public interface StudentFeedbackRepository extends JpaRepository<StudentFeedback, Integer> {

	public List<StudentFeedback> findByStudentBatchesAndTopic(StudentBatches studentBatches, Topics topic);

	public List<StudentFeedback> findByStudentBatchesAndTopicIdOrderByTeacher(StudentBatches studentBatches,
			int topicId);

	public List<StudentFeedback> findByStudentBatchesAndTopicAndTeacher(StudentBatches studentBatches, Topics topic,
			Teacher teacher);

	public void deleteByStudentBatchesAndTopicAndTeacher(StudentBatches studentBatches, Topics topic, Teacher teacher);
}
