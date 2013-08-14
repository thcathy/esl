package com.esl.web.jsf.controller.practice;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.esl.dao.practice.IIrregularVerbDAO;
import com.esl.entity.practice.qa.IrregularVerb;
import com.esl.util.BeanUtil;

/**
 * A application scoped controller contains all irregular verbs
 */
@Service("irregularVerbListController")
public class IrregularVerbListController {
	private static Logger logger = LoggerFactory.getLogger("ESL");

	//	 Supporting instance
	@Resource private IIrregularVerbDAO irregularVerbDAO;

	// ============== UI display data ================//
	private List<IrregularVerb> verbs;

	// ============== Constructor ================//
	public IrregularVerbListController() {
		super();
	}

	//============== Functions ================//
	/**
	 * Init method called by Spring to load all irregular verb on startup
	 */
	@PostConstruct
	public void init() {
		verbs = irregularVerbDAO.getAll();
		Collections.sort(verbs, BeanUtil.getCompare("getPresent"));
		logger.info("init: Total verbs loaded [{}]", verbs.size());
	}

	//	============== Getter Function ================//


	//	============== Supporting Function ================//

	//	 ============== Setter / Getter ================//
	public void setIrregularVerbDAO(IIrregularVerbDAO irregularVerbDAO) {this.irregularVerbDAO = irregularVerbDAO;}

	public List<IrregularVerb> getVerbs() {return verbs;}
	public void setVerbs(List<IrregularVerb> verbs) {this.verbs = verbs;}

}
