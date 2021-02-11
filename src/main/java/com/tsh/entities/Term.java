package com.tsh.entities;

import java.text.ParseException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.tsh.utility.TshUtil;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "term")
public class Term extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private int term;
	private String description;

	@Column(name = "startdate")
	private Date startDate;

	@Column(name = "enddate")
	private Date endDate;

	public Term() {
	}

	public Term(int term) {
		this();
		this.term = term;
		this.description = "Term " + term;
		try {
			this.startDate = TshUtil.getFirstDayOfCurrentWeek();
			this.endDate = TshUtil.getLastDayOfCurrentWeek();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}

	public int getTerm() {
		return term;
	}

	public String getDescription() {
		return description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + id;
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + term;
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
		Term other = (Term) obj;
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
		if (term != other.term)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Term [id=" + id + ", term=" + term + ", startDate=" + startDate + ", endDate=" + endDate + "]";
	}
}
