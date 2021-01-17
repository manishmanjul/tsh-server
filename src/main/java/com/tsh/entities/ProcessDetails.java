package com.tsh.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.tsh.exception.TSHException;
import com.tsh.utility.TshUtil;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "process_details")
public class ProcessDetails extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "process_id")
	private Process process;

	@Column(name = "step")
	private int step;

	@Column(name = "stepName")
	private String stepName;

	@Column(name = "startTime")
	private Date startTime;

	@Column(name = "endTime")
	private Date endTime;

	@Column(name = "status")
	private int status;

	@Column(name = "weight")
	private double weight;

	public ProcessDetails() {
		super();
		try {
			this.startTime = TshUtil.getCurrentDate();
		} catch (TSHException e) {
			e.printStackTrace();
		}
		this.status = 1;
		this.weight = 1;
	}

	public ProcessDetails(String stepName, int step, double weight, Process parent) {
		this();
		this.stepName = stepName;
		this.step = step;
		this.weight = weight;
		this.process = parent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Process getProcessId() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
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

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

}
