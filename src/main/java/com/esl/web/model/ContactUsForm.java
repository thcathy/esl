package com.esl.web.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("request")
public class ContactUsForm {
	private String title;
	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private String country;
	private String phone;
	private String email;
	private String subject;
	private String message;

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		String str = "";
		str += "Name: " + title + ". " + firstName + " " + lastName + "\n";
		str += "Address: " + address + "\n";
		str += "City: " + city + "\n";
		str += "Country: " + country + "\n";
		str += "Phone: " + phone + "\n";
		str += "Email: " + email + "\n";
		str += "City: " + city + "\n";
		str += "Subject: " + subject + "\n";
		str += "Message: " + message + "\n";
		return str;
	}


}