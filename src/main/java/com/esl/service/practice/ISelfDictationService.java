package com.esl.service.practice;

import com.esl.entity.dictation.Dictation;
import com.esl.entity.dictation.DictationHistory;
import com.esl.entity.dictation.MemberDictationHistory;
import com.esl.entity.dictation.Vocab;
import com.esl.model.Member;
import com.esl.model.PhoneticPractice;
import com.esl.model.PhoneticQuestion;

import javax.servlet.ServletContext;
import java.util.List;

public interface ISelfDictationService {
	public int getMaxQuestions();
	public void setMaxQuestions(int maxQuestions);

	public PhoneticPractice generatePractice(Member member, List<String> inputVocabularies);
	public PhoneticPractice generatePractice(List<Vocab> vocabs);
	public void completedPractice(List<PhoneticQuestion> questions, ServletContext context);
	public MemberDictationHistory updateMemberDictationHistory(Dictation dictation, Member member, PhoneticPractice practice);
	public DictationHistory createDictationHistory(Dictation dictation, Member member, PhoneticPractice practice);
}
