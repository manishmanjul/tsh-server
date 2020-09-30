package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer>{

	public List<Feedback> findByShortDescription(String description);
}
