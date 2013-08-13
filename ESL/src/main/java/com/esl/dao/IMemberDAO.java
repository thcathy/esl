package com.esl.dao;

import com.esl.model.Member;

public interface IMemberDAO extends IESLDao<Member> {
	public Member getMemberById(Long id);
	public Member getMemberByUserID(String userId);
	public void makePersistent(Member member);
	public void makeTransient(Member member);

	/**
	 * Get member by logined session id
	 */
	public Member getMemberByLoginedSessionID(String sessionId);
}
