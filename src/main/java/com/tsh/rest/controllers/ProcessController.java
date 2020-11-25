package com.tsh.rest.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsh.entities.Process;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.ProcessRequest;
import com.tsh.library.dto.ProcessStatus;
import com.tsh.service.IDataImportService;
import com.tsh.service.IProcessService;

@RestController
@RequestMapping("/tsh/process")
public class ProcessController {

	@Autowired
	private IProcessService processService;
	@Autowired
	private IDataImportService dataImportService;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@PostMapping("/importFromOutlook")
	public ProcessStatus importFromOutlook() {
		logger.info("Request to start DataImport from Outlook...");

		Process p = processService.newProcess("Import Batches from Outlook", 10);
		logger.info("Process with id : {} created", p.getId());
		ProcessStatus status = new ProcessStatus(1);
		status.setPsCode(p.getId());
		status.setPercetCompleted(0);
		status.setStepName("Initiating Data Import from Outlook...");

		new Thread(() -> {
			try {

				dataImportService.importDataFromOutlook(p.getId());
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}).start();

		logger.info("Concurrent process triggred");
		return status;
	}

	@PostMapping("/status")
	public ProcessStatus getStatus(@RequestBody ProcessRequest request) {
		logger.info("Getting status of process: {}", request.getProcessId());
		ProcessStatus status = null;
		try {
			status = processService.getStatusOf(request.getProcessId());
		} catch (TSHException e) {
			e.printStackTrace();
		}
		return status;
	}
}
