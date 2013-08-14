package com.esl.service.manage;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esl.service.IMailService;
import com.esl.util.SourceChecker;

public class WebSourceChecker {
	private static Logger logger = LoggerFactory.getLogger("ESL");

	List<SourceChecker> checkers = new ArrayList<SourceChecker>();
	IMailService mailService;

	public void setCheckers(List<SourceChecker> checkers) {
		this.checkers = checkers;
	}

	public void addTestContent(SourceChecker content) {
		checkers.add(content);
	}

	public IMailService getMailService() {	return mailService;}
	public void setMailService(IMailService mailService) {	this.mailService = mailService;}

	public void checkAll() {
		final String logPrefix = "checkAll:";
		logger.debug("{} START", logPrefix);

		List<SourceChecker> fails = runCheck();
		sendFailsNotification(fails);
	}

	private List<SourceChecker> runCheck() {
		List<SourceChecker> fails = new ArrayList<SourceChecker>();
		for (SourceChecker checker : checkers) {
			logger.debug("run check for class {}", checker.getClass().getName());
			checker.parse();
			if (!checker.isContentCorrect()) {
				fails.add(checker);
			}
		}
		return fails;
	}

	private void sendFailsNotification(List<SourceChecker> fails) {
		if (fails.size() <= 0) return;

		logger.debug("Web source test fail, send notification email");
		String subject = "Web source check fail: (total:" + fails.size() + ")";
		StringBuffer sb = new StringBuffer();

		for (SourceChecker checker : fails) {
			sb.append("Checker: " + checker.getClass().getName() + "\n");
			sb.append("URL: " + checker.getSourceLink() + "\n");
			sb.append("Actual Content: " + checker.getParsedContent() + "\n");
			sb.append("Expected Content: " + checker.getParsedContentCheck() + "\n");
		}

		mailService.sendToHost(subject, sb.toString());
	}
}
