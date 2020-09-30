package com.tsh.library.dto;

public class StudentResponseTO {
	
	public StudentResponseTO() {
	}
	
	int id;				//This is not the studentID from the student table. It is Student_Batch_Id
	String name;
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
}
