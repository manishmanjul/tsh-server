package com.tsh.rest.controllers;

import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsh.library.dto.DashboardItems;
import com.tsh.service.IDashboardService;

@RestController
@RequestMapping("/tsh/dashboard")
public class DashboardController {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private IDashboardService dashboardService;

	@GetMapping("/getBatchDetails/{batchDate}")
	public ResponseEntity<List<DashboardItems>> getBatchDetailsFor(@PathVariable("batchDate") String batchDate) {
		ResponseEntity<List<DashboardItems>> response = null;
		logger.info("Getting all batches for {}", batchDate);
		batchDate = batchDate.replaceAll("-", "/");
		try {
			List<DashboardItems> dashboardItemList = dashboardService.getAllBatchesFor(batchDate);
			logger.info("{} batches fetched.", dashboardItemList.size());
			if (dashboardItemList.size() > 0) {
				HttpHeaders headers = new HttpHeaders();
				headers.set("statusCode", "202");
				headers.set("message", dashboardItemList.size() + " batches fetched.");
				response = new ResponseEntity<>(dashboardItemList, headers, HttpStatus.ACCEPTED);
			} else {
				HttpHeaders headers = new HttpHeaders();
				headers.set("statusCode", "204");
				headers.set("message", dashboardItemList.size() + " batches fetched.");
				response = new ResponseEntity<>(dashboardItemList, headers, HttpStatus.NO_CONTENT);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			HttpHeaders headers = new HttpHeaders();
			headers.set("statusCode", "201");
			headers.set("message", "Internal Server Error. Could not fetch batch records.");
			response = new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

}
