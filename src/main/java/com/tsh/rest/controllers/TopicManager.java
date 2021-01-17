package com.tsh.rest.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsh.exception.TSHException;
import com.tsh.library.TopicCompartor;
import com.tsh.library.dto.CreateTopicRequest;
import com.tsh.library.dto.ResponseMessage;
import com.tsh.library.dto.TopicGenerationRequest;
import com.tsh.library.dto.TopicManagerResponse;
import com.tsh.library.dto.TopicRequest;
import com.tsh.library.dto.TopicResponse;
import com.tsh.service.IGeneralService;
import com.tsh.service.ITopicService;

@RestController
@RequestMapping("/tsh/topicmanager")
public class TopicManager {

	@Autowired
	private ITopicService topicService;
	@Autowired
	private IGeneralService generalService;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/getTopicData")
	public TopicManagerResponse fetchTopicData() {

		TopicManagerResponse response = new TopicManagerResponse();

		logger.info("Request to fetch all terms received...");

		response.setTerms(generalService.findAllTermsAsTO());
		response.setGrades(generalService.findAllGradesAsTO());
		response.setCourse(generalService.findAllCourseTypes());
		response.setrCourse(generalService.findAllCourseAsTO());
		response.setWeek(generalService.getAllWeekRangeAsTO(1, 12));
		return response;
	}

	@PostMapping("/submit")
	public ResponseEntity<?> generateTopics(@RequestBody TopicGenerationRequest topicGenerationRequest) {
		logger.info("Request to generate new topics received..");
		ResponseMessage response = null;
		try {
			int count = topicService.generateNewTopics(topicGenerationRequest);
			response = ResponseMessage.SUCCESSFULLY_GENERATED_TOPICS.appendMessage("Generated : " + count + " Topics");
		} catch (TSHException e) {
			e.printStackTrace();
			response = ResponseMessage.UNABLE_TO_GENERATE_TOPICS.appendMessage(e.getLocalizedMessage());
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping("/update")
	public ResponseEntity<?> updateTopic(@RequestBody TopicRequest topicRequest) {
		ResponseMessage response = null;

		try {
			System.out.println(topicService.updateTopic(topicRequest));
			response = ResponseMessage.SUCCESSFULLY_UPDATED_TOPICS;
		} catch (TSHException e) {
			response = ResponseMessage.UNABLE_TO_UPDATE_TOPICS.appendMessage(e.getMessage());
		}

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getAllTopics")
	public List<TopicResponse> fetchAllTopics() {

		List<TopicResponse> topicList = new ArrayList<>();
		logger.info("Request to fetch all topics received...");
		topicList = topicService.getAllActiveTopicsAsTO();
		Collections.sort(topicList, new TopicCompartor());
		logger.info("Returning {} active topics", topicList.size());
		return topicList;
	}

	@PostMapping("/createTopic")
	public ResponseEntity<String> createTopics(@RequestBody CreateTopicRequest topicRequest) {
		logger.info("Creating {} new topics.", topicRequest.getTopicRequest().size());
		ResponseEntity<String> response = null;
		topicService.createAllTopics(topicRequest.getTopicRequest());

		HttpHeaders header = new HttpHeaders();
		header.set("statusCode", "200");
		header.set("message", "Success");
		response = new ResponseEntity<>("Topics created successdfully", header, HttpStatus.ACCEPTED);
		return response;
	}
}
