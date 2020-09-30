package com.tsh.rest.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tsh.entities.Topics;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.ResponseMessage;
import com.tsh.library.dto.ResponseWithMap;
import com.tsh.service.IDataImportService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/tsh/import")
public class DataImportController {
	
	@Autowired
	private IDataImportService importService;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@GetMapping("/outlook")
	public String importData(){
		logger.info("Request to import data from external source recieved by Data Import Controller");
		String result = "Success from import outlook";
		/*
		 * try { result = importService.importData(); } catch (Exception e) {
		 * e.printStackTrace(); return e.getMessage(); }
		 */
		return result;
	}
	
	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping("/file")
	public ResponseWithMap<Topics> importFile(@RequestParam("file") MultipartFile file, @RequestParam("command") String command) {
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
}
