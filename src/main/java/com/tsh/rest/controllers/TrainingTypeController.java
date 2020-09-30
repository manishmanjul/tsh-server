package com.tsh.rest.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsh.entities.TrainingType;
import com.tsh.service.impl.TrainingTypeService;

@RestController
@RequestMapping("trtypes")
public class TrainingTypeController {

	@Autowired
	private TrainingTypeService service;
	
	@GetMapping
	public List<TrainingType> getAllTrainigTypes(){
		return service.findAll();
	}
}
