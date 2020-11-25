package com.tsh.library.dto;

import com.tsh.utility.TshUtil;

public class ProcessStatus {
	private int psCode;
	private String status;
	private int totalStep;
	private int step;
	private String stepName;
	private int percetCompleted;

	public ProcessStatus(int status) {
		this.status = TshUtil.statusToString(status);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = TshUtil.statusToString(status);
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public int getPercetCompleted() {
		return percetCompleted;
	}

	public void setPercetCompleted(int percetCompleted) {
		this.percetCompleted = percetCompleted;
	}

	public int getTotalStep() {
		return totalStep;
	}

	public void setTotalStep(int totalStep) {
		this.totalStep = totalStep;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getPsCode() {
		return psCode;
	}

	public void setPsCode(int psCode) {
		this.psCode = psCode;
	}

}