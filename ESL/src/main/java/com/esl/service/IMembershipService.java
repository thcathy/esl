package com.esl.service;

import com.esl.model.*;

public interface IMembershipService {
	// Use for sign up
	public static final String USER_ID_DUPLICATED = "USER_ID_DUPLICATED";	
	public static final String ACCOUNT_CREATED = "ACCOUNT_CREATED";
	
	// Use for login
	public static final String LOGIN_SUCCEED = "LOGIN_SUCCEED";	
	public static final String USER_ID_PASSWORD_NOT_MATCH = "USER_ID_PASSWORD_NOT_MATCH";
	
	// Use for update profile
	public static final String USER_ID_NOT_FOUND = "USER_ID_NOT_FOUND";
	public static final String PROFILE_UPDATED = "PROFILE_UPDATED";
	
	// Use for all function
	public static final String INVALID_INPUT = "INVALID_INPUT";
	public static final String SYSTEM_ERROR = "SYSTEM_ERROR";
	
	// Return para
	public Member getMember();
	
	// Main function
	public String signUp(Member member);
	public String login(Member member);
	public String updateProfile(Member member);
	
	
}
