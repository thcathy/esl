package com.esl.service.manage;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esl.service.IMailService;

public class JMXMBeanCheckProcessor {
	private static Logger logger = LoggerFactory.getLogger("ESL");


	List<JMXMBeanChecker> checkers = new ArrayList<JMXMBeanChecker>();
	IMailService mailService;

	public void setMailService(IMailService mailService) {	this.mailService = mailService;}
	public void setCheckers(List<JMXMBeanChecker> checkers) {
		this.checkers = checkers;
	}

	public void addChecker(JMXMBeanChecker checker) {
		checkers.add(checker);
	}

	public void checkAll() {
		logger.debug("checkAll start");
		for (JMXMBeanChecker checker : checkers) {
			logger.debug("check {} now", checker.getClass());

			// Connect and checker
			try {
				JMXServiceURL url = new JMXServiceURL(checker.getJMXServiceURL());
				JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
				MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();


				if (!checker.checkBeans(mbsc, mbsc.queryNames(new ObjectName(checker.getObjectNameQuery()), null))) {
					logger.debug("Warning found, sent email notification");
					mailService.sendToHost(checker.getWarningTitle(), checker.getWarningContent());
				}
			} catch (Exception e) {
				logger.warn("Exception found during JMXMBeanCheckProcessor.checkAll ", e);
			}
		}

		logger.debug("checkAll end");

		//		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:8999/jmxrmi");
		//		JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
		//		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		//
		//		ObjectName mbeanName = new ObjectName("Catalina:type=RequestProcessor,worker=\"http-bio-8080\",name=HttpRequest1");
		//		System.out.println(mbsc.getAttribute(mbeanName, "modelerType"));
		//		Set<ObjectName> names = mbsc.queryNames(new ObjectName("Catalina:type=RequestProcessor,worker=\"http-bio-8080\",name=HttpRequest*"), null);
		//		for (ObjectName n : names) {
		//			System.out.println(mbsc.getAttribute(n, "requestProcessingTime"));
		//		}
	}
}
