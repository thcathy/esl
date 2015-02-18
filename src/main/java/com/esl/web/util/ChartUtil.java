package com.esl.web.util;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esl.model.PracticeResult;
import com.esl.web.model.practice.PracticeResultSummary;


public class ChartUtil {
	private static Logger logger = LoggerFactory.getLogger(ChartUtil.class);
	
	// Using for Google Chart API
	private static final String chartLabelSeparator = "|";
	private static final String chartDataSeparator = ",";
	private static final String chartLabelPrefix = "chl=";
	private static final String chartDataPrefix = "chd=t:";
	
	/**
	 * 
	 */
	public static void setPracticeSummaryCharts(PracticeResultSummary summary, Locale locale) {
		String chl = chartLabelPrefix;
		String scorechd = chartDataPrefix;
		String countchd = chartDataPrefix;
		
		for (PracticeResult pr : summary.getPracticeResults()) {
			scorechd += pr.getMark() + chartDataSeparator;
			countchd += pr.getFullMark() + chartDataSeparator;			// round to 2 digit
			chl += LanguageUtil.formatGradeDescription(pr.getGrade(), locale).getDescription() + chartLabelSeparator;
		}
		summary.setScoreChartRS(scorechd.substring(0, scorechd.length()-1) + "&" + chl.substring(0, chl.length() -1));
		summary.setCountChartRS(countchd.substring(0, countchd.length()-1) + "&" + chl.substring(0, chl.length() -1));
		logger.info("setPracticeSummaryCharts: scoreChartRS[" + summary.getScoreChartRS() + "]");
		logger.info("setPracticeSummaryCharts: countChartRS[" + summary.getCountChartRS() + "]");
	}
}
