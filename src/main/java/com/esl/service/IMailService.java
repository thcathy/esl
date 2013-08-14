package com.esl.service;

import java.util.Locale;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;

import com.esl.model.Member;
import com.esl.web.model.ContactUsForm;

public interface IMailService {
	// Use for all function
	public static final String CONTACT_US_SYSTEM_ERROR = "CONTACT_US_SYSTEM_ERROR";
	public static final String CONTACT_US_EMAIL_SENT = "CONTACT_US_EMAIL_SENT";

	// Main function
	public String contactUs(ContactUsForm form);
	public Member forgetPassword(String userId, Locale locale);
	public MimeMessageHelper getMimeMessageHelperInstance();
	public void send(MimeMessage message);
	public boolean sendToHost(String subject, String text);
}
