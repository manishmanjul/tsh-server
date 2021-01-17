package com.tsh.rest.controllers;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tsh.exception.TSHException;
import com.tsh.service.IInitService;

@Component
public class StartupJob {

	@Autowired
	private IInitService initService;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@PostConstruct
	public void init() {

		logger.info("Setting up the Daily BatchProgress ...");
		try {
			if (initService.startInitialisation()) {
				initService.createAllBatchProgress();
				initService.completeInitialisation();
			}
		} catch (TSHException e) {
			e.printStackTrace();
		}
		logger.info("---- DONE ----");
	}

	@Scheduled(cron = "0 01 00 ? * * ")
	public void scheduledBatchProcessCreator() {
		logger.info("---- From Scheduler ----");
		logger.info("Setting up the Daily BatchProgress ...");
		try {
			if (initService.startInitialisation()) {
				initService.createAllBatchProgress();
				initService.completeInitialisation();
			}
		} catch (TSHException e) {
			e.printStackTrace();
		}
		logger.info("---- DONE ----");
	}
}
