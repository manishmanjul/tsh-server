package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.TopicStatus;

@Repository
public interface TopicStatusRepository extends JpaRepository<TopicStatus, Integer>{
	public TopicStatus findByStatus(String status);
	public List<TopicStatus> findByStatusIn(List<String> status);
}
