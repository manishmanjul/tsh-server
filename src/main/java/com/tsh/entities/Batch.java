package com.tsh.entities;

import java.text.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.tsh.exception.TSHException;

@Entity
@Table(name="batch")
public class Batch extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name="timeslot_id")
	private TimeSlot timeSlot;
	
	@Column(name="batch_startdate")
	private Date startDate;
	
	@Column(name="batch_enddate")
	private Date endDate;
	
	private int strength;
	
	public Batch() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TimeSlot getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(TimeSlot timeSlot) {
		this.timeSlot = timeSlot;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + id;
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + strength;
		result = prime * result + ((timeSlot == null) ? 0 : timeSlot.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Batch other = (Batch) obj;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (id != other.id)
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (strength != other.strength)
			return false;
		if (timeSlot == null) {
			if (other.timeSlot != null)
				return false;
		} else if (!timeSlot.equals(other.timeSlot))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Batch [id=" + id + ", timeSlot=" + timeSlot + "]";
	}

	public static Batch getNewInstance(TimeSlot timeSlot) throws TSHException {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Batch batch = new Batch();
		batch.setTimeSlot(timeSlot);
		batch.setStrength(4);
		
		try {
			batch.setStartDate(formatter.parse(formatter.format(Calendar.getInstance().getTime())));
		} catch (ParseException e) {
			throw new TSHException("Could not set Start Date for new Batch with TimeSlot : " + timeSlot.toString());
		}
		return batch;
	}
}
