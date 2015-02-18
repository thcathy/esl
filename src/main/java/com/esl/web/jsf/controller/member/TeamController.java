package com.esl.web.jsf.controller.member;

import java.util.*;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esl.dao.IGradeDAO;
import com.esl.dao.IMemberDAO;
import com.esl.dao.group.*;
import com.esl.exception.BusinessValidationException;
import com.esl.model.*;
import com.esl.model.group.*;
import com.esl.service.group.IMemberGroupResultService;
import com.esl.service.group.IMemberGroupService;
import com.esl.web.jsf.controller.ESLController;
import com.esl.web.model.group.GroupSummaryByMember;
import com.esl.web.util.LanguageUtil;

//@Controller
//@Scope("session")
public class TeamController extends ESLController {
	private static Logger logger = LoggerFactory.getLogger(TeamController.class);

	private final String bundleName = "messages.member.Team";
	private final String teamsView = "/member/team/teams";
	private final String teamView = "/member/team/team";
	private final String createTeamView = "/member/team/createteam";
	private final String createTeam2View = "/member/team/createteam2";
	private final String leaveTeamView = "/member/team/leave";
	private final String updateTeamView = "/member/team/update";

	// Supporting instance
	@Resource private IMemberGroupService memberGroupService;
	@Resource private IGroupPracticeResultDAO practiceResultDAO;
	@Resource private IMemberGroupActivityLogDAO activityLogDAO;
	@Resource private IMemberGroupMessageDAO messageDAO;
	@Resource private IGradeDAO gradeDAO;
	@Resource private IMemberDAO memberDAO;
	@Resource private IMemberGroupDAO memberGroupDAO;
	@Resource private IMemberGroupResultService resultService;

	//	 ============== UI display data ================//
	// for My teams index
	private List<GroupSummaryByMember> teamSummaries;
	private String inputID = "";
	private String inputPIN = "";
	private MemberGroup selectedGroup;

	// for create team
	private MemberGroup newGroup;
	private MemberGroup createdGroup;
	private String confirmedPIN;
	private String existPIN;		// for chg pw

	// for My team single view
	private List<PracticeResult> phonPractResults;
	private List<MemberGroupActivityLog> activities;
	private MemberGroupMessage newMessage;
	private Grade selectedGrade;
	private Long selectedGradeID;
	private MemberGroupMessage selectedMessage;
	private List<MemberGroupPracticeResult> phonGroupResults;
	private Map<Long, Integer> phonGroupPosMap;

	// for kick member
	private Member selectedMember;
	private Long selectedMemberID;

	// for leave team
	private boolean showLeaveForm;

	// ============== Setter / Getter ================//
	public void setMemberGroupService(IMemberGroupService memberGroupService) {this.memberGroupService = memberGroupService;}
	public void setPracticeResultDAO(IGroupPracticeResultDAO practiceResultDAO) {this.practiceResultDAO = practiceResultDAO;}
	public void setActivityLogDAO(IMemberGroupActivityLogDAO activityLogDAO) {this.activityLogDAO = activityLogDAO;}
	public void setMessageDAO(IMemberGroupMessageDAO messageDAO) {this.messageDAO = messageDAO;}
	public void setGradeDAO(IGradeDAO gradeDAO) {this.gradeDAO = gradeDAO;}
	public void setMemberDAO(IMemberDAO memberDAO) {this.memberDAO = memberDAO;}
	public void setMemberGroupDAO(IMemberGroupDAO groupDAO) {this.memberGroupDAO = groupDAO; }
	public void setResultService(IMemberGroupResultService resultService) {this.resultService = resultService; }

	public String getInputID() {return inputID;	}
	public void setInputID(String inputID) {this.inputID = inputID;	}

	public String getInputPIN() {return inputPIN;}
	public void setInputPIN(String inputPIN) {this.inputPIN = inputPIN;	}

	public List<GroupSummaryByMember> getTeamSummaries() {return teamSummaries;	}
	public void setTeamSummaries(List<GroupSummaryByMember> teamSummaries) {this.teamSummaries = teamSummaries;	}

	public MemberGroup getSelectedGroup() {return selectedGroup;}
	public void setSelectedGroup(MemberGroup selectedGroup) {this.selectedGroup = selectedGroup;}

	public String getConfirmedPIN() {return confirmedPIN;}
	public void setConfirmedPIN(String confirmedPIN) {this.confirmedPIN = confirmedPIN;}

	public MemberGroup getNewGroup() {return newGroup;}
	public void setNewGroup(MemberGroup newGroup) {this.newGroup = newGroup;}

	public MemberGroup getCreatedGroup() {return createdGroup;}
	public void setCreatedGroup(MemberGroup createdGroup) {this.createdGroup = createdGroup;}

	public List<MemberGroupActivityLog> getActivities() {return activities;}
	public void setActivities(List<MemberGroupActivityLog> activities) {this.activities = activities;}

	public MemberGroupMessage getNewMessage() {return newMessage;}
	public void setNewMessage(MemberGroupMessage newMessage) {this.newMessage = newMessage;	}

	public List<PracticeResult> getPhonPractResults() {	return phonPractResults;}
	public void setPhonPractResults(List<PracticeResult> phonPractResults) {this.phonPractResults = phonPractResults;}

	public boolean isShowLeaveForm() {return showLeaveForm;}
	public void setShowLeaveForm(boolean showLeaveForm) {this.showLeaveForm = showLeaveForm;}

	public String getExistPIN() {return existPIN;}
	public void setExistPIN(String existPIN) {this.existPIN = existPIN;	}

	public Member getSelectedMember() {	return selectedMember;}
	public void setSelectedMember(Member selectedMember) {this.selectedMember = selectedMember;}

	public MemberGroupMessage getSelectedMessage() {return selectedMessage;}
	public void setSelectedMessage(MemberGroupMessage selectedMessage) {this.selectedMessage = selectedMessage;}

	public Map<Long, Integer> getPhonGroupPosMap() {return phonGroupPosMap;}
	public void setPhonGroupPosMap(Map<Long, Integer> phonGroupPosMap) {this.phonGroupPosMap = phonGroupPosMap;}

	public List<MemberGroupPracticeResult> getPhonGroupResults() {return phonGroupResults;}
	public void setPhonGroupResults(List<MemberGroupPracticeResult> phonGroupResults) {this.phonGroupResults = phonGroupResults;}

	public Long getSelectedGradeID() {return selectedGradeID;}
	public void setSelectedGradeID(Long selectedGradeID) {
		this.selectedGradeID = selectedGradeID;
		if (selectedGradeID < 0 )
			this.selectedGrade = null;
		else
			this.selectedGrade = gradeDAO.getGradeById(selectedGradeID);
	}

	public Long getSelectedMemberID() {	return selectedMemberID;}
	public void setSelectedMemberID(Long selectedMemberID) {
		this.selectedMemberID = selectedMemberID;
		if (selectedMemberID < 0)
			this.selectedMember = null;
		else
			this.selectedMember = memberDAO.getMemberById(selectedMemberID);
	}

	// ============== Getter Function ================//
	public String getInitTeams() {
		logger.info("getInitTeams: START");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Locale locale= facesContext.getViewRoot().getLocale();

		if (teamSummaries == null) return "";

		// get practice type title
		for (GroupSummaryByMember summary : teamSummaries) {
			summary.setPracticeType(LanguageUtil.getPracticeType(summary.getPhonPractResult().getPracticeType(), locale));
		}

		return "";
	}

	//	 Return all grades available
	public List<SelectItem> getRemovableMembers() {
		List<SelectItem> items = new ArrayList<SelectItem>();

		for (Member member : selectedGroup.getMembers()) {
			if (!member.equals(selectedGroup.getAdmin()))
				items.add(new SelectItem(member.getId(), member.getUserId() + " : " + member.getName()));
		}
		logger.info("getRemovableMembers: returned items size: " + items.size());
		return items;
	}

	//============== Constructor ================//
	public TeamController() {
		newGroup = new MemberGroup();
		newMessage = new MemberGroupMessage();
	}

	// ============== Functions ================//

	/**
	 * retrieve data require for all teams (index) page
	 */
	public String showTeams() {
		logger.info("showTeams: START");
		Member member = userSession.getMember();
		if (userSession.getMember() == null) {
			logger.warn("showTeams: no member found in session");
			return errorView;
		}

		List<GroupSummaryByMember> summaries = new ArrayList<GroupSummaryByMember>();
		gradeDAO.attachSession(userSession.getMember());
		for (MemberGroup group : member.getGroups()) {
			GroupSummaryByMember summary = memberGroupService.getGroupSummaryByMember(group, member);
			logger.info("showTeams: retrieved summary[" + summary + "]");
			summaries.add(summary);
		}
		if (summaries.size() > 0)
			teamSummaries = summaries;
		else
			teamSummaries = null;

		logger.info("showTeams: END");
		return teamsView;
	}

	/**
	 * create New Team
	 */
	public String createTeam() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		// Check PIN with confirmed PIN
		if (newGroup.getPIN()== null || !newGroup.getPIN().equals(confirmedPIN)) {
			logger.info("createTeam: input PIN[" + newGroup.getPIN() + "," + confirmedPIN + "] different");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("exception.PINNotMatch"), null));
			return null;
		}

		// create group
		try {
			createdGroup = memberGroupService.createGroup(newGroup, userSession.getMember());
		} catch (BusinessValidationException e) {
			logger.info("createTeam: BV Exception:" + e.getMessage());
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString(e.getErrorCode()),null));
			return createTeamView;
		}
		logger.info("createTeam: Group [" + createdGroup.getId() + "," + createdGroup.getTitle() + "] created");
		newGroup = new MemberGroup();
		return createTeam2View;
	}

	/**
	 * Join Team
	 */
	public String joinTeam() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		logger.info("joinTeam: START");

		// join team
		MemberGroup group;
		try {
			group = memberGroupService.joinGroup(userSession.getMember(), newGroup.getId(), newGroup.getPIN());
		}
		catch (BusinessValidationException e) {
			logger.info("joinTeam: BV Exception:" + e.getMessage());
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString(e.getErrorCode()),null));
			return teamsView;
		}

		// success join team
		logger.info("joinTeam: Member[" + userSession.getMember().getUserId() + "] join Group[" + group.getTitle() + "]");
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("joinSuccess"),null));
		newGroup = new MemberGroup();
		return showTeams();				// refresh the new joined team in summary page
	}

	public String launchLeaveTeam() {
		logger.info("launchLeaveTeam: START");
		showLeaveForm = true;
		return leaveTeamView;
	}

	public String launchUpdateTeam() {
		logger.info("launchUpdateTeam: START");
		newGroup = new MemberGroup();
		newGroup.setTitle(selectedGroup.getTitle());
		return updateTeamView;
	}

	/**
	 * leave Team
	 */
	public String leaveTeam() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		logger.info("leaveTeam: START");

		// check member PIN
		if (!userSession.getMember().getPIN().equals(confirmedPIN)) {
			logger.info("leaveTeam: input Member PIN[" + userSession.getMember().getPIN() + "," + confirmedPIN + "] different");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("exception.PINNotMatch"), null));
			return null;
		}
		if (!newGroup.getPIN().equals(selectedGroup.getPIN())) {
			logger.info("leaveTeam: input Group PIN[" + selectedGroup.getPIN() + "," + newGroup.getPIN() + "] different");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("exception.PINNotMatch"), null));
			return null;
		}

		// leave team
		try {
			memberGroupService.leaveGroup(selectedGroup, userSession.getMember());
		}
		catch (BusinessValidationException e) {
			logger.info("leaveTeam: BV Exception:" + e.getMessage());
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString(e.getErrorCode()),null));
			return teamsView;
		}

		// success leave team
		logger.info("leaveTeam: Member[" + userSession.getMember().getUserId() + "] leave Group[" + selectedGroup.getTitle() + "]");
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("leaveSuccess"),null));
		newGroup = new MemberGroup();
		selectedGroup = null;
		showLeaveForm = false;
		return showTeams();				// refresh leave team in teams summary page

	}

	/**
	 * Single Team View
	 */
	public String showTeam() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		logger.info("showTeam: START");

		if (selectedGroup == null) {
			logger.info("showTeam: do not find team selected");
			return errorView;
		}
		logger.info("showTeam: show group[" + selectedGroup.getTitle() + "]");
		// Get teams related data result
		phonPractResults = practiceResultDAO.listResultsByGroup(selectedGroup, selectedGrade, PracticeResult.PHONETICPRACTICE);

		logger.info("showTeam: phonPractResults.size[" + phonPractResults.size() + "]");
		activities = activityLogDAO.listByGroup(selectedGroup);
		if (activities != null) logger.info("showTeam: activities.size[" + activities.size() + "]");
		memberGroupDAO.attachSession(selectedGroup);
		if (selectedGroup.getMessages() != null) logger.info("showTeam: messages.size[" + selectedGroup.getMessages().size() + "]");

		// Get team ranking
		MemberGroupPracticeResult result = practiceResultDAO.getGroupResult(selectedGroup, selectedGrade, PracticeResult.PHONETICPRACTICE);
		phonGroupResults = resultService.listResults(result);
		phonGroupPosMap = resultService.getPositionMap(phonGroupResults);

		logger.info("showTeam: Group result[" + result + "]");
		logger.info("showTeam: Group results list size[" + phonGroupResults.size() + "]");
		logger.info("showTeam: Group results position map size[" + phonGroupPosMap.size() + "]");

		newMessage = new MemberGroupMessage();

		return teamView;
	}

	/**
	 * active when change selected grade
	 */
	public String changeGrade() {
		logger.info("changeGrade: START");

		phonPractResults = practiceResultDAO.listResultsByGroup(selectedGroup, selectedGrade, PracticeResult.PHONETICPRACTICE);
		logger.info("changeGrade: phonPractResults.size[" + phonPractResults.size() + "]");

		// Get team ranking
		MemberGroupPracticeResult result = practiceResultDAO.getGroupResult(selectedGroup, selectedGrade, PracticeResult.PHONETICPRACTICE);
		phonGroupResults = resultService.listResults(result);
		phonGroupPosMap = resultService.getPositionMap(phonGroupResults);
		logger.info("changeGrade: Group result[" + result + "]");
		logger.info("changeGrade: Group results list size[" + phonGroupResults.size() + "]");
		logger.info("changeGrade: Group results position map size[" + phonGroupPosMap.size() + "]");

		return "";
	}

	/**
	 * Sort the phonetic practice result by score
	 */
	public String sortPhonByScore() {
		logger.info("sortPhonByScore: START");
		Collections.sort(phonPractResults, new PracticeResult.TopScoreComparator());
		return "";
	}

	/**
	 * Sort the phonetic practice result by rate
	 */
	public String sortPhonByRate() {
		logger.info("sortPhonByRate: START");
		Collections.sort(phonPractResults, new PracticeResult.TopRateComparator());
		return "";
	}

	/**
	 * update Team title
	 */
	public String updateTitle() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		logger.info("updateTitle: START");

		// update title
		try {
			memberGroupService.updateTitle(selectedGroup, userSession.getMember(), newGroup.getTitle());
		} catch (BusinessValidationException e) {
			logger.info("createTeam: BV Exception:" + e.getMessage());
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString(e.getErrorCode()),null));
			return updateTeamView;
		}
		logger.info("updateTitle: Group Title [" + selectedGroup.getId() + "," + selectedGroup.getTitle() + "] updated");
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("changeTitleSuccess"),null));
		newGroup = new MemberGroup();
		newGroup.setTitle(selectedGroup.getTitle());
		return updateTeamView;
	}

	/**
	 * Update Team PIN
	 */
	public String updatePIN() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		logger.info("updatePIN: START");

		// Check PIN with confirmed PIN
		if (newGroup.getPIN()== null || !newGroup.getPIN().equals(confirmedPIN)) {
			logger.info("updatePIN: input new PIN[" + newGroup.getPIN() + "," + confirmedPIN + "] different");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("exception.PINNotMatch"), null));
			return null;
		}

		// update PIN
		try {
			memberGroupService.updatePIN(selectedGroup, userSession.getMember(), existPIN, newGroup.getPIN());
		} catch (BusinessValidationException e) {
			logger.info("createTeam: BV Exception:" + e.getMessage());
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString(e.getErrorCode()),null));
			return null;
		}
		logger.info("updatePIN: Group[" + selectedGroup.getTitle() + "] PIN updated");
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("changePINSuccess"),null));
		newGroup = new MemberGroup();

		return null;
	}

	/**
	 * create new message
	 */
	public String createMessage() {
		logger.info("createMessage: START");

		if (newMessage.getMessage().equals("")) return null;

		// add message
		newMessage.setGroup(selectedGroup);
		newMessage.setMember(userSession.getMember());

		if (selectedGroup.getMessages() == null) selectedGroup.setMessages(new ArrayList<MemberGroupMessage>());
		selectedGroup.getMessages().add(0, newMessage);
		newMessage = new MemberGroupMessage();
		messageDAO.persist(newMessage);
		memberGroupDAO.persist(selectedGroup);

		logger.info("createMessage: Member[" + userSession.getMember().getUserId() + "] add a message to Group[" + selectedGroup.getTitle() + "]");

		return null;
	}

	/**
	 * kick Member
	 */
	public String kickMember() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		logger.info("kickMember: START");

		if (selectedMember == null) return null;

		// kick member
		try {
			memberGroupService.kickMember(selectedGroup, userSession.getMember(), selectedMember);
		} catch (BusinessValidationException e) {
			logger.info("createTeam: BV Exception:" + e.getMessage());
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString(e.getErrorCode()),null));
			return null;
		}

		logger.info("kickMember: Member[" + selectedMember.getUserId() + "] is removed from Group[" + selectedGroup.getTitle() + "]");
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("kickMemberSuccess"),null));
		selectedMember = null;
		selectedMemberID = null;

		return null;
	}

	/**
	 * delete message
	 */
	public String deleteMessage() {
		logger.info("deleteMessage: START");

		if (selectedMessage == null) return null;

		// check delete right
		if (!selectedMessage.getMember().equals(userSession.getMember()) && !selectedMessage.getGroup().getAdmin().equals(userSession.getMember())) return null;

		// delete message
		memberDAO.attachSession(selectedGroup);
		selectedGroup.getMessages().remove(selectedMessage);
		messageDAO.transit(selectedMessage);
		logger.info("deleteMessage: deleted a msg in group[" + selectedGroup.getTitle() + "], msg.size[" + selectedGroup.getMessages().size() + "]");

		return null;
	}
}
