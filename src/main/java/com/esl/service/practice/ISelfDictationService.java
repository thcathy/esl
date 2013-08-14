package com.esl.service.practice;

import java.util.List;

import javax.servlet.ServletContext;

import com.esl.entity.dictation.*;
import com.esl.model.*;

public interface ISelfDictationService {
	public int getMaxQuestions();
	public void setMaxQuestions(int maxQuestions);

	public PhoneticPractice generatePractice(Member member, List<String> inputVocabularies, ServletContext context);
	public PhoneticPractice generatePractice(List<Vocab> vocabs, ServletContext context);
	public void completedPractice(List<PhoneticQuestion> questions, ServletContext context);
	public MemberDictationHistory updateMemberDictationHistory(Dictation dictation, Member member, PhoneticPractice practice);
	public DictationHistory createDictationHistory(Dictation dictation, Member member, PhoneticPractice practice);
}
