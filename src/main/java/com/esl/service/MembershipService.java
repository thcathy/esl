package com.esl.service;

import com.esl.dao.IGradeDAO;
import com.esl.dao.IMemberDAO;
import com.esl.model.Member;
import com.esl.util.ValidationUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("membershipService")
@Transactional
public class MembershipService implements IMembershipService {
	private Logger logger = LoggerFactory.getLogger(MembershipService.class);
	
	@Resource private IGradeDAO gradeDAO;
	@Resource private IMemberDAO memberDAO;

	// Return para
	private Member member = null;

	public Member getMember() { return member; }

	public String signUp(Member member) {
		// Check user ID have created
		if (memberDAO.getMemberByUserID(member.getUserId()) != null) {
			logger.info("signUp: USER_ID_DUPLICATED: userId:" + member.getUserId());
			return USER_ID_DUPLICATED;
		}

		// Create new member
		try {
			// All new member having the min grade
			member.setGrade(gradeDAO.getFirstLevelGrade());

			memberDAO.makePersistent(member);
		} catch (Exception e) {
			logger.info("signUp: " + e);
			return SYSTEM_ERROR;
		}
		logger.info("signUp: ACCOUNT_CREATED: userId:" + member.getUserId());
		return ACCOUNT_CREATED;
	}

	public String updateProfile(Member member) {
		if (member == null) {
			logger.warn("updateProfile: USER_ID_NOT_FOUND: userId:" + member.getUserId());
			return USER_ID_NOT_FOUND;
		}
		try {
			memberDAO.makePersistent(member);
		} catch (Exception e) {
			logger.info("updateProfile: " + e);
			return SYSTEM_ERROR;
		}
		logger.info("updateProfile: PROFILE_UPDATED: userId:" + member.getUserId());
		return PROFILE_UPDATED;
	}

	public String login(Member member) {
		try {
			if (!ValidationUtil.isAlphaNumeric(member.getUserId()) || StringUtils.isEmpty(member.getPIN()))
			{
				logger.info("login: INVALID_INPUT: userId:" + member.getUserId());
				return INVALID_INPUT;
			}

			this.member = memberDAO.getMemberByUserID(member.getUserId());
			if (this.member == null)						// user ID not found
				return USER_ID_PASSWORD_NOT_MATCH;
			else if (!this.member.getPIN().equals(member.getPIN()))	// PIN not match
				return USER_ID_PASSWORD_NOT_MATCH;
		} catch (Exception e) {
			logger.info("login: " + e);
			return SYSTEM_ERROR;
		}
		logger.info("login: LOGIN_SUCCEED: userId:" + member.getUserId());
		return LOGIN_SUCCEED;
	}
}
