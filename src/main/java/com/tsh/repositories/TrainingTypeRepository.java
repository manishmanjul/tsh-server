package com.tsh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.TrainingType;

@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType, Integer>{
	
}
