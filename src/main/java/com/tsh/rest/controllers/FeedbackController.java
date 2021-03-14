package com.tsh.rest.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.BatchProgress;
import com.tsh.entities.Feedback;
import com.tsh.entities.FeedbackCategory;
import com.tsh.entities.Student;
import com.tsh.entities.StudentBatches;
import com.tsh.entities.TopicProgress;
import com.tsh.entities.User;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.DeleteFeedbackRequest;
import com.tsh.library.dto.FeedbackCategoryTO;
import com.tsh.library.dto.FeedbackResponseTO;
import com.tsh.library.dto.FeedbackTO;
import com.tsh.library.dto.ResponseMessage;
import com.tsh.library.dto.SimpleIDRequest;
import com.tsh.library.dto.StudentFeedbackRequestTO;
import com.tsh.library.dto.StudentFeedbackResponseTO;
import com.tsh.library.dto.TopicsTO;
import com.tsh.library.dto.UpdateFeedbackRequest;
import com.tsh.library.dto.UserPrinciple;
import com.tsh.service.IBatchService;
import com.tsh.service.IFeedbackService;
import com.tsh.service.IProgressService;
import com.tsh.service.IStudentService;
import com.tsh.service.ITopicService;

@RestController
@RequestMapping("/tsh/feedback")
public class FeedbackController {

	@Autowired
	private IFeedbackService feedbackService;
	@Autowired
	private IBatchService batchService;
	@Autowired
	private IProgressService progressService;
	@Autowired
	private IStudentService studentService;
	@Autowired
	private ITopicService topicService;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/category/{grade}/{active}")
	public List<FeedbackCategoryTO> getAllFeedbackCategory(@PathVariable("grade") String grade,
			@PathVariable("active") boolean active) {
		logger.info("Processing request to get all active feedback categories for grade : " + grade);
		List<FeedbackCategoryTO> categoriesTO = new ArrayList<>();
		List<FeedbackCategory> categories = new ArrayList<>();
		ModelMapper mapper = new ModelMapper();

		if (active)
			categories = feedbackService.getAllActiveFeedbackCategories(Integer.parseInt(grade));
		else
			categories = feedbackService.getAllFeedbackCategories(Integer.parseInt(grade));

		logger.info("{} feedback categories fetched.", categories.size());
		categoriesTO = categories.stream().map(cat -> {
			FeedbackCategoryTO catTO = mapper.map(cat, FeedbackCategoryTO.class);
			List<FeedbackTO> feedbackList = new ArrayList<>();
			for (Feedback f : cat.getFeedbacks()) {
				if (active) {
					if (!f.isActive())
						continue;
				}
				FeedbackTO fTO = mapper.map(f, FeedbackTO.class);
				feedbackList.add(fTO);
			}

			catTO.setFeedbacks(feedbackList);
			return catTO;
		}).collect(Collectors.toList());

		return categoriesTO;
	}

	@PostMapping("/submit")
	public FeedbackResponseTO submitFeedback(@RequestBody StudentFeedbackRequestTO studentFeedback) {

		BatchDetails batchDetails;
		UserPrinciple principle = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User loggedinUser = principle.getUser();

		try {
			batchDetails = batchService.getBatchDetailsById(studentFeedback.getBatchDetailId());
		} catch (TSHException e) {
			return new FeedbackResponseTO(ResponseMessage.BATCH_DETAILS_NOT_FOUND);
		}

		logger.info("Updating Batch progress for batch {} - {}", batchDetails.getBatchName(), batchDetails.getCourse());
		try {
			// Manage Batch Progress for CURRENT Topic
			BatchProgress batchProgress = progressService.manageCurrentBatchProgress(batchDetails, studentFeedback);
			batchProgress = progressService.addBatchProgress(batchProgress);

			// Manage Batch Progress for NEXT Topic
			BatchProgress nextBatch = progressService.manageNextBatchProgress(batchDetails, studentFeedback);
			if (nextBatch != null) {
				logger.info("Saving next Batch progress.");
				progressService.addBatchProgress(nextBatch);
			}
		} catch (TSHException e) {
			return new FeedbackResponseTO(
					ResponseMessage.UNABLE_TO_UPDATE_BATCH_PROGRESS.appendMessage(e.getMessage()));
		}

		// Manage Topic Progress.
		List<TopicProgress> currTopicProgess;
		try {
			currTopicProgess = progressService.manageCurrentAndNextTopicProgress(batchDetails, studentFeedback);
		} catch (TSHException e) {
			return new FeedbackResponseTO(
					ResponseMessage.UNABLE_TO_UPDATE_TOPIC_PROGRESS.appendMessage(e.getMessage()));
		}

		// Manage Feedback for all Students.
		try {
			feedbackService.processStudentFeedback(batchDetails, studentFeedback, loggedinUser, currTopicProgess);
		} catch (TSHException e) {
			return new FeedbackResponseTO(
					ResponseMessage.UNABLE_TO_UPDATE_STUDENT_FEEDBACK.appendMessage(e.getMessage()));
		}

		logger.info("Refreshing batch details post feedback update. Batch Id : {}", batchDetails.getId());
		FeedbackResponseTO response = new FeedbackResponseTO();
		try {
			response.setSchedule(batchService.getBatchInfoToRender(batchDetails));
		} catch (TSHException | ParseException e) {
			return new FeedbackResponseTO(
					ResponseMessage.UNABLE_TO_UPDATE_STUDENT_FEEDBACK.appendMessage(e.getMessage()));
		}

		logger.info("Feedback update sucessfully completed. Returned Batch details.");
		response.setMessage(ResponseMessage.STUDENT_FEEDBACK_UPDATED);
		return response;
	}

	@PostMapping("/getSingleStudentFeedback")
	public StudentFeedbackResponseTO studentBatchesFeedback(@RequestBody SimpleIDRequest studentBatchId) {
		StudentFeedbackResponseTO response = new StudentFeedbackResponseTO();
		logger.info("getSingleStudentFeedback...");
		try {
			StudentBatches studentBatch = studentService.getStudentBatchesById(studentBatchId.getId());
			Student stud = studentBatch.getStudent();
			logger.info("Retreive Feedback Data for {}", stud.getStudentName());

			List<TopicsTO> allTopics = progressService.getAllTopicsProgress(stud, studentBatch.getCourse());

			response.setTopics(feedbackService.populateAllFeedbacksWithProviders(allTopics, studentBatch));
			response.setMessage(ResponseMessage.GENERAL_SUCCESS);
		} catch (Exception e) {
			response.setMessage(ResponseMessage.GENERAL_FAIL.appendMessage(e.getLocalizedMessage()));
		}
		return response;
	}

	@PostMapping("/deleteFeedback")
	public ResponseMessage deleteFeedback(@RequestBody DeleteFeedbackRequest request) {
		ResponseMessage response = null;
		UserPrinciple principle = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User loggedinUser = principle.getUser();
		logger.info("Deleting Feedback for topic id: {}", request.getTopicId());
		try {
			feedbackService.deleteFeedback(request, loggedinUser);
			response = ResponseMessage.GENERAL_SUCCESS;
		} catch (Exception e) {
			response = ResponseMessage.GENERAL_FAIL.appendMessage(e.getLocalizedMessage());
		}
		return response;
	}

	@PostMapping("/updateStudentFeedback")
	public ResponseEntity<String> updateStudentFeedback(@RequestBody UpdateFeedbackRequest request) {
		ResponseEntity<String> response = null;
		UserPrinciple principle = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User loggedinUser = principle.getUser();

		logger.info("/updateStudentFeedback called for {} topic {}", request.getStudentName(),
				request.getTopicChapter() + " -" + request.getTopicName() + " - " + request.getTopicDescription());
		StudentBatches studentBatches = studentService.getStudentBatchesById(request.getStudentBatchId());
		try {
			feedbackService.updateAndAddStudentFeedback(request, studentBatches, loggedinUser);
			HttpHeaders headers = new HttpHeaders();
			headers.set("statusCode", "504");
			headers.set("message", "Feedbacks updated...");
			response = new ResponseEntity<>("Feedback updated.", headers, HttpStatus.ACCEPTED);
			logger.info("Feedbacks successfully updated...");

		} catch (TSHException e) {
			e.printStackTrace();
			HttpHeaders headers = new HttpHeaders();
			headers.set("statusCode", "201");
			headers.set("message", "Internal Server Error. Could notupdate feedback.");
			response = new ResponseEntity<>("Internal Server Error. Could not update feedbacks.", headers,
					HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Feedbacks could not be updated due to above error.");
		}
		return response;
	}

	@PostMapping("/category/addCategory")
	public ResponseEntity<String> addFeedbackCategory(@RequestBody FeedbackCategoryTO category) {
		logger.info("Adding new Feedback Category : {}", category.getDescription());
		category = feedbackService.addFeedbackCategory(category);
		ResponseEntity<String> response = null;
		if (category == null) {
			HttpHeaders headers = new HttpHeaders();
			headers.set("statusCode", "201");
			headers.set("message", "Internal Server Error. Could not add Category.");
			response = new ResponseEntity<>("Internal Server Error. Could not add Category.", headers,
					HttpStatus.INTERNAL_SERVER_ERROR);
			logger.warn("Unable to add Category to db. Check logs for errors.");

		} else {
			HttpHeaders headers = new HttpHeaders();
			headers.set("statusCode", "504");
			headers.set("message", "Category " + category.getDescription() + " addedd successfully");
			response = new ResponseEntity<>("Category " + category.getDescription() + " addedd successfully", headers,
					HttpStatus.ACCEPTED);
			logger.info("Successfully added " + category.getDescription() + " to the db");
		}
		return response;
	}

	@PostMapping("/addFeedback")
	public ResponseEntity<String> addFeedbackItem(@RequestBody FeedbackTO feedbackRequest) {
		ResponseEntity<String> response = null;
		FeedbackTO savedObj = null;
		logger.info("Adding new feedback item : {}", feedbackRequest.getDescription());
		try {
			savedObj = feedbackService.addFeedbackItem(feedbackRequest);
		} catch (TSHException e) {
			e.printStackTrace();
			HttpHeaders headers = new HttpHeaders();
			headers.set("statusCode", "201");
			headers.set("message", "Internal Server Error. Could not add Feedback.");
			response = new ResponseEntity<>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.warn("Unable to add Feedback to db. Check logs for errors.");
		}
		if (savedObj == null) {
			HttpHeaders headers = new HttpHeaders();
			headers.set("statusCode", "201");
			headers.set("message", "Internal Server Error. Could not add Feedback.");
			response = new ResponseEntity<>("Internal Server Error. Could not add Feedback.", headers,
					HttpStatus.INTERNAL_SERVER_ERROR);
			logger.warn("Unable to add Feedback to db. Check logs for errors.");
		} else {
			HttpHeaders headers = new HttpHeaders();
			headers.set("statusCode", "504");
			headers.set("message", "Feedback " + feedbackRequest.getDescription() + " addedd successfully");
			response = new ResponseEntity<>("Feedback " + feedbackRequest.getDescription() + " addedd successfully",
					headers, HttpStatus.ACCEPTED);
			logger.info("Successfully added feedback : " + feedbackRequest.getDescription() + " to the db");
		}

		return response;
	}

	@PostMapping("/toggleFeedbackCategoryState")
	public ResponseEntity<String> toggleFeedbackCategoryState(@RequestBody SimpleIDRequest fCategory) {
		ResponseEntity<String> response = null;
		logger.info("Changing active state of Feedback category {} ", fCategory.getId());
		FeedbackCategoryTO fCategoryTO = feedbackService.findFeedbackCategoryById(fCategory.getId());
		try {
			feedbackService.toggleFeedbackCategoryState(fCategoryTO);
			HttpHeaders header = new HttpHeaders();
			header.set("statusCode", "200");
			header.set("message", "Success");
			response = new ResponseEntity<>("Feedback Category state changed", header, HttpStatus.ACCEPTED);
		} catch (TSHException e) {
			e.printStackTrace();
			HttpHeaders headers = new HttpHeaders();
			headers.set("statusCode", "201");
			headers.set("message", "Internal Server Error. Could not change state FeedbackCategory.");
			response = new ResponseEntity<>("Internal Server Error. Could not change state FeedbackCategory.", headers,
					HttpStatus.INTERNAL_SERVER_ERROR);
			logger.warn("Unable to change state of Feedback Category. Check logs for errors.");
		}
		return response;
	}

	@PostMapping("/toggleFeedbackState")
	public ResponseEntity<String> toggleFeedbackItemState(@RequestBody SimpleIDRequest feedback) {
		ResponseEntity<String> response = null;
		logger.info("Changing active state of Feedback Item {}", feedback.getId());
		Feedback f = feedbackService.getFeedbackById(feedback.getId());
		ModelMapper mapper = new ModelMapper();
		FeedbackTO feedbackTO = mapper.map(f, FeedbackTO.class);
		try {
			feedbackService.toggleFeedbackItemState(feedbackTO);
			HttpHeaders header = new HttpHeaders();
			header.set("statusCode", "200");
			header.set("message", "Success");
			response = new ResponseEntity<>("Feedback state changed", header, HttpStatus.ACCEPTED);
		} catch (TSHException e) {
			e.printStackTrace();
			HttpHeaders headers = new HttpHeaders();
			headers.set("statusCode", "201");
			headers.set("message", "Internal Server Error. Could not change state of Feedback.");
			response = new ResponseEntity<>("Internal Server Error. Could not change state of Feedback.", headers,
					HttpStatus.INTERNAL_SERVER_ERROR);
			logger.warn("Unable to change state of Feedback. Check logs for errors.");
		}

		return response;
	}
}
