package com.tsh.library.dto;

public class FeedbackResponseTO {
	
	private ResponseMessage message;
	private ScheduleTO schedule;
	
	public FeedbackResponseTO(ResponseMessage message) {
		super();
		this.message = message;
	}
	
	public FeedbackResponseTO() {
		super();
	}

	public ResponseMessage getMessage() {
		return message;
	}
	public void setMessage(ResponseMessage message) {
		this.message = message;
	}
	public ScheduleTO getSchedule() {
		return schedule;
	}
	public void setSchedule(ScheduleTO schedule) {
		this.schedule = schedule;
	}
}
