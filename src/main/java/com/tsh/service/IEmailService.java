package com.tsh.service;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

import com.tsh.exception.TSHException;
import com.tsh.library.dto.HTMLElementRequest;

@Service
public interface IEmailService {

	public boolean sendStudentFeedback(HTMLElementRequest request)
			throws UnsupportedEncodingException, MessagingException, TSHException;
}
