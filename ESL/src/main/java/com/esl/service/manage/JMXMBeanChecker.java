package com.esl.service.manage;

import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

public interface JMXMBeanChecker {
	public String getJMXServiceURL();
	public String getObjectNameQuery();

	public boolean checkBeans(MBeanServerConnection mbsc, Set<ObjectName> names) throws Exception;
	public String getWarningTitle();
	public String getWarningContent();
}
