package com.tsh.library.dto;

import java.util.ArrayList;
import java.util.List;

public class DataImportResponseTO {

	private List<ImportItemTO> importItems;
	private List<ImportStatistics> stats;
	List<String> descriptions = new ArrayList<>();
	List<String> teachers = new ArrayList<>();
	List<String> weekdays = new ArrayList<>();
	List<String> subjects = new ArrayList<>();
	List<String> grades = new ArrayList<>();

	private String importDate;
	private int totalItems;
	private String pass;
	private String fail;
	private String skip;

	public List<ImportItemTO> getImportItems() {
		return importItems;
	}

	public void setImportItems(List<ImportItemTO> importItems) {
		this.importItems = importItems;
		this.totalItems = importItems.size();
	}

	public List<ImportStatistics> getStats() {
		return stats;
	}

	public void setStats(List<ImportStatistics> stats) {
		this.stats = stats;
	}

	public String getImportDate() {
		return importDate;
	}

	public void setImportDate(String importDate) {
		this.importDate = importDate;
	}

	public int getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getFail() {
		return fail;
	}

	public void setFail(String fail) {
		this.fail = fail;
	}

	public String getSkip() {
		return skip;
	}

	public void setSkip(String skip) {
		this.skip = skip;
	}

	public List<String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}

	public List<String> getTeachers() {
		return teachers;
	}

	public void setTeachers(List<String> teachers) {
		this.teachers = teachers;
	}

	public List<String> getWeekdays() {
		return weekdays;
	}

	public void setWeekdays(List<String> weekdays) {
		this.weekdays = weekdays;
	}

	public List<String> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}

	public List<String> getGrades() {
		return grades;
	}

	public void setGrades(List<String> grades) {
		this.grades = grades;
	}

}
