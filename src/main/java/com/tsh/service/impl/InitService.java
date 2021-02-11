package com.tsh.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsh.entities.AppInit;
import com.tsh.entities.BatchDetails;
import com.tsh.entities.BatchProgress;
import com.tsh.entities.Term;
import com.tsh.entities.TopicStatus;
import com.tsh.entities.Topics;
import com.tsh.entities.Week;
import com.tsh.exception.TSHException;
import com.tsh.repositories.AppInitRepository;
import com.tsh.repositories.BatchDetailsRepository;
import com.tsh.service.IGeneralService;
import com.tsh.service.IInitService;
import com.tsh.service.IProgressService;
import com.tsh.service.ITopicService;
import com.tsh.utility.TshUtil;

@Service
public class InitService implements IInitService {

	@Autowired
	private AppInitRepository appInitRepo;
	@Autowired
	private BatchDetailsRepository batchDetailsRepo;
	@Autowired
	private IProgressService progressService;
	@Autowired
	private ITopicService topicService;
	@Autowired
	private IGeneralService generalService;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public boolean startInitialisation() throws TSHException {
		AppInit appInit = appInitRepo.findByInitForAndActivity(TshUtil.formatOz(TshUtil.getCurrentDate()), "Started");

		if (appInit == null) {
			appInit = new AppInit();
			appInit.setInitFor(TshUtil.getCurrentDate());
			appInit.setActivity("Started");
		}

		appInit = appInitRepo.save(appInit);
		if (appInit.getId() > 0)
			return true;
		else
			return false;
	}

	@Override
	public void completeInitialisation() throws TSHException {
		AppInit appInit = appInitRepo.findByInitForAndActivity(TshUtil.formatOz(TshUtil.getCurrentDate()), "Completed");

		if (appInit == null) {
			appInit = new AppInit();
			appInit.setInitFor(TshUtil.getCurrentDate());
			appInit.setActivity("Completed");
		}

		appInit = appInitRepo.save(appInit);
	}

	@Override
	public void createAllBatchProgress() throws TSHException {
		int weekDay = TshUtil.getTodaysWeekDay();
		List<BatchDetails> batches = batchDetailsRepo.findAllActiveBatchesForWeekday(weekDay);
		TopicStatus planned = topicService.getTopicStatusByStatus("Planned");
		Term term = generalService.getCurrentTerm().orElse(new Term(0));
		logger.info("Curent Term is {}", term.getTerm());
		int weekNum = TshUtil.getWeekNUmberPost(term.getStartDate());
		Week week = generalService.getWeekByWeekNumber(weekNum);
		List<BatchProgress> newBatchProgress = new ArrayList<>();

		for (BatchDetails batch : batches) {
			BatchProgress progress = progressService.getBatchProgressAsOfToday(batch);
			if (progress == null) {
				progress = new BatchProgress();
				progress.setBatchDetails(batch);
				progress.setCourse(batch.getCourse());
				progress.setPlannedStartDate(TshUtil.getCurrentDate());
				progress.setTeacher(batch.getTeacher());
				progress.setPlannedTime(batch.getBatch().getTimeSlot().getStartTime());
				progress.setCanceled(false);
				progress.setStatus(planned);
				progress.setTopic(getTopicFor(batch, term, week));
				newBatchProgress.add(progress);
			}
		}

		progressService.saveAllBatchProgress(newBatchProgress);
	}

	private Topics getTopicFor(BatchDetails batch, Term term, Week week) {

		Topics topic = null;
		try {
			topic = topicService.getForGradeCourseTermAndWeek(batch.getGrade(), batch.getCourse(), term, week);
		} catch (Exception e) {
			logger.error("Exception while retreiving topics for grade : {}, Course : {} , Term : {} , Week : {}",
					batch.getGrade().getGrade(), batch.getCourse().getShortDescription(), term.getTerm(),
					week.getWeekNumber());
			e.printStackTrace();
			throw e;
		}
		return topic;
	}
}
