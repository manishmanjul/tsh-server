package com.tsh.rest.controllers;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsh.exception.TSHException;
import com.tsh.library.dto.HTMLElementRequest;
import com.tsh.library.dto.ResponseMessage;
import com.tsh.service.IEmailService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/tsh/mail")
public class EmailController {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private IEmailService emailSertvice;

	@PostMapping("/send")
	public ResponseMessage emailFeedbackToParent(@RequestBody HTMLElementRequest element) {
		logger.info("HTML ELement : {}", element.getElement());
		ResponseMessage response = null;

		try {
			emailSertvice.sendStudentFeedback(element);
			response = ResponseMessage.GENERAL_SUCCESS;
			logger.info("Feedback sent");
		} catch (MessagingException e) {
			response = ResponseMessage.GENERAL_FAIL.appendMessage(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			response = ResponseMessage.GENERAL_FAIL.appendMessage(e.getMessage());
		} catch (TSHException e) {
			response = ResponseMessage.GENERAL_FAIL.appendMessage(e.getMessage());
		}
		return response;
	}

}
