package com.tsh.library.dto;

public class ResponseMessage {
	public final static int SUCCESS = 1;
	public final static int ERROR = 0;
	public final static ResponseMessage INVALID_ROLE = new ResponseMessage(901, "ERROR : Invalid Role: Role Not Found");
	public final static ResponseMessage INVALID_EMAIL = new ResponseMessage(902, "ERROR : Invalid Email Address");
	public final static ResponseMessage USER_ALREADY_EXIST = new ResponseMessage(903, "ERROR : User Already Exist");
	public final static ResponseMessage FAILED_TO_REGISTER = new ResponseMessage(904,
			"ERROR : Failed to register new User");
	public final static ResponseMessage SUCCESSFULLY_NEW_USER_ADDED = new ResponseMessage(SUCCESS,
			"Successfully Added a new User");
	public final static ResponseMessage SUCCESSFULLY_GENERATED_TOPICS = new ResponseMessage(SUCCESS,
			"Successfully Added a new Topics");
	public final static ResponseMessage INVALID_USERNAME_PASSWORD = new ResponseMessage(201,
			"Invalid User Name or Password");
	public final static ResponseMessage SESSION_EXPIRED = new ResponseMessage(401,
			"Session Expired. Please login again");
	public final static ResponseMessage BATCH_DETAILS_NOT_FOUND = new ResponseMessage(301, "Batch Details not found");
	public final static ResponseMessage UNABLE_TO_UPDATE_BATCH_PROGRESS = new ResponseMessage(302,
			"Unable to generate Batch Progress");
	public final static ResponseMessage UNABLE_TO_UPDATE_TOPIC_PROGRESS = new ResponseMessage(303,
			"Unable to generate Topic Progress");
	public final static ResponseMessage UNABLE_TO_UPDATE_STUDENT_FEEDBACK = new ResponseMessage(304,
			"Unable to update Student Feedback");
	public final static ResponseMessage UNABLE_TO_FETCH_BATCH_DETAILS = new ResponseMessage(305,
			"Unable to fetch Batch Details");
	public final static ResponseMessage UNABLE_TO_GENERATE_TOPICS = new ResponseMessage(306,
			"Unable to generate Topics");
	public final static ResponseMessage STUDENT_FEEDBACK_UPDATED = new ResponseMessage(100,
			"Feedback Saved Successfully");
	public final static String SUCCESS_STRING = "Success";
	public final static String ERROR_STRING = "Error";

	private int returnCode;
	private String message;

	private ResponseMessage(int code, String msg) {
		this.returnCode = code;
		this.message = msg;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ResponseMessage appendMessage(String msg) {
		this.message = this.message + ": " + msg;
		return this;
	}

	public static ResponseMessage getSuccessResponse() {
		return new ResponseMessage(SUCCESS, SUCCESS_STRING);
	}

	public static ResponseMessage getErrorResponse() {
		return new ResponseMessage(ERROR, ERROR_STRING);
	}

	public static ResponseMessage getErrorResponse(String message) {
		return new ResponseMessage(ERROR, ERROR_STRING + ": " + message);
	}

}
