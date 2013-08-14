package com.esl.service.memberword;

import java.util.*;

import com.esl.model.Grade;
import com.esl.model.Member;
import com.esl.model.MemberWord;
import com.esl.model.PhoneticPractice;
import com.esl.model.PhoneticQuestion;
import com.esl.model.PracticeResult;

public interface IMemberWordManageService {		
				
	public String saveWord(Member member, PhoneticQuestion word);
	public static final String WORD_SAVED = "WORD_SAVED";
	public static final String WORD_ALREADY_SAVED = "WORD_ALREADY_SAVED";
	public static final String OVER_MAX_WORDS = "OVER_MAX_WORDS";
		
	public int deleteWords(List<MemberWord> words);	
	public int deleteLearntWords(Member member);
}
