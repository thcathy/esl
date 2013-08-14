package com.esl.dao.group;

import java.util.List;

import com.esl.exception.IllegalParameterException;
import com.esl.model.group.MemberGroup;
import com.esl.model.group.MemberGroupMessage;

public interface IMemberGroupMessageDAO {
	public MemberGroupMessage getMemberGroupMessageById(Long id);
	public void persist(MemberGroupMessage group);
	public void transit(MemberGroupMessage group);

	public boolean haveNewMessage(MemberGroup group) throws IllegalParameterException;
	public List<MemberGroupMessage> listByGroup(MemberGroup group) throws IllegalParameterException;
}
