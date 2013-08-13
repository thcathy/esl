package com.esl.web.model;

import java.util.Date;

public class SignUpForm {
	private String userId;
	private String PIN;
	private String PIN2;
	private String firstName;
	private String lastName;
	private Date birthday;
	private String address;
	private String phoneNumber;
	private String school;
	private String email;
	private String action;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPIN() {
		return PIN;
	}
	public void setPIN(String pin) {
		PIN = pin;
	}
	public String getPIN2() {
		return PIN2;
	}
	public void setPIN2(String pin2) {
		PIN2 = pin2;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}	
}
