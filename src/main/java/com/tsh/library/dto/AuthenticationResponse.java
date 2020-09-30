package com.tsh.library.dto;

public class AuthenticationResponse {
	private String jwt;
	private WelcomeKit welcomeKit;
	
	
	public AuthenticationResponse(String jwt, WelcomeKit welcomeKit) {
		super();
		this.jwt = jwt;
		this.welcomeKit = welcomeKit;
	}
	
	public AuthenticationResponse() {
	}

	public WelcomeKit getWelcomeKit() {
		return welcomeKit;
	}

	public String getJwt() {
		return jwt;
	}	
}
