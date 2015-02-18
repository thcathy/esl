package com.esl.service.group;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.*;
import com.esl.dao.group.*;
import com.esl.exception.BusinessValidationException;
import com.esl.exception.IllegalParameterException;
import com.esl.model.*;
import com.esl.model.group.*;
import com.esl.web.model.group.GroupSummaryByMember;

@Service("memberGroupService")
@Transactional
public class MemberGroupService implements IMemberGroupService {
	private static Logger logger = LoggerFactory.getLogger(MemberGroupService.class);

	// supporting class
	@Resource private IMemberGroupDAO memberGroupDAO;
	@Resource(name="groupPracticeResultDAO") private IGroupPracticeResultDAO practiceResultDAO;
	@Resource private IMemberGroupMessageDAO messageDAO;
	@Resource private IMemberGroupActivityLogDAO memberGroupActivityLogDAO;
	@Resource private IMemberDAO memberDAO;
	@Resource private IGradeDAO gradeDAO;

	//	 ============== Setter / Getter ================//


	//	 ============== Constructor ================//
	public MemberGroupService() {}


	//	 ============== Functions ================//

	/**
	 * Create new MemberGroup
	 */
	public MemberGroup createGroup(MemberGroup group, Member admin) throws BusinessValidationException {
		if (group == null || admin == null) throw new IllegalParameterException(new String[]{"group","admin"}, new Object[]{group,admin});

		// check duplicate title
		if (memberGroupDAO.getMemberGroupByTitle(group.getTitle()) != null) throw new BusinessValidationException("exception.duplicateTitle","Member Group Title[" + group.getTitle() + "] is used");

		// save group
		group.setAdmin(admin);
		admin.addGroup(group);
		memberGroupDAO.persist(group);
		memberDAO.makePersistent(admin);
		logger.info("createGroup: New Member Group(" + group.getId() + ") created by Member[" + admin.getUserId() + "]");

		// save log
		MemberGroupActivityLog log = new MemberGroupActivityLog(group, admin, MemberGroupActivityLog.CREATE_GROUP);
		((IESLDao) memberGroupActivityLogDAO).persist(log);

		// create practice result
		createPracticeResults(group, PracticeResult.PHONETICPRACTICE);

		return group;
	}

	public MemberGroup joinGroup(Member member, Long id, String PIN) throws BusinessValidationException {
		if (member == null) throw new IllegalParameterException(new String[]{"member"}, new Object[]{member});

		memberDAO.attachSession(member);

		// check group is exist
		MemberGroup group = memberGroupDAO.getMemberGroupById(id);
		logger.info(group.toString());
		if (group == null) throw new BusinessValidationException("exception.memberGroupNotFound","joinGroup: Group ID[" + id + "] not found");

		// check member is joined
		if (group.getMembers().contains(member)) throw new BusinessValidationException("exception.alreadyJoinGroup", "joinGrop: Member[" + member.getUserId() + "] already joined Group[" + group.getTitle() + "]");

		// check group pin
		if (!group.getPIN().equals(PIN)) throw new BusinessValidationException("exception.wrongGroupPIN","joinGroup: Wrong Group[" + group.getTitle() + "] PIN[" + PIN + "]");

		// save group
		member.addGroup(group);
		memberDAO.makePersistent(member);
		logger.info("joinGroup: Member[" + member.getUserId() + "] joined Group[" + group.getTitle() + "]");

		// save log
		MemberGroupActivityLog log = new MemberGroupActivityLog(group, member, MemberGroupActivityLog.JOIN_GROUP);
		memberGroupActivityLogDAO.persist(log);
		return group;
	}

	/**
	 * Retrieve a group summary for UI group summary page
	 */
	public GroupSummaryByMember getGroupSummaryByMember(MemberGroup group, Member member) {
		if (group == null || member == null) throw new IllegalParameterException(new String[]{"group","member"}, new Object[]{group,member});

		memberDAO.attachSession(member);
		memberGroupDAO.attachSession(group);

		GroupSummaryByMember summary = new GroupSummaryByMember();
		PracticeResult result = practiceResultDAO.getPracticeResult(member, null, PracticeResult.PHONETICPRACTICE);
		summary.setGroup(group);
		summary.setHaveNewMsg(messageDAO.haveNewMessage(group));
		summary.setPhonPractResult(result);
		summary.setPhonRateRank(practiceResultDAO.getPosition(group, TopResult.OrderType.Rate, result));
		summary.setPhonScoreRank(practiceResultDAO.getPosition(group, TopResult.OrderType.Score, result));
		summary.setPhonTeamRank(practiceResultDAO.getRank(practiceResultDAO.getGroupResult(group, null, PracticeResult.PHONETICPRACTICE)));

		logger.info("getGroupSummaryByMember: retrieved summary[" + summary + "], size[" + summary.getTotalMember() + "]");

		return summary;
	}

	/**
	 * the member leave the group
	 */
	public boolean leaveGroup(MemberGroup group, Member member) throws BusinessValidationException {
		if (group == null || member == null) throw new IllegalParameterException(new String[]{"group","member"}, new Object[]{group,member});

		memberGroupDAO.attachSession(group);
		memberDAO.attachSession(member);

		// check member is inside group
		if (!group.getMembers().contains(member)) throw new BusinessValidationException("exception.memberNotInGroup","leaveGroup: Member[" + member.getUserId() + "] do not in group[" + group.getTitle() + "]");
		// check member is not admin
		if (group.getAdmin().equals(member)) throw new BusinessValidationException("exception.adminLeaveGroup","leaveGroup: Cannot leave group[" + group.getTitle() + "] as member[" + member.getUserId() + "] is admin");

		// remove member
		member.getGroups().remove(group);
		group.getMembers().remove(member);
		memberGroupDAO.persist(group);
		memberDAO.makePersistent(member);
		logger.info("leaveGroup: Member[" + member.getUserId() + "] leave group[" + group.getTitle() + "] updated");

		// save log
		MemberGroupActivityLog log = new MemberGroupActivityLog(group, member, MemberGroupActivityLog.LEAVE_GROUP);
		memberGroupActivityLogDAO.persist(log);
		return true;
	}

	/**
	 * Update an existing group (no data checking)
	 */
	public boolean updateGroup(MemberGroup group) {
		if (group == null) throw new IllegalParameterException(new String[]{"group"}, new Object[]{group});

		// save group
		memberGroupDAO.persist(group);
		logger.info("updateGroup: New Member Group(" + group.getId() + ") updated");

		return true;
	}

	/**
	 * Update the group pin if exist pin correct
	 */
	public boolean updatePIN(MemberGroup group, Member admin, String existPIN, String newPIN) throws BusinessValidationException {
		if (group == null) throw new IllegalParameterException(new String[]{"group"}, new Object[]{group});

		// check exist pin correct
		if (!group.getPIN().equals(existPIN)) throw new BusinessValidationException("exception.wrongGroupPIN","updatePIN: PIN[" + existPIN + "] incorrect in group[" + group.getTitle() + "]");
		// check is admin
		if (!group.getAdmin().equals(admin)) throw new BusinessValidationException("exception.notAdmin","updatePIN: Member[" + admin.getUserId() + "] is not group[" + group.getTitle() + "] admin");

		// update group
		logger.info("updatePIN: Group[" + group.getTitle() + "] PIN is updated");
		group.setPIN(newPIN);
		boolean result = updateGroup(group);
		if (result) {
			// save log
			MemberGroupActivityLog log = new MemberGroupActivityLog(group, admin, MemberGroupActivityLog.CHANGE_PIN);
			memberGroupActivityLogDAO.persist(log);
		}
		return result;
	}

	/**
	 * Update the title by admin only
	 */
	public boolean updateTitle(MemberGroup group, Member admin, String newTitle) throws BusinessValidationException {
		if (group == null || admin == null) throw new IllegalParameterException(new String[]{"group","admin"}, new Object[]{group,admin});

		// check is admin
		if (!group.getAdmin().equals(admin)) throw new BusinessValidationException("exception.notAdmin","updateTitle: Member[" + admin.getUserId() + "] is not group[" + group.getTitle() + "] admin");
		// check title not using
		MemberGroup aGroup = memberGroupDAO.getMemberGroupByTitle(newTitle);
		if (aGroup != null && !aGroup.equals(group)) throw new BusinessValidationException("exception.duplicateTitle","updateTitle: Title[" + newTitle + "] is using by group ID[" + aGroup.getId() + "]");

		// update group
		group.setTitle(newTitle);
		boolean result = updateGroup(group);
		if (result) {
			// save log
			MemberGroupActivityLog log = new MemberGroupActivityLog(group, admin, MemberGroupActivityLog.CHANGE_TITLE);
			memberGroupActivityLogDAO.persist(log);
		}
		logger.info("updateTitle: Group ID[" + group.getId() + "] title[" + newTitle + "] is updated");
		return result;
	}

	/**
	 * Kick member by admin only
	 */
	public boolean kickMember(MemberGroup group, Member admin, Member member) throws BusinessValidationException {
		if (group == null || admin == null || member == null) throw new IllegalParameterException(new String[]{"group","admin","member"}, new Object[]{group,admin,member});

		// check is admin
		if (!group.getAdmin().equals(admin)) throw new BusinessValidationException("exception.notAdmin","kickMember: Member[" + admin.getUserId() + "] is not group[" + group.getTitle() + "] admin");
		// check not remove admin
		if (group.getAdmin().equals(member)) throw new BusinessValidationException("exception.adminLeaveGroup","kickMember: Member[" + member.getUserId() + "] is group[" + group.getTitle() + "] admin");

		// call leave group for remove member by admin
		return leaveGroup(group, member);
	}

	/**
	 * Create practice results for the new group
	 */
	private void createPracticeResults(MemberGroup group, String practiceType) {
		MemberGroupPracticeResult pr = new MemberGroupPracticeResult();

		pr.setFullMark(0);
		pr.setGrade(null);
		pr.setGroup(group);
		pr.setMark(0);
		pr.setPracticeType(practiceType);
		practiceResultDAO.makePersistent(pr);
		logger.info("createPracticeResults: saved practice result [" + pr + "]");

		List<Grade> grades = gradeDAO.getAll();
		for (Grade grade : grades) {
			logger.info("createPracticeResults: create for grade [" + grade.getTitle() + "]");
			pr = new MemberGroupPracticeResult();
			pr.setFullMark(0);
			pr.setGrade(grade);
			pr.setGroup(group);
			pr.setMark(0);
			pr.setPracticeType(practiceType);
			practiceResultDAO.makePersistent(pr);
			logger.info("createPracticeResults: saved practice result [" + pr + "]");
		}
	}
}
