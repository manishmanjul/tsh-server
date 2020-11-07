package com.tsh.rest.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.BatchProgress;
import com.tsh.entities.FeedbackCategory;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.FeedbackCategoryTO;
import com.tsh.library.dto.FeedbackResponseTO;
import com.tsh.library.dto.FeedbackTO;
import com.tsh.library.dto.ResponseMessage;
import com.tsh.library.dto.StudentFeedbackRequestTO;
import com.tsh.service.IBatchService;
import com.tsh.service.IFeedbackService;
import com.tsh.service.IProgressService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/tsh/feedback")
public class FeedbackController {

	@Autowired
	private IFeedbackService feedbackService;
	@Autowired
	private IBatchService batchService;
	@Autowired
	private IProgressService progressService;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/category")
	public List<FeedbackCategoryTO> getAllFeedbackCategory() {
		logger.info("Processing request to get all active feedback categories");
		List<FeedbackCategoryTO> categoriesTO = new ArrayList<>();
		List<FeedbackCategory> categories = new ArrayList<>();
		ModelMapper mapper = new ModelMapper();

		categories = feedbackService.getAllActiveFeedbackCategories();
		logger.info("{} feedback categories fetched.", categories.size());
		categoriesTO = categories.stream().map(cat -> {
			FeedbackCategoryTO catTO = mapper.map(cat, FeedbackCategoryTO.class);
			List<FeedbackTO> feedbackList = cat.getFeedbacks().stream().map(f -> mapper.map(f, FeedbackTO.class))
					.collect(Collectors.toList());
			catTO.setFeedbacks(feedbackList);
			return catTO;
		}).collect(Collectors.toList());

		return categoriesTO;
	}

	@PostMapping("/submit")
	public FeedbackResponseTO submitFeedback(@RequestBody StudentFeedbackRequestTO studentFeedback) {

		BatchDetails batchDetails;
		try {
			batchDetails = batchService.getBatchDetailsById(studentFeedback.getBatchDetailId());
		} catch (TSHException e) {
			return new FeedbackResponseTO(ResponseMessage.BATCH_DETAILS_NOT_FOUND);
		}

		logger.info("Updating Batch progress for batch {} - {}", batchDetails.getBatchName(), batchDetails.getCourse());
		try {
			BatchProgress batchProgress = progressService.manageCurrentBatchProgress(batchDetails, studentFeedback); // Manage
																														// Batch
																														// Progress
																														// for
																														// CURRENT
																														// Topic
			batchProgress = progressService.addBatchProgress(batchProgress);

			BatchProgress nextBatch = progressService.manageNextBatchProgress(batchDetails, studentFeedback); // Manage
																												// Batch
																												// Progress
																												// for
																												// NEXT
																												// Topic
			if (nextBatch != null) {
				logger.info("Saving next Batch progress.");
				progressService.addBatchProgress(nextBatch);
			}
		} catch (TSHException e) {
			return new FeedbackResponseTO(
					ResponseMessage.UNABLE_TO_UPDATE_BATCH_PROGRESS.appendMessage(e.getMessage()));
		}

		// Manage Topic Progress.
		try {
			progressService.manageCurrentAndNextTopicProgress(batchDetails, studentFeedback);
		} catch (TSHException e) {
			return new FeedbackResponseTO(
					ResponseMessage.UNABLE_TO_UPDATE_TOPIC_PROGRESS.appendMessage(e.getMessage()));
		}

		// Manage Feedback for all Students.
		try {
			feedbackService.processStudentFeedback(batchDetails, studentFeedback);
		} catch (TSHException e) {
			return new FeedbackResponseTO(
					ResponseMessage.UNABLE_TO_UPDATE_STUDENT_FEEDBACK.appendMessage(e.getMessage()));
		}

		logger.info("Refreshing batch details post feedback update. Batch Id : {}", batchDetails.getId());
		FeedbackResponseTO response = new FeedbackResponseTO();
		try {
			response.setSchedule(batchService.getBatchDetails(batchDetails));
		} catch (TSHException | ParseException e) {
			return new FeedbackResponseTO(
					ResponseMessage.UNABLE_TO_UPDATE_STUDENT_FEEDBACK.appendMessage(e.getMessage()));
		}

		logger.info("Feedback update sucessfully completed. Returned Batch details.");
		response.setMessage(ResponseMessage.STUDENT_FEEDBACK_UPDATED);
		return response;
	}
}
