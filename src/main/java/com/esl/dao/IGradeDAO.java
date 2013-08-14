package com.esl.dao;

import com.esl.model.Grade;

public interface IGradeDAO extends IESLDao<Grade> {
	public Grade getGradeById(Long id);
	public Grade getGradeByTitle(String title);
	public Grade getGradeByLevel(int level);
	public Grade getFirstLevelGrade();
	public void makePersistent(Grade grade);
	public void makeTransient(Grade grade);
}
