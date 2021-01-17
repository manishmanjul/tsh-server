package com.tsh.rest.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsh.entities.StudentBatches;
import com.tsh.entities.Teacher;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.ResponseMessage;
import com.tsh.library.dto.SimpleIDRequest;
import com.tsh.library.dto.StudentTO;
import com.tsh.library.dto.UserPrinciple;
import com.tsh.service.IStudentService;
import com.tsh.service.ITeacherService;

@RestController
@RequestMapping("/tsh/student")
public class StudentController {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ITeacherService teacherService;
	@Autowired
	private IStudentService studentService;

	@GetMapping("/getStudents")
	public List<StudentTO> returnAllStudentsForUser() {
		UserPrinciple principle = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Teacher teacher = teacherService.findByUser(principle.getUser());
		logger.info("Return all active students for user : {} : {}", principle.getUser(), teacher.getTeacherName());

		return studentService.getStudentsForTeacher(teacher);
	}

	@PostMapping("/markAbsent")
	public ResponseMessage setAbsent(@RequestBody SimpleIDRequest studentBatchId) {

		ResponseMessage response;

		StudentBatches studentBatch = studentService.getStudentBatchesById(studentBatchId.getId());
		logger.info("Setting {} absent today.", studentBatch.getStudent().getStudentName());
		try {
			studentService.markAbsent(studentBatch);
			response = ResponseMessage.GENERAL_SUCCESS;
		} catch (TSHException e) {
			response = ResponseMessage.GENERAL_FAIL.appendMessage(e.getLocalizedMessage());
		}

		return response;
	}
}
