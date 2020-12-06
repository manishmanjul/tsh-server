package com.tsh.service.impl;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsh.entities.StudentBatches;
import com.tsh.exception.TSHException;
import com.tsh.library.Mailer;
import com.tsh.library.dto.HTMLElementRequest;
import com.tsh.service.IEmailService;
import com.tsh.service.IStudentService;
import com.tsh.utility.TshUtil;

@Service
public class EmailService implements IEmailService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IStudentService studentService;
	@Autowired
	private Mailer mailer;

	@Override
	public boolean sendStudentFeedback(HTMLElementRequest request)
			throws UnsupportedEncodingException, MessagingException, TSHException {
		StudentBatches studentbatch = studentService.getStudentBatchesById(request.getStudentBatchId());
		String email = studentbatch.getStudent().getEmail();

		if (email == null) {
			email = "manjul.manish@gmail.com";
		}
		String body = request.getElement().replaceAll("hidden=\"\"", "");
		mailer.sendAsHTML(email, "Weekly Feedback of : " + studentbatch.getStudent().getStudentName() + " - ["
				+ TshUtil.getCurrentDate() + "]", body);

		return true;
	}

}
