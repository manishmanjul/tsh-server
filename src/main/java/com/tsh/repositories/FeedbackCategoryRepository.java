package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.FeedbackCategory;

@Repository
public interface FeedbackCategoryRepository extends JpaRepository<FeedbackCategory, Integer> {

	public List<FeedbackCategory> findByGradeAndActiveOrderByOrder(int grade, boolean active);

	public List<FeedbackCategory> findByGradeOrderByOrder(int grade);
}
