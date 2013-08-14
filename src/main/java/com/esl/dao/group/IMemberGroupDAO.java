package com.esl.dao.group;

import com.esl.dao.IESLDao;
import com.esl.model.group.MemberGroup;

public interface IMemberGroupDAO extends IESLDao<MemberGroup> {
	public MemberGroup getMemberGroupById(Long id);
	public MemberGroup getMemberGroupByTitle(String title);
	public void persist(MemberGroup group);
	public void transit(MemberGroup group);
}
