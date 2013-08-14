package com.esl.web.util;

import java.util.*;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.esl.entity.dictation.Dictation;
import com.esl.entity.dictation.Dictation.AgeGroup;
import com.esl.model.group.MemberGroup;
import com.esl.model.practice.PhoneticSymbols;


public class SelectItemUtil {
	private static Logger logger = Logger.getLogger("ESL");

	/**
	 * 
	 */
	public static List<SelectItem> getPhoneticSymobolPracticeLevels() {
		logger.info("getPhoneticSymobolPracticeLevels: START");

		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> items = new ArrayList<SelectItem>();

		for (PhoneticSymbols.Level l : PhoneticSymbols.Level.values()) {
			String desc = LanguageUtil.getLevelTitle(l, locale);
			SelectItem item = new SelectItem(l.toString(), desc);
			logger.info("setLevels: create new SelectItem [" + item + "]");
			items.add(item);
		}

		return items;
	}


	/**
	 * return dictation available age groups
	 */
	public static List<SelectItem> getAvailableAgeGroups() {
		List<SelectItem> items = new ArrayList<SelectItem>();

		for (AgeGroup g : Dictation.AgeGroup.values()) {
			SelectItem item = new SelectItem(g.ordinal(), g.toString());
			items.add(item);
		}
		return items;
	}

	/**
	 * return member's groups of input member, remember link member to db
	 */
	public static List<SelectItem> getAvailableMemberGroups(List<MemberGroup> groups) {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (MemberGroup g : groups) {
			SelectItem item = new SelectItem(g.getId(), g.getTitle());
			items.add(item);
		}
		return items;
	}
}
