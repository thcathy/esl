package com.esl.web.util;

import com.esl.web.model.dictation.DictationStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

public class DictationUtil {
	protected final Logger logger = LoggerFactory.getLogger(DictationUtil.class);
	private static final String bundleName = "messages.member.Dictation";

	public static String getStatisticsTitle(DictationStatistics stat, Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
		return bundle.getString("statisticsType." + stat.getType().toString());
	}

}
