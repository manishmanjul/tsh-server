package com.tsh.rest.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsh.library.dto.GradeTO;
import com.tsh.service.IGeneralService;

@RestController
@RequestMapping("/tshServices")
public class GeneralController {

	@Autowired
	private IGeneralService generalService;
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/getAllGrades")
	public List<GradeTO> getAllGrades() {
		List<GradeTO> responseList = null;
		logger.info("Fetching all active grades");
		responseList = generalService.findAllGradesAsTO();
		return responseList;
	}
}
