package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.StudentBatches;
import com.tsh.entities.StudentFeedback;
import com.tsh.entities.Teacher;
import com.tsh.entities.TopicProgress;
import com.tsh.entities.Topics;
import com.tsh.entities.User;

@Repository
public interface StudentFeedbackRepository extends JpaRepository<StudentFeedback, Integer> {

	public List<StudentFeedback> findByStudentBatchesAndTopic(StudentBatches studentBatches, Topics topic);

	public List<StudentFeedback> findByStudentBatchesAndTopicIdOrderByFeedbackDate(StudentBatches studentBatches,
			int topicId);

	public List<StudentFeedback> findByStudentBatchesAndTopicAndTeacher(StudentBatches studentBatches, Topics topic,
			Teacher teacher);

	public void deleteByStudentBatchesAndTopicAndTeacher(StudentBatches studentBatches, Topics topic, Teacher teacher);

	public List<StudentFeedback> findByTopicProgressAndUserAndStudentBatches(TopicProgress topicProgress, User user,
			StudentBatches studentBatches);

	public List<StudentFeedback> findByTopicProgressAndStudentBatches(TopicProgress topicProgress,
			StudentBatches studentBatches);
}
