package com.tsh.entities;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "import_item")
public class ImportItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "name")
	private String name;
	@Column(name = "subject")
	private String subject;
	@Column(name = "grade")
	private String grade;
	@Column(name = "teacher")
	private String teacher;
	@Column(name = "location")
	private String location;
	@Column(name = "batch_date")
	private Date batchDate;
	@Column(name = "batch_start_time")
	private Time batchStartTime;
	@Column(name = "batch_end_time")
	private Time batchEndTime;
	@Column(name = "email")
	private String email;
	@Column(name = "father_name")
	private String fatherName;
	@Column(name = "mother_name")
	private String motherName;
	@Column(name = "phone")
	private String phone;
	@Column(name = "status")
	private int status;
	@Column(name = "import_date")
	private Date importDate;

	private transient int gradeNumber;
	public static int NEW_ITEM = 1;
	public static int IN_PROGRESS = 2;
	public static int SUCCESS = 3;
	public static int FAILED = 4;

	public ImportItem() {
		super();
		this.status = ImportItem.NEW_ITEM;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
		if (this.gradeNumber == 0) {
			StringTokenizer token = new StringTokenizer(grade);
			token.nextElement();
			this.gradeNumber = Integer.parseInt((String) token.nextElement());
		}
	}

	public int getGradeNumber() {
		if (gradeNumber == 0) {
			setGrade(this.grade);
		}
		return gradeNumber;
	}

	public void setGradeNumber(int gradeNumber) {
		this.gradeNumber = gradeNumber;
		if (this.grade == null || this.grade.length() <= 0)
			this.grade = "Year " + gradeNumber;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(Date batchDate) {
		this.batchDate = batchDate;
	}

	public Time getBatchStartTime() {
		return batchStartTime;
	}

	public void setBatchStartTime(Time batchStartTime) {
		this.batchStartTime = batchStartTime;
	}

	public Time getBatchEndTime() {
		return batchEndTime;
	}

	public void setBatchEndTime(Time batchEndTime) {
		this.batchEndTime = batchEndTime;
	}

	public int getBatchWeekDay() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(batchDate);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getStatus() {
		return status;
	}

	public String getStatusAsString() {
		String status = null;
		if (this.status == NEW_ITEM)
			status = "New";
		if (this.status == IN_PROGRESS)
			return "In Progress";
		if (this.status == SUCCESS)
			return "Success";
		if (this.status == FAILED)
			return "Failed";
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getImportDate() {
		return importDate;
	}

	public void setImportDate(Date importDate) {
		this.importDate = importDate;
	}

	public String toString() {
		String returnVal;
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat timeFormatter = new SimpleDateFormat("HH:mm");
		returnVal = "Name :" + name + ", Subject :" + subject + ", Grade :" + grade + ", Teacher :" + teacher
				+ ", Location :" + location + ", BatchDate :" + formatter.format(batchDate) + ", BatchTime :"
				+ timeFormatter.format(batchStartTime) + ", BatchEnds :" + timeFormatter.format(batchEndTime);

		return returnVal;
	}

	public void startImport() {
		this.status = ImportItem.IN_PROGRESS;
	}

	public boolean isInProgress() {
		return this.status == ImportItem.IN_PROGRESS;
	}

	public void imported() {
		this.status = ImportItem.SUCCESS;
		this.importDate = Calendar.getInstance().getTime();
	}

	public boolean isImported() {
		return this.status == ImportItem.SUCCESS;
	}

	public void failed() {
		this.status = ImportItem.FAILED;
	}

	public boolean hasFailedImport() {
		return this.status == ImportItem.FAILED;
	}

	public void readyForImport() {
		this.status = ImportItem.NEW_ITEM;
	}

	public boolean isReadyForImport() {
		return this.status == ImportItem.NEW_ITEM;
	}
}
