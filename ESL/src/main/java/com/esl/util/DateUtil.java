package com.esl.util;

import java.sql.Date;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class DateUtil {
	private static Logger logger = Logger.getLogger("ESL");

	/**
	 * To first day of month
	 */
	public static java.sql.Date toFirstDayOfMonth(java.sql.Date inputDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(inputDate);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return new Date(c.getTime().getTime());
	}
}
