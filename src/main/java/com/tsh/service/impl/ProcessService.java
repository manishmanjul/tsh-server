package com.tsh.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsh.entities.Process;
import com.tsh.entities.ProcessDetails;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.ProcessStatus;
import com.tsh.repositories.ProcessDetailsRepository;
import com.tsh.repositories.ProcessRepository;
import com.tsh.service.IProcessService;
import com.tsh.utility.TshUtil;

@Service
public class ProcessService implements IProcessService {

	@Autowired
	private ProcessRepository processRepo;
	@Autowired
	private ProcessDetailsRepository processDetailsRepo;

	@Override
	public Process getProcessById(int id) throws TSHException {
		return processRepo.findById(id).orElseThrow(() -> new TSHException("Process with id: " + id + " not found."));
	}

	@Override
	public Process newProcess(String processName, int totalSteps) {
		return processRepo.save(new Process(processName, totalSteps));
	}

	@Override
	public ProcessDetails updateProcessStep(ProcessDetails processDetails) {
		return processDetailsRepo.save(processDetails);
	}

	@Override
	public ProcessStatus getStatusOf(int processId) throws TSHException {
		Process p = getProcessById(processId);
		double valueOfEachStep = 100 / p.getTotalSteps();
		int percentage = 0;
		ProcessStatus status = new ProcessStatus(p.getStatus());
		status.setPsCode(processId);
		status.setTotalStep(p.getTotalSteps());
		for (ProcessDetails s : p.getSteps()) {
			if (s.getStatus() == 4) {
				percentage = percentage + new Double(valueOfEachStep * s.getWeight()).intValue();
				status.setStepName("Import from Outlook Completed successfully");
			} else {
				status.setStep(s.getStep());
				status.setStepName(s.getStepName());
			}
			status.setPercetCompleted(percentage);
		}
		return status;
	}

	@Override
	public void completeProcess(Process process) throws TSHException {
		List<ProcessDetails> steps = process.getSteps().stream().filter(s -> s.getStatus() != 1)
				.collect(Collectors.toList());
		if (steps.size() > 0) {
			process.setStatus(3);
			for (ProcessDetails step : steps) {
				if (step.getStatus() == 5) {
					process.setStatus(5);
				} else if (step.getStatus() == 1) {
					step.setStatus(3);
				}
			}
		} else {
			process.setStatus(4);
		}
		process.setEndTime(TshUtil.getCurrentDate());
		updateProcess(process);
	}

	@Override
	public void completeProcess(int processId) throws TSHException {
		Process p = processRepo.findById(processId).orElse(null);
		if (p != null) {
			completeProcess(p);
		}
	}

	@Override
	public Process updateProcess(Process process) {
		return processRepo.save(process);
	}

	@Override
	public ProcessDetails newProcessStep(String stepName, int step, double weight, Process parent) {
		ProcessDetails newStep = processDetailsRepo.save(new ProcessDetails(stepName, step, weight, parent));
		return newStep;
	}

	@Override
	public void completeProcessStep(ProcessDetails step) throws TSHException {
		step.setStatus(4);
		step.setEndTime(TshUtil.getCurrentDate());
		updateProcessStep(step);
	}

	@Override
	public void completeProcessStep(int stepId) throws TSHException {
		ProcessDetails step = processDetailsRepo.findById(stepId).orElse(null);

		if (step != null) {
			completeProcessStep(step);
		}

	}

	@Override
	public ProcessDetails closeOldAndCreateNewStep(ProcessDetails oldStep, String stepName, int step, double weight,
			Process parent) throws TSHException {

		List<ProcessDetails> stepList = new ArrayList<>();
		oldStep.setStatus(4);
		oldStep.setEndTime(TshUtil.getCurrentDate());
		stepList.add(oldStep);

		ProcessDetails newStep = new ProcessDetails(stepName, step, weight, parent);
		stepList.add(newStep);

		processDetailsRepo.saveAll(stepList);
		return newStep;
	}

	@Override
	public void failStep(ProcessDetails step, Process parent) throws TSHException {
		List<ProcessDetails> stepList = new ArrayList<>();
		step.setEndTime(TshUtil.getCurrentDate());
		step.setStatus(5);
		stepList.add(step);

		ProcessDetails newStep = new ProcessDetails("Failed to import data. Something went wrong", step.getStep() + 1,
				1, parent);
		stepList.add(newStep);

		processDetailsRepo.saveAll(stepList);
	}
}
