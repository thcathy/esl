package com.esl.web.jsf.controller.dictation;

import com.esl.web.jsf.controller.ESLController;
import com.esl.web.model.dictation.DictationStatistics;
import com.esl.web.util.DictationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;

@Controller
@Scope("request")
public class SingleDictationStatController extends ESLController {
	private final Logger logger = LoggerFactory.getLogger("ESL");
	private static String bundleName = "messages.member.Dictation";

	//	 Supporting instance

	//	 ============== UI display data ================//
	@Resource private DictationStatistics stat;
	private String statTitle;

	//============== Functions ================//
	@PostConstruct
	public void init() {
		final String logPrefix = "init: ";
		logger.debug("{}START", logPrefix);

		if (stat != null) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			statTitle = DictationUtil.getStatisticsTitle(stat, facesContext.getViewRoot().getLocale());
			logger.debug("{}statTille [{}]", logPrefix, statTitle);
		}
	}

	//	============== Getter Function ================//

	//	============== Supporting Function ================//

	//	 ============== Setter / Getter ================//
	public DictationStatistics getStat() {	return stat;}
	public void setStat(DictationStatistics stat) {	this.stat = stat;}

	public String getStatTitle() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return DictationUtil.getStatisticsTitle(stat, facesContext.getViewRoot().getLocale());
	}
	public void setStatTitle(String statTitle) {this.statTitle = statTitle;}

}
