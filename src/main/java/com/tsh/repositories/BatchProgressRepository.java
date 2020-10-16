package com.tsh.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.BatchProgress;
import com.tsh.entities.Course;
import com.tsh.entities.TopicStatus;
import com.tsh.entities.Topics;

@Repository
public interface BatchProgressRepository extends JpaRepository<BatchProgress, Integer>{

	public List<BatchProgress> findAllByBatchDetails(BatchDetails batchDetails); 
	public List<BatchProgress> findAllByBatchDetailsOrderByStartDateDesc(BatchDetails batchDetails);
	public List<BatchProgress> findByBatchDetailsAndStartDateBetween(BatchDetails batchDetails, Date startDate, Date endDate);
	public List<BatchProgress> findByBatchDetailsAndPlannedStartDateBetween(BatchDetails batchDetails, Date startDate, Date endDate);
	public BatchProgress findByBatchDetailsAndTopicAndCourseAndStatusNot(BatchDetails batchDetails, Topics topics, Course course, TopicStatus status);
	public List<BatchProgress> findByBatchDetailsAndStatusOrderByStartDateDesc(BatchDetails batchDetails, TopicStatus status);
	public List<BatchProgress> findByBatchDetailsAndStatusOrderByPlannedStartDateDesc(BatchDetails batchDetails, TopicStatus status);
}
