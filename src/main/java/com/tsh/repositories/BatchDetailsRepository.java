package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.Teacher;

@Repository
public interface BatchDetailsRepository extends JpaRepository<BatchDetails, Integer> {

	public List<BatchDetails> findByActive(boolean active);

	@Query(value = "select b.* from batch_details b LEFT join student_batches s on b.id = s.batch_details_id "
			+ "where b.active = 1 AND (s.batch_details_id is null or s.end_date is not null);", nativeQuery = true)
	public List<BatchDetails> findAllOrphans();

	@Query("Select bd from BatchDetails bd join bd.batch b join b.timeSlot t where t.weekday = :weekDay and bd.active = 1")
	public List<BatchDetails> findAllActiveBatchesForWeekday(@Param("weekDay") int weekDay);

	public List<BatchDetails> findAllByTeacherAndActive(Teacher teacher, boolean active);

	public BatchDetails findById(int id);

	public List<BatchDetails> findByIdIn(List<Integer> idList);
}
