package com.esl.service.group;

import com.esl.exception.BusinessValidationException;
import com.esl.model.*;
import com.esl.model.group.MemberGroup;
import com.esl.web.model.group.GroupSummaryByMember;

public interface IMemberGroupService {
	// Main function
	public MemberGroup createGroup(MemberGroup group, Member admin) throws BusinessValidationException;
	public boolean updateGroup(MemberGroup group);
	public boolean leaveGroup(MemberGroup group, Member member) throws BusinessValidationException;	
	public MemberGroup joinGroup(Member member, Long id, String PIN) throws BusinessValidationException;
	public boolean updatePIN(MemberGroup group, Member admin, String existPIN, String newPIN) throws BusinessValidationException;
	public boolean updateTitle(MemberGroup group, Member admin, String newTitle) throws BusinessValidationException;
	public boolean kickMember(MemberGroup group, Member admin, Member member) throws BusinessValidationException;
	
	public GroupSummaryByMember getGroupSummaryByMember(MemberGroup group, Member member);
}
