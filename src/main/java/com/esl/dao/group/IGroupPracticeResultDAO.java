package com.esl.dao.group;

import java.util.List;

import com.esl.dao.IPracticeResultDAO;
import com.esl.exception.DBException;
import com.esl.exception.IllegalParameterException;
import com.esl.model.*;
import com.esl.model.group.MemberGroup;
import com.esl.model.group.MemberGroupPracticeResult;

public interface IGroupPracticeResultDAO extends IPracticeResultDAO {
	public int getPosition(MemberGroup group, TopResult.OrderType orderType, PracticeResult pResult) throws IllegalParameterException, DBException;
	public List<PracticeResult> listResultsByGroup(MemberGroup group, Grade grade, String practiceType) throws IllegalParameterException;

	// Member Group Practice Result
	public int getRank(MemberGroupPracticeResult result) throws IllegalParameterException;
	public MemberGroupPracticeResult getGroupResult(MemberGroup group, Grade grade, String practiceType) throws IllegalParameterException;
	public List<MemberGroupPracticeResult> listResultsHigher(MemberGroupPracticeResult result) throws IllegalParameterException;
	public List<MemberGroupPracticeResult> listResultsLower(MemberGroupPracticeResult result) throws IllegalParameterException;

	// Schedule tasks
	public void removeAllGroupResult();
	public void importGroupResult();

	// override
	public void makePersistent(MemberGroupPracticeResult result);
	public void makeTransient(MemberGroupPracticeResult result);
}