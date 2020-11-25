package com.tsh.service;

import org.springframework.stereotype.Service;

import com.tsh.entities.Process;
import com.tsh.entities.ProcessDetails;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.ProcessStatus;

@Service
public interface IProcessService extends TshService {

	public Process getProcessById(int id) throws TSHException;

	public Process newProcess(String processName, int totalSteps);

	public ProcessStatus getStatusOf(int processId) throws TSHException;

	public void completeProcess(int processId) throws TSHException;

	public void completeProcess(Process process) throws TSHException;

	public Process updateProcess(Process process);

	public void failStep(ProcessDetails step, Process parent) throws TSHException;

	public ProcessDetails newProcessStep(String stepName, int step, double weight, Process parent);

	public ProcessDetails closeOldAndCreateNewStep(ProcessDetails oldStep, String stepName, int step, double weight,
			Process parent) throws TSHException;

	public ProcessDetails updateProcessStep(ProcessDetails processDetails);

	public void completeProcessStep(ProcessDetails step) throws TSHException;

	public void completeProcessStep(int stepId) throws TSHException;
}
