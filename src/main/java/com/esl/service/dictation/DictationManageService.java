package com.esl.service.dictation;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.IMemberDAO;
import com.esl.dao.dictation.IDictationDAO;
import com.esl.dao.dictation.IDictationHistoryDAO;
import com.esl.dao.dictation.IMemberDictationHistoryDAO;
import com.esl.dao.dictation.IVocabDAO;
import com.esl.dao.dictation.IVocabHistoryDAO;
import com.esl.entity.dictation.Dictation;
import com.esl.entity.dictation.DictationHistory;
import com.esl.entity.dictation.MemberDictationHistory;
import com.esl.entity.dictation.Vocab;
import com.esl.exception.BusinessValidationException;
import com.esl.exception.IllegalParameterException;
import com.esl.model.Member;
import com.esl.model.group.MemberGroup;

@Service("dictationManageService")
@Transactional
public class DictationManageService implements IDictationManageService {
	private static Logger logger = LoggerFactory.getLogger("ESL");

	public static int MAX_VOCABS = 100;

	// supporting class
	@Resource private IDictationDAO dictationDAO;
	@Resource private IMemberDictationHistoryDAO memberDictationHistoryDAO;
	@Resource private IDictationHistoryDAO dictationHistoryDAO;
	@Resource private IMemberDAO memberDAO;
	@Resource private IVocabDAO vocabDAO;
	@Resource private IVocabHistoryDAO vocabHistoryDAO;

	//	 ============== Setter / Getter ================//
	@Value("${Dictation.MaxVocabs}") public void setMaxVocabs(int maxVocabs) { MAX_VOCABS = maxVocabs; }

	//	 ============== Constructor ================//
	public DictationManageService() {}

	//	 ============== Functions ================//
	/**
	 * Get Dictations by member
	 */
	public List<Dictation> getDictationsByMember(Member member) {
		logger.info("getDictationsByMember: START");
		if (member == null) throw new IllegalParameterException(new String[]{"member"}, new Object[]{member});

		logger.info("getDictationsByMember: input member[" + member.getUserId() + "]");
		List<Dictation> results = dictationDAO.listByMember(member);
		if (results == null) {
			logger.info("getDictationsByMember: No dictation return");
			return new ArrayList<Dictation>();
		} else {
			logger.info("getDictationsByMember: Returned dictations size[" + results.size() + "]");
			return results;
		}
	}

	/**
	 * Get DictationHistory by member
	 * Return all if maxResult < 1
	 */
	public List<MemberDictationHistory> getDictationsHistoriesByMember(Member member, int maxResult) {
		logger.info("getDictationsHistoriesByMember: START");
		if (member == null) throw new IllegalParameterException(new String[]{"member"}, new Object[]{member});

		logger.info("getDictationsHistoriesByMember: input member[" + member.getUserId() + "], maxResult[" + maxResult + "]");
		List<MemberDictationHistory> results = memberDictationHistoryDAO.listByMember(member, maxResult);
		if (results == null) {
			logger.info("getDictationsHistoriesByMember: No history return");
			return new ArrayList<MemberDictationHistory>();
		} else {
			logger.info("getDictationsHistoriesByMember: Returned history size[" + results.size() + "]");
			return results;
		}
	}

	/**
	 * Get Dictation History by dictation, return all if maxResult < 1
	 */
	public List<DictationHistory> getDictationsHistoriesByDictation(Dictation dictation, int maxResult) {
		final String logTitle = "getDictationsHistoriesByDictation: ";
		logger.info(logTitle + "START");
		if (dictation == null) throw new IllegalParameterException(new String[]{"dictation"}, new Object[]{dictation});

		logger.info(logTitle + "input dictation[" + dictation.getId() + "], maxResult[" + maxResult + "]");
		List<DictationHistory> results = dictationHistoryDAO.listByDictation(dictation, maxResult);
		if (results == null) {
			logger.info(logTitle + "No history return");
			return new ArrayList<DictationHistory>();
		} else {
			logger.info(logTitle + "Returned history size[" + results.size() + "]");
			return results;
		}
	}


	/**
	 * Get Dictations that can be access by the group
	 * Return all if maxResult < 1
	 */
	public List<Dictation> getDictationsByGroup(MemberGroup group, int maxResult) {
		final String logPrefix = "getDictationsByGroup: ";
		logger.info(logPrefix + "START");
		if (group == null) throw new IllegalParameterException(new String[]{"group"}, new Object[]{group});

		logger.info(logPrefix + "input group[" + group.getTitle() + "], maxResult[" + maxResult + "]");
		List<Dictation> results = dictationDAO.listByMemberGroup(group, maxResult);
		if (results == null) {
			logger.info(logPrefix + "No dictation return");
			return new ArrayList<Dictation>();
		} else {
			logger.info(logPrefix + "Returned dictation size[" + results.size() + "]");
			return results;
		}
	}

	/**
	 * Save a new / edited dictation
	 */
	public boolean saveDictation(Dictation dictation) throws BusinessValidationException {
		final String logPrefix = "saveDictation: ";
		logger.info(logPrefix + "START");
		if (dictation == null) throw new IllegalParameterException(new String[]{"dictation"}, new Object[]{dictation});

		// Checking
		if (dictation.getCreator() == null) throw new BusinessValidationException("exception.noCreator",logPrefix + "no creator set");
		if (dictation.getVocabs() == null || dictation.getVocabs().size() < 1) throw new BusinessValidationException(BusinessValidationException.NO_VOCAB_SET,logPrefix + "no vocab set");

		dictationDAO.persist(dictation);
		logger.info(logPrefix + "dictation [" + dictation.getId() + "] persisted");
		return true;
	}

	/**
	 * Set new vocabs list and remove the old vocabs
	 */
	public boolean setVocabs(Dictation dictation, String vocabs) throws BusinessValidationException {
		final String logPrefix = "setVocabs: ";
		logger.info(logPrefix + "START");
		if (dictation == null) throw new IllegalParameterException(new String[]{"dictation"}, new Object[]{dictation});

		logger.info(logPrefix + "dictation[" + dictation.getId() + "] original vocabs [" + dictation.getVocabsSize() + "]");
		logger.info(logPrefix + "input vocabs string [" + vocabs + "]");

		String[] vocabsArr = vocabs.split(Dictation.SEPARATOR);
		List<Vocab> newVocabs = new ArrayList<Vocab>();
		List<String> addedWord = new ArrayList<String>();

		Outer:
			for (String s : vocabsArr) {
				s = s.trim();
				if (addedWord.contains(s)) continue Outer;
				for (Vocab v : dictation.getVocabs()) {
					if (s.equals(v.getWord())) {
						newVocabs.add(v);
						addedWord.add(s);
						continue Outer;
					}
				}
				Vocab v = new Vocab(s);
				v.setDictation(dictation);
				newVocabs.add(v);
				addedWord.add(s);
			}

		if (newVocabs.size() > MAX_VOCABS) throw new BusinessValidationException(BusinessValidationException.TOO_MANY_VOCABS,logPrefix + "Vocabs size [" + dictation.getVocabsSize() + "] too large");

		// merge and remove entity
		dictation.getVocabs().removeAll(newVocabs);
		vocabHistoryDAO.removeByVocabs(dictation.getVocabs());
		vocabDAO.deleteAll(dictation.getVocabs());
		dictation.getVocabs().clear();
		dictation.getVocabs().addAll(newVocabs);
		return true;
	}

	/**
	 * Return true if user allow to edit the dictation
	 */
	@Transactional(readOnly=true)
	public boolean allowEdit(Dictation dictation, Member user) {
		final String logPrefix = "allowEdit: ";
		logger.info(logPrefix + "START");
		if (dictation == null) throw new IllegalParameterException(new String[]{"dictation"}, new Object[]{dictation});

		if (user == null) return false;
		if (dictation.getCreator().equals(user)) return true;
		return false;
	}

	/**
	 * Return true if user allow to view / practice the dictation
	 */
	@Transactional(readOnly=true)
	public boolean allowView(Dictation dictation, Member user) {
		final String logPrefix = "allowView: ";
		logger.info(logPrefix + "START");
		if (dictation == null) throw new IllegalParameterException(new String[]{"dictation"}, new Object[]{dictation});

		dictationDAO.attachSession(dictation);
		if (dictation.isPublicAccess()) return true;
		if (user == null) return false;

		dictationDAO.attachSession(dictation);
		memberDAO.attachSession(user);

		if (dictation.getCreator().equals(user)) return true;
		// check in group
		for (MemberGroup g : dictation.getAccessibleGroups()) {
			if (user.getGroups().contains(g)) {
				logger.info(logPrefix + "user in group[" + g.getTitle() + "]");
				return true;
			}
		}

		return false;
	}

	/**
	 * Rating a dictation
	 */
	public boolean rateDictation(Dictation dictation, int rating) {
		final String logPrefix = "rateDictation: ";
		logger.info(logPrefix + "START");
		if (dictation == null) throw new IllegalParameterException(new String[]{"dictation"}, new Object[]{dictation});

		dictationDAO.attachSession(dictation);
		int total = dictation.getTotalRated() + 1;
		double rate = (dictation.getRating() * dictation.getTotalRated() + rating) / total;
		logger.info(logPrefix + "Dictation[" + dictation.getId() + "] new rate [" + rate + "] by total[" + total + "]");
		dictation.setTotalRated(total);
		dictation.setRating(rate);
		dictationDAO.persist(dictation);
		return true;
	}

	@Override
	public int getMaxVocabs() { return MAX_VOCABS; }
}
