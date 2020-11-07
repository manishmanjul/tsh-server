package com.tsh.rest.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsh.exception.TSHException;
import com.tsh.library.dto.ResponseMessage;
import com.tsh.library.dto.TopicGenerationRequest;
import com.tsh.library.dto.TopicManagerResponse;
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

}
