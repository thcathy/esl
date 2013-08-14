package com.esl.service.manage;

import java.text.MessageFormat;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class TomcatProcessingTimeChecker implements JMXMBeanChecker {
	private static Logger logger = LoggerFactory.getLogger(TomcatProcessingTimeChecker.class);
	private static String warningTitlePattern = "{0} processor over max time {1}";

	private String jmxServiceURL;
	private String objectNameQuery;
	private long limitProcessTime = 60000;

	private String warningTitle;
	private String warningContent;

	public void setJmxServiceURL(String jmxServiceURL) {this.jmxServiceURL = jmxServiceURL;}
	public String getJMXServiceURL() {return jmxServiceURL;}

	public void setObjectNameQuery(String objectNameQuery) {this.objectNameQuery = objectNameQuery;}
	public String getObjectNameQuery() {return objectNameQuery;}

	public long getLimitProcessTime() {return limitProcessTime;}
	public void setLimitProcessTime(long limitProcessTime) {this.limitProcessTime = limitProcessTime;}

	@Override
	public String getWarningTitle() {
		return warningTitle;
	}

	@Override
	public String getWarningContent() {
		return warningContent;
	}


	@Override
	public boolean checkBeans(MBeanServerConnection mbsc, Set<ObjectName> names) throws Exception {
		logger.debug("checkBeans start, total objectName {}", names.size());
		boolean isPass = true;
		int totalFailProcessor = 0;
		warningContent = "";
		warningTitle = "";

		for (ObjectName name : names) {
			long maxTime  = (Long) mbsc.getAttribute(name, "maxTime");
			String workerThreadName =  (String) mbsc.getAttribute(name, "workerThreadName");
			if (maxTime > limitProcessTime && StringUtils.hasLength(workerThreadName)) {
				logger.warn("A long process found!");
				isPass = false;
				totalFailProcessor++;

				concatWarningContent(mbsc, name);
			}
		}

		warningTitle = MessageFormat.format(warningTitlePattern, totalFailProcessor, limitProcessTime);

		return isPass;
	}

	private void concatWarningContent(MBeanServerConnection mbsc, ObjectName name) throws Exception {
		StringBuilder sb = new StringBuilder(warningContent);
		sb.append("objectName: " +  name + "\n");
		sb.append("currentQueryString: " +  mbsc.getAttribute(name, "currentQueryString") + "\n");
		sb.append("currentUri: " +  mbsc.getAttribute(name, "currentUri") + "\n");
		sb.append("maxTime: " +  mbsc.getAttribute(name, "maxTime") + "\n");
		sb.append("processingTime: " +  mbsc.getAttribute(name, "processingTime") + "\n");
		sb.append("remoteAddr: " +  mbsc.getAttribute(name, "remoteAddr") + "\n");
		sb.append("workerThreadName: " +  mbsc.getAttribute(name, "workerThreadName") + "\n\n");
		warningContent = sb.toString();
	}








}

