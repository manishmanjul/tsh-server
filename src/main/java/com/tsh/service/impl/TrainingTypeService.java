package com.tsh.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsh.entities.TrainingType;
import com.tsh.repositories.TrainingTypeRepository;

@Service
public class TrainingTypeService {

	@Autowired
	private TrainingTypeRepository repository;
	
	public List<TrainingType> findAll(){
		return repository.findAll();
	}
	
}
