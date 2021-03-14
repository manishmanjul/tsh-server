package com.tsh.rest.controllers;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tsh.entities.Topics;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.DataImportResponseTO;
import com.tsh.library.dto.ImportItemTO;
import com.tsh.library.dto.ImportStatistics;
import com.tsh.library.dto.ReImportRequest;
import com.tsh.library.dto.ResponseMessage;
import com.tsh.library.dto.ResponseWithMap;
import com.tsh.service.IDataImportService;

@RestController
@RequestMapping("/tsh/import")
public class DataImportController {

	@Autowired
	private IDataImportService importService;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/outlook")
	public String importData() {
		logger.info("Request to import data from external source recieved by Data Import Controller");
		String result = "Success from import outlook";

//		try {
//			result = importService.importDataFromOutlook();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return e.getMessage();
//		}

		return result;
	}

	@PostMapping("/file")
	public ResponseWithMap<Topics> importFile(@RequestParam("file") MultipartFile file,
			@RequestParam("command") String command) {
		Map<String, List<Topics>> returnData = null;
		logger.info("Request to import a file with command : {}, recieved.", command);
		ResponseMessage response = null;
		try {
			returnData = importService.processFile(file, command);
			response = ResponseMessage.getSuccessResponse();
		} catch (TSHException e) {
			logger.info(e.getMessage());
			response = ResponseMessage.getErrorResponse();
		}
		ResponseWithMap<Topics> res = new ResponseWithMap<>();
		res.setResponseMessage(response);
		res.setData(returnData);
		return res;
	}

	@PostMapping("/reImport")
	public ResponseEntity<String> reImport(@RequestBody ReImportRequest request) {
		ResponseEntity<String> response = null;
		logger.info("ReImport service called for Item : {}, Name: {}", request.getId(), request.getName());
		try {
			importService.reImport(request);
			logger.info("Import completed. Check status");
			HttpHeaders header = new HttpHeaders();
			header.set("statusCode", "200");
			header.set("message", "Success");
			response = new ResponseEntity<>("Success", header, HttpStatus.ACCEPTED);

		} catch (TSHException | ParseException e) {

			e.printStackTrace();
			HttpHeaders header = new HttpHeaders();
			header.set("statusCode", "201");
			header.set("message", "Unable to imported Items." + e.getMessage());
			response = new ResponseEntity<>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@GetMapping("/getImportedItemList/{cycleNumber}")
	public ResponseEntity<DataImportResponseTO> getImportedItemList(@PathVariable("cycleNumber") String cycleNumber) {
		int cycle = 0;
		if (cycleNumber.equalsIgnoreCase("0")) {
			cycle = importService.getLastImportCycle();
		} else {
			cycle = Integer.parseInt(cycleNumber);
		}
		logger.info("getImportedItemList service called for cycle : {}", cycle);
		ResponseEntity<DataImportResponseTO> response = null;
		DataImportResponseTO responseTO = new DataImportResponseTO();
		try {
			responseTO.setImportItems(importService.getAllImportedItems(cycle));
			logger.info("Getting import statistics.");
			responseTO.setStats(importService.getImportStatistics(cycle));
			responseTO.setImportDate(importService.getImportDate(cycle));

			int pass = 0, fail = 0, skip = 0;
			for (ImportStatistics s : responseTO.getStats()) {
				if (s.getStatus() == 3)
					pass += s.getCount();
				if (s.getStatus() == 4)
					fail += s.getCount();
				if (s.getStatus() == 5)
					skip += s.getCount();
			}
			responseTO.setPass(pass + "");
			responseTO.setFail(fail + "");
			responseTO.setSkip(skip + "");

			populateMenuItems(responseTO);

			HttpHeaders header = new HttpHeaders();
			header.set("statusCode", "200");
			header.set("message", "Success");
			response = new ResponseEntity<>(responseTO, header, HttpStatus.ACCEPTED);
			logger.info("getImportedItemList service finished with returning {} imported items.",
					responseTO.getImportItems().size());
		} catch (Exception e) {
			e.printStackTrace();
			HttpHeaders header = new HttpHeaders();
			header.set("statusCode", "305");
			header.set("message", "Unable to find imported Items." + e.getMessage());
			response = new ResponseEntity<>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	private void populateMenuItems(DataImportResponseTO response) {

		for (ImportItemTO item : response.getImportItems()) {
			if (!response.getDescriptions().contains(item.getMessage()))
				response.getDescriptions().add(item.getMessage());

			if (!response.getTeachers().contains(item.getTeacher()))
				response.getTeachers().add(item.getTeacher());

			if (!response.getSubjects().contains(item.getSubject()))
				response.getSubjects().add(item.getSubject());

			if (!response.getGrades().contains(item.getGrade()))
				response.getGrades().add(item.getGrade());
		}
		Collections.sort(response.getTeachers());
		Collections.sort(response.getGrades());
		Collections.sort(response.getSubjects());

		response.getWeekdays().add("Sunday");
		response.getWeekdays().add("Monday");
		response.getWeekdays().add("Tuesday");
		response.getWeekdays().add("Wednesday");
		response.getWeekdays().add("Thursday");
		response.getWeekdays().add("Friday");
	}
}
