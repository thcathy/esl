package com.esl.web.util;

import java.util.*;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.esl.model.*;
import com.esl.model.practice.PhoneticSymbols;
import com.esl.model.practice.PhoneticSymbols.Level;
import com.esl.web.model.practice.PracticeResultSummary;


public class ChartUtil {
	private static Logger logger = Logger.getLogger("ESL");
	
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
