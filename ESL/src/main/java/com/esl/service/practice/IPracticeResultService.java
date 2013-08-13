package com.esl.service.practice;

import java.util.*;
import com.esl.model.*;
import com.esl.model.practice.PhoneticSymbols.Level;
import com.esl.web.model.practice.PracticeResultSummary;

public interface IPracticeResultService {	
	
	public PracticeResultSummary getPracticeResultSummary(Member member, String practiceType, Level level);
	public List<PracticeResultSummary> getAllPracticeResultSummary(Member member);
}
