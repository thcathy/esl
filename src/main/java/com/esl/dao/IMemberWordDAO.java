package com.esl.dao;

import java.util.*;

import com.esl.exception.DBException;
import com.esl.exception.IllegalParameterException;
import com.esl.model.*;

public interface IMemberWordDAO {
	public MemberWord getMemberWordById(Long id);	
	public void persist(MemberWord word);
	public void transit(MemberWord word);
	
	public MemberWord getWord(Member member, PhoneticQuestion word) throws IllegalParameterException;
	public List<MemberWord> listWords(Member member) throws IllegalParameterException;
	public List<MemberWord> listLearntWords(Member member) throws IllegalParameterException;
	public List<MemberWord> listRandomWords(Member member, int total, Collection<MemberWord> excludeWords) throws IllegalParameterException;
	public int totalWords(Member member) throws IllegalParameterException;	
	public int deleteWordsByRate(Member member, double rate) throws IllegalParameterException;
	public int deleteWordsByCorrectCount(Member member, int count) throws IllegalParameterException;
	
}