package com.tsh.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.tsh.exception.TSHException;
import com.tsh.utility.TshUtil;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "process")
public class Process extends BaseEntity {

	public Process() {
		super();
		try {
			this.startTime = TshUtil.getCurrentDate();
			this.status = 1;
		} catch (TSHException e) {
			e.printStackTrace();
		}
	};

	public Process(String processName, int totalSteps) {
		this();
		this.name = processName;
		this.totalSteps = totalSteps;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "name")
	private String name;

	@Column(name = "startTime")
	private Date startTime;

	@Column(name = "endTime")
	private Date endTime;

	@Column(name = "status")
	private int status;

	@Column(name = "totalSteps")
	private int totalSteps;

	@OneToMany(mappedBy = "process", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	List<ProcessDetails> steps;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int getTotalSteps() {
		return totalSteps;
	}

	public void setTotalSteps(int totalSteps) {
		this.totalSteps = totalSteps;
	}

	public List<ProcessDetails> getSteps() {
		return steps;
	}

	public void setSteps(List<ProcessDetails> steps) {
		this.steps = steps;
	}

	public void addStep(ProcessDetails step) {
		if (steps == null)
			steps = new ArrayList<>();
		steps.add(step);
	}
}
