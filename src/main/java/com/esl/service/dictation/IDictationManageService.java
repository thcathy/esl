package com.esl.service.dictation;

import java.util.List;

import com.esl.entity.dictation.Dictation;
import com.esl.entity.dictation.DictationHistory;
import com.esl.entity.dictation.MemberDictationHistory;
import com.esl.exception.BusinessValidationException;
import com.esl.model.Member;
import com.esl.model.group.MemberGroup;

public interface IDictationManageService {
	public List<Dictation> getDictationsByMember(Member member);
	public List<Dictation> getDictationsByGroup(MemberGroup group, int maxResult);
	public List<MemberDictationHistory> getDictationsHistoriesByMember(Member member, int maxResult);
	public List<DictationHistory> getDictationsHistoriesByDictation(Dictation dictation, int maxResult);
	public boolean saveDictation(Dictation dictation) throws BusinessValidationException;
	public boolean setVocabs(Dictation dictation, String vocabs) throws BusinessValidationException;
	public boolean allowEdit(Dictation dictation, Member user);
	public boolean allowView(Dictation dictation, Member user);
	public boolean rateDictation(Dictation dictation, int rating);
		
	/**
	 * Getter return the maximum number of words allowed
	 */
	public int getMaxVocabs();
}
