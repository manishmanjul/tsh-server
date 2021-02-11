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
import com.tsh.service.IConverter;
import com.tsh.service.ITeacherService;

@RestController
@RequestMapping("/tsh/schedule")
public class BatchScheduleProvider {

	@Autowired
	private IBatchService batchService;

	@Autowired
	private ITeacherService teacherService;
	@Autowired
	private IConverter converter;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

//	@GetMapping
//	public List<ScheduleTO> getTodaysBatchList() {
//
//		List<ScheduleTO> batches = null;
//		UserPrinciple principle = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		Teacher teacher = teacherService.findByUser(principle.getUser());
//		if (teacher != null)
//			logger.info("Request recieved to get batches for teacher : {}", teacher.getTeacherName());
//		else {
//			logger.info("Logged in as Admin");
//		}
//		try {
//			batches = batchService.getSchedulesFor(teacher, principle.getUser());
//		} catch (ParseException e) {
//			return new ArrayList<>();
//		} catch (TSHException e) {
//			return new ArrayList<>();
//		}
//		return batches;
//	}

	@GetMapping
	public List<ScheduleTO> getTodaysBatchList() {

		List<ScheduleTO> batches = null;
		List<BatchDetails> batchDetails = null;
		UserPrinciple principle = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Teacher teacher = teacherService.findByUser(principle.getUser());
		if (teacher != null)
			logger.info("Request recieved to get batches for teacher : {}", teacher.getTeacherName());
		else {
			logger.info("Logged in as Admin");
		}
		try {
			batchDetails = batchService.getAllBatchDetailsForUser(principle.getUser(), teacher);
			logger.info("{} batches fetched.", batchDetails.size());
			logger.info("Converting Batch Data to object streams..");
			batches = converter.convertToScheduleTO(batchDetails, principle.getUser());
			logger.info("Sent {} objects to client", batches.size());
		} catch (TSHException e) {
			logger.error(e.getMessage());
			logger.error(e.getLocalizedMessage());
			return new ArrayList<>();
		}
		return batches;
	}

	@GetMapping("/getBatchTopics/{batchId}")
	public ResponseEntity<List<TopicsTO>> getBatchTopics(@PathVariable("batchId") String batchId) {
		ResponseEntity<List<TopicsTO>> response = null;
		logger.info("\"tsh/schedule/getBatchData\"  called for batch : {}", batchId);
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

	@GetMapping("/getBatchData/{batchId}")
	public ResponseEntity<ScheduleTO> getBatchData(@PathVariable("batchId") String batchId) {
		ResponseEntity<ScheduleTO> response = null;
		logger.info("getBatchData called for batch ID : {}", batchId);
		try {
			BatchDetails batchDetails = batchService.getBatchDetailsById(Integer.parseInt(batchId));
			ScheduleTO schedule = batchService.getBatchInfoToRender(batchDetails);
			logger.info("Found all data for batch : {} at {}", batchDetails.getBatchName(),
					batchDetails.getBatch().getTimeSlot().getStartTime());
			logger.info("Teacher : {}, Attendies : {}", schedule.getTeacherName(), schedule.getAttendies());
			HttpHeaders header = new HttpHeaders();
			header.set("statusCode", "200");
			header.set("message", "Success");
			response = new ResponseEntity<>(schedule, header, HttpStatus.ACCEPTED);

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
		} catch (ParseException e) {
			e.printStackTrace();
			HttpHeaders header = new HttpHeaders();
			header.set("statusCode", "305");
			header.set("message", "Unable to find BatchDetails." + e.getMessage());
			response = new ResponseEntity<>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}
}
