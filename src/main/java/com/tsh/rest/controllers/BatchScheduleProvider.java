package com.tsh.rest.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.Teacher;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.ScheduleTO;
import com.tsh.library.dto.TopicsTO;
import com.tsh.library.dto.UserPrinciple;
import com.tsh.service.IBatchService;
import com.tsh.service.ITeacherService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/tsh/schedule")
public class BatchScheduleProvider {

	@Autowired
	private IBatchService batchService;

	@Autowired
	private ITeacherService teacherService;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping
	public List<ScheduleTO> getTodaysBatchList() {

		List<ScheduleTO> batches = null;
		UserPrinciple principle = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Teacher teacher = teacherService.findByUser(principle.getUser());

		logger.info("Request recieved to get batches for teacher : {}", teacher.getTeacherName());
		try {
			batches = batchService.getSchedulesFor(teacher);
		} catch (ParseException e) {
			return new ArrayList<>();
		} catch (TSHException e) {
			return new ArrayList<>();
		}
		return batches;
	}

	@GetMapping("/getBatchTopics/{batchId}")
	public ResponseEntity<List<TopicsTO>> getBatchTopics(@PathVariable("batchId") String batchId) {
		ResponseEntity<List<TopicsTO>> response = null;
		logger.info("Request to fetch topics for batch : {}", batchId);
		try {
			BatchDetails batchDetails = batchService.getBatchDetailsById(Integer.parseInt(batchId));
			List<TopicsTO> topics = batchService.getBAtchTopics(batchDetails);

			HttpHeaders header = new HttpHeaders();
			header.set("statusCode", "200");
			header.set("message", "Success");
			response = new ResponseEntity<>(topics, header, HttpStatus.ACCEPTED);
			
		} catch (NumberFormatException e) {
			HttpHeaders header = new HttpHeaders();
			header.set("statusCode", "305");
			header.set("message", "Unable to find BatchDetails." + e.getMessage());
			response = new ResponseEntity<>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (TSHException e) {
			HttpHeaders header = new HttpHeaders();
			header.set("statusCode", "305");
			header.set("message", "Unable to find BatchDetails." + e.getMessage());
			response = new ResponseEntity<>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}
}
