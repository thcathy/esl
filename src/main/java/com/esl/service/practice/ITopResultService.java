package com.esl.service.practice;

import java.util.*;
import com.esl.model.*;
import com.esl.model.practice.PhoneticSymbols.Level;

public interface ITopResultService {	
	
	public TopResult getRandomTopResults();
	
	public TopResult getTopResult(TopResult.OrderType orderType, String practiceType);
	public TopResult getTopResultByGrade(TopResult.OrderType orderType, String practiceType, Grade grade);
	public TopResult getTopResultByGrade(TopResult.OrderType orderType, String practiceType, Grade grade, Level level);
	public TopResult getResultListByMember(TopResult.OrderType orderType, String practiceType, Member member);
	public TopResult getResultListByMemberGrade(TopResult.OrderType orderType, String practiceType, Member member, Grade grade);
	public TopResult getResultListByMemberGrade(TopResult.OrderType orderType, String practiceType, Member member, Grade grade, Level level);
}
