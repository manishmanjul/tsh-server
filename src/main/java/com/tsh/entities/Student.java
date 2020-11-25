package com.tsh.entities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.tsh.exception.TSHException;

@Entity
@Table(name = "student")
public class Student extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "student_name")
	private String studentName;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "grade_id")
	private Grades grade;

	@Column(name = "joining_date")
	private Date joiningDate;

	private String email;

	@Column(name = "father_name")
	private String fatherName;

	@Column(name = "mother_name")
	private String motherName;

	private String phone;

	private boolean active;

	public Student() throws ParseException {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		this.joiningDate = formatter.parse(formatter.format(Calendar.getInstance().getTime()));

	}

	public Student(String name, Grades grade) throws ParseException {
		this();
		this.studentName = name;
		this.grade = grade;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public Grades getGrade() {
		return grade;
	}

	public void setGrade(Grades grade) {
		this.grade = grade;
	}

	public Date getJoiningDate() {
		return joiningDate;
	}

	public void setJoiningDate(Date joiningDate) {
		this.joiningDate = joiningDate;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((fatherName == null) ? 0 : fatherName.hashCode());
		result = prime * result + ((grade == null) ? 0 : grade.hashCode());
		result = prime * result + id;
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((studentName == null) ? 0 : studentName.hashCode());
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
		Student other = (Student) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (fatherName == null) {
			if (other.fatherName != null)
				return false;
		} else if (!fatherName.equals(other.fatherName))
			return false;
		if (grade == null) {
			if (other.grade != null)
				return false;
		} else if (!grade.equals(other.grade))
			return false;
		if (id != other.id)
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (studentName == null) {
			if (other.studentName != null)
				return false;
		} else if (!studentName.equals(other.studentName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Student [studentName=" + studentName + ", grade=" + grade + ", joiningDate=" + joiningDate + ", email="
				+ email + ", fatherName=" + fatherName + ", motherName=" + motherName + ", phone=" + phone + "]";
	}

	public static Student getNewInstance(ImportItem item, Grades grade) throws TSHException {
		Student student;
		try {
			student = new Student(item.getName(), grade);
		} catch (ParseException e) {
			throw new TSHException("Unable to set Joining Date of Student : " + item.getName()
					+ "Possible cause : Error in formatting the date");
		}
		student.setActive(true);
		return student;
	}
}
