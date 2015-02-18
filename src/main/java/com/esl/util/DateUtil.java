package com.esl.util;

import java.sql.Date;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {
	private static Logger logger = LoggerFactory.getLogger(DateUtil.class);

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
