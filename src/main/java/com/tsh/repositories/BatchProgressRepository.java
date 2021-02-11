package com.tsh.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.BatchProgress;
import com.tsh.entities.Course;
import com.tsh.entities.TopicStatus;
import com.tsh.entities.Topics;

@Repository
public interface BatchProgressRepository extends JpaRepository<BatchProgress, Integer> {

	public List<BatchProgress> findAllByBatchDetails(BatchDetails batchDetails);

	public List<BatchProgress> findAllByBatchDetailsOrderByStartDateDesc(BatchDetails batchDetails);

	public List<BatchProgress> findByBatchDetailsAndStartDateBetween(BatchDetails batchDetails, Date startDate,
			Date endDate);

	public List<BatchProgress> findByBatchDetailsAndPlannedStartDateBetween(BatchDetails batchDetails, Date startDate,
			Date endDate);

	public BatchProgress findByBatchDetailsAndTopicAndCourseAndStatusNot(BatchDetails batchDetails, Topics topics,
			Course course, TopicStatus status);

	public List<BatchProgress> findByBatchDetailsAndStatusOrderByStartDateDesc(BatchDetails batchDetails,
			TopicStatus status);

	public List<BatchProgress> findByBatchDetailsAndStatusOrderByPlannedStartDateDesc(BatchDetails batchDetails,
			TopicStatus status);

	public BatchProgress findByBatchDetailsAndPlannedStartDate(BatchDetails batchDetails, Date plannedStartDate);

	public List<BatchProgress> findAllByPlannedStartDate(Date plannedStartDate);

	public List<BatchProgress> findByBatchDetailsAndPlannedStartDateGreaterThanOrderByPlannedStartDate(
			BatchDetails batchDetails, Date plannedStartDate);

	@Query(value = "select * from tsh.batch_progress b1 where "
			+ "b1.topic_id in (select distinct(topic_id) from tsh.batch_progress b2 where b2.batch_detail_id = :batchDetailId) and "
			+ "b1.status = (select max(status) from tsh.batch_progress b3 where b3.topic_id = b1.topic_id and b3.batch_detail_id = :batchDetailId) "
			+ "and b1.topic_id is not null and b1.batch_detail_id = :batchDetailId", nativeQuery = true)
	public List<BatchProgress> findAllUniqueBatchProgressWithLastStatus(@Param("batchDetailId") int batchDetailId);

	@Query(value = "select * from tsh.batch_progress b where b.id in "
			+ "(select max(b2.id) from batch_progress b2 where (b2.planned_startdate >= :plannedDate or b2.startdate >= :plannedDate) "
			+ "and b2.teacher_id = :teacherId group by b2.batch_detail_id);", nativeQuery = true)
	public List<BatchProgress> findMaxRecordsForTeacherWithPlannedStartDateOrStartDateGreaterThan(
			@Param("plannedDate") Date plannedDate, @Param("teacherId") int teacherId);

	@Query(value = "select * from tsh.batch_progress b where b.id in "
			+ "(select max(b2.id) from batch_progress b2 where (b2.planned_startdate >= :plannedDate or b2.startdate >= :plannedDate) "
			+ "group by b2.batch_detail_id);", nativeQuery = true)
	public List<BatchProgress> findAllMaxRecordsWithPlannedStartDateOrStartDateGreaterThan(
			@Param("plannedDate") Date plannedDate);

}
