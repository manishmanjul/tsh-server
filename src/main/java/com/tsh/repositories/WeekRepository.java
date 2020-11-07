package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.Week;

@Repository
public interface WeekRepository extends JpaRepository<Week, Integer> {
	public List<Week> findByWeekNumber(int weekNumber);

	public List<Week> findByWeekNumberBetweenOrderByWeekNumber(int start, int end);
}
