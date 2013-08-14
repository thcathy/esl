package com.esl.service.dictation;

import static com.esl.entity.dictation.DictationSearchCriteria.Accessible;
import static com.esl.entity.dictation.DictationSearchCriteria.CreatorName;
import static com.esl.entity.dictation.DictationSearchCriteria.Description;
import static com.esl.entity.dictation.DictationSearchCriteria.MaxAge;
import static com.esl.entity.dictation.DictationSearchCriteria.MaxDate;
import static com.esl.entity.dictation.DictationSearchCriteria.MinAge;
import static com.esl.entity.dictation.DictationSearchCriteria.MinDate;
import static com.esl.entity.dictation.DictationSearchCriteria.NotRequirePassword;
import static com.esl.entity.dictation.DictationSearchCriteria.Tag;
import static com.esl.entity.dictation.DictationSearchCriteria.Title;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.dictation.IDictationDAO;
import com.esl.dao.dictation.IDictationHistoryDAO;
import com.esl.entity.dictation.Dictation;
import com.esl.entity.dictation.DictationSearchCriteria;
import com.esl.exception.IllegalParameterException;
import com.esl.model.Member;
import com.esl.web.model.SearchDictationInputForm;
import com.esl.web.model.dictation.DictationStatistics;
import com.esl.web.model.dictation.DictationSummary;

@Transactional
@Service("dictationStatService")
public class DictationStatService implements IDictationStatService {
	private static Logger logger = LoggerFactory.getLogger("ESL");
	public static int maxDictationStatistics = 5;

	// supporting class
	@Resource private DictationStatistics statistics;
	@Resource private IDictationDAO dictationDAO;
	@Resource private IDictationHistoryDAO dictationHistoryDAO;

	//	 ============== Setter / Getter ================//
	@Value("${Dictation.MaxDictationStatistics}")
	public void setMaxDictationStatistics(int maxDictationStatistics) { DictationStatService.maxDictationStatistics = maxDictationStatistics;}

	//	 ============== Constructor ================//
	public DictationStatService() {}

	//	 ============== Functions ================//
	@Override
	public List<Dictation> searchDictation(SearchDictationInputForm inputForm, int maxResult) {
		final String logPrefix = "searchDictation: ";
		logger.info(logPrefix + "START");
		if (inputForm == null) throw new IllegalParameterException(new String[]{"inputForm"}, new Object[]{inputForm});

		Map<DictationSearchCriteria, Object> searchCriteria = new HashMap<DictationSearchCriteria, Object>();
		// Construct search criteria
		if (inputForm.getKeyword() != null || !inputForm.getKeyword().equals("")) {
			if (inputForm.isSearchTitle()) searchCriteria.put(Title, inputForm.getKeyword());
			if (inputForm.isSearchDescription()) searchCriteria.put(Description, inputForm.getKeyword());
			if (inputForm.isSearchTags()) searchCriteria.put(Tag, inputForm.getKeyword());
		}
		if (inputForm.getMinAge() >= 0) searchCriteria.put(MinAge, inputForm.getMinAge());
		if (inputForm.getMaxAge() >= 0) searchCriteria.put(MaxAge, inputForm.getMaxAge());
		if (inputForm.getMinDate() != null) searchCriteria.put(MinDate, inputForm.getMinDate());
		if (inputForm.getMaxDate() != null) searchCriteria.put(MaxDate, inputForm.getMaxDate());
		if (inputForm.getCreatorName() != null || !inputForm.getCreatorName().equals("")) searchCriteria.put(CreatorName, inputForm.getCreatorName());
		if (inputForm.isAccessible()) searchCriteria.put(Accessible, inputForm.getCurrentUser());
		if (inputForm.isNotRequirePassword()) searchCriteria.put(NotRequirePassword, true);
		List<Dictation> result = dictationDAO.searchDictation(searchCriteria, maxResult);

		logger.info(logPrefix + "result size[" + result.size() + "]");
		return result;
	}

	@Override
	public DictationSummary getDictationSummary(Member member) {
		final String logPrefix = "getDictationSummary: ";
		logger.debug(logPrefix + "START");
		if (member == null) return null;

		DictationSummary s = new DictationSummary();
		s.setDictationCreated(dictationDAO.getTotalDictationByMember(member));
		s.setMostRecommendedDictation(dictationDAO.getMostRecommendedByMember(member));
		s.setLastHistory(dictationHistoryDAO.getLastestOfAllDictationByMember(member));
		s.setMostAttemptedDictation(dictationDAO.getMostAttemptedByMember(member));
		s.setTotalAttempted(dictationDAO.getTotalAttemptedByMember(member));
		return s;
	}

	@Override
	public void changeStaticDictationStatistics() {
		final String logPrefix = "changeStaticDictationStatistics: ";
		logger.info("{}START", logPrefix);
		DictationStatistics stat = randomDictationStatistics(maxDictationStatistics);

		synchronized (statistics) {
			statistics.setType(stat.getType());
			statistics.getDictations().clear();
			statistics.getDictations().addAll(stat.getDictations());
		}
	}

	@Override
	public DictationStatistics randomDictationStatistics(int maxResult) {
		final String logPrefix = "randomDictationStatistics: ";
		DictationStatistics stat = new DictationStatistics();
		Random r = new Random();
		stat.setType(DictationStatistics.Type.values()[r.nextInt(4)]);
		logger.info("{}randomed type [{}]", logPrefix, stat.getType());

		switch (stat.getType()) {
		case MostPracticed:
			stat.setDictations(dictationDAO.listMostPracticed(maxResult)); break;
		case NewCreated:
			stat.setDictations(dictationDAO.listNewCreated(maxResult)); break;
		case MostRecommended:
			stat.setDictations(dictationDAO.listMostRecommended(0, maxResult)); break;
		case LatestPracticed:
			stat.setDictations(dictationDAO.listLatestPracticed(maxResult)); break;
		}
		logger.info("{}returned dictations size [{}]", logPrefix, stat.getDictations().size());
		return stat;
	}
}
