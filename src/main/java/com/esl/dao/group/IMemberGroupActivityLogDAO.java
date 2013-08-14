package com.esl.dao.group;

import java.util.List;

import com.esl.dao.IESLDao;
import com.esl.exception.IllegalParameterException;
import com.esl.model.group.MemberGroup;
import com.esl.model.group.MemberGroupActivityLog;

public interface IMemberGroupActivityLogDAO extends IESLDao<MemberGroupActivityLog> {
	public MemberGroupActivityLog getMemberGroupActivityLogById(Long id);

	public List<MemberGroupActivityLog> listByGroup(MemberGroup group) throws IllegalParameterException;
}
