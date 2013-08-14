package com.esl.service;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.IMemberDAO;
import com.esl.exception.ESLSystemException;
import com.esl.exception.IllegalParameterException;
import com.esl.model.Member;
import com.esl.util.SpringUtil;
import com.esl.web.model.ContactUsForm;

@Transactional
public class MailService implements IMailService {
	// Logging
	private static Logger log = LoggerFactory.getLogger("ESL");

	private String primaryMailAddress;
	private JavaMailSenderImpl mailSender;
	private Map<String, String> mailTemplates;

	// Supporting class
	private IMemberDAO memberDAO;

	// ============== Setter / Getter ================//
	public void setMemberDAO(IMemberDAO memberDAO) { this.memberDAO = memberDAO; }

	public String getPrimaryMailAddress() {	return primaryMailAddress;	}
	public void setPrimaryMailAddress(String primaryMailAddress) {	this.primaryMailAddress = primaryMailAddress;}

	public JavaMailSenderImpl getMailSender() {	return mailSender;}
	public void setMailSender(JavaMailSenderImpl mailSender) {	this.mailSender = mailSender;}

	public Map<String, String> getMailTemplates() {	return mailTemplates;}
	public void setMailTemplates(Map<String, String> mailTemplates) {this.mailTemplates = mailTemplates;}

	// ============== Constructor ================//
	public MailService() {}

	// ============== Functions ================//
	public MimeMessageHelper getMimeMessageHelperInstance() {
		MimeMessage message = mailSender.createMimeMessage();
		return new MimeMessageHelper(message);
	}

	public void send(MimeMessage message) {
		mailSender.send(message);
	}

	public void sendToHost(MimeMessageHelper messageHelper) throws MessagingException {
		try {
			messageHelper.setTo(primaryMailAddress);
		} catch (MessagingException e) {
			throw(e);
		}
		send(messageHelper.getMimeMessage());
	}

	public String contactUs(ContactUsForm form) {
		log.info("[START] Send Contact Us Email");
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		try {
			helper.setTo(primaryMailAddress);
			helper.setSubject("[FunFunSpell] Contact Us - " + form.getSubject());
			helper.setText(form.toString());

			log.info("Send to : " + primaryMailAddress);
			log.info("Send by : " + form.getEmail());

			mailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Fail to send email. (Contact Us Mail: " + form.toString());
			return MailService.CONTACT_US_SYSTEM_ERROR;
		}
		log.info("[FINISH] Send Contact Us Email");
		return MailService.CONTACT_US_EMAIL_SENT;
	}

	/**
	 * Send Password to member
	 */
	public Member forgetPassword(String userId, Locale locale) {
		log.info("forgetPassword: START");
		if (userId == null) throw new IllegalParameterException(new String[]{"userId"}, new Object[]{userId});

		Member member = memberDAO.getMemberByUserID(userId);
		if (member == null) {
			log.info("forgetPassword: member[" + userId + "] not found.");
			return null;
		}

		ResourceBundle bundle = ResourceBundle.getBundle("messages.Authentication", locale);
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

		try {
			helper.setTo(member.getEmailAddress());
			log.info("forgetPassword: Send to address[" + member.getEmailAddress() + "]");
			//helper.setFrom("");
			helper.setSubject(bundle.getString("forgetPINEmailTitle"));
			String htmlText = mailTemplates.get("forgetPassword" + "_" + locale.toString().toLowerCase());

			// Set html content
			log.info("forgetPassword: htmlText " + htmlText );
			htmlText = htmlText.replaceAll("#userId#", member.getUserId());
			htmlText = htmlText.replaceAll("#PIN#", member.getPIN());
			htmlText = htmlText.replaceAll("#firstName#", member.getName().getFirstName());

			helper.setText(htmlText, true);
			mailSender.send(message);
			log.info("forgetPassword: email sent");
		} catch (Exception e) {
			throw new ESLSystemException(null, e.getMessage());
		}

		return member;
	}

	public static void main(String[] args)
	{
		IMailService es = (IMailService) SpringUtil.getContext().getBean("mailService");
		try
		{
			ContactUsForm f = new ContactUsForm();
			f.setSubject("anc");

			es.contactUs(f);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean sendToHost(String subject, String text) {
		log.debug("[START] sendToHost: subject[{}]", subject);
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		try {
			helper.setTo(primaryMailAddress);
			helper.setSubject("[FunFunSpell] " + subject);
			helper.setText(text);			
			mailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Fail to send mail to host: subject [{}], text[{}]", subject, text);
			return false;
		}
		log.debug("Email sent");
		return true;
	}
}
