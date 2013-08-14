package com.esl.model;

import java.io.Serializable;
import java.util.*;

import com.esl.model.group.MemberGroup;

public class Member implements Serializable {
	private Long id;
	private String userId;
	private Name name = new Name();
	private Date birthday;
	private String address;
	private String phoneNumber;
	private String PIN;
	private String school;
	private int totalWordLearnt = 0;
	private Date activatedDate;
	private boolean acceptedTerm = false;
	private String emailAddress;
	private Grade grade;
	private String loginedSessionId;
	private String imagePath;
	private Collection<Receipt> receipts = new ArrayList<Receipt>();
	private List phoneticPractices = new ArrayList();
	private List practiceResults = new ArrayList();
	private List<MemberGroup> groups = new ArrayList<MemberGroup>();
	private Date createdDate = new Date();

	// ********************** Constructors ********************** //
	public Member() {}

	public Member(String userId, Name name, Date birthday, String address, String phoneNumber, String PIN, String school, Date activatedDate, boolean acceptedTerm, String emailAddress) {
		this.userId = userId;
		this.name = name;
		this.birthday = birthday;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.PIN = PIN;
		this.school = school;
		this.activatedDate = activatedDate;
		this.emailAddress = emailAddress;
	}

	public Member(String userId, Name name) {
		this.userId = userId;
		this.name = name;
	}

	// ********************** Accessor Methods ********************** //
	public Long getId() { return id; }
	private void setId(Long id) { this.id = id; }

	public String getUserId() { return userId; }
	public void setUserId(String userId) { this.userId = userId; }

	public Name getName() { return name; }
	public void setName(Name name) { this.name = name; }
	public void setName(String lastName, String firstName) {
		Name aName = new Name(lastName, firstName);
		this.name = aName;
	}

	public Date getBirthday() { return birthday; }
	public void setBirthday(Date birthday) { this.birthday = birthday; }
	public int getAge() {
		if (birthday == null) return -1;

		Calendar dateOfBirth = Calendar.getInstance();
		dateOfBirth.setTime(birthday);
		Calendar today = Calendar.getInstance();

		int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
		// Add the tentative age to the date of birth to get this year's birthday
		dateOfBirth.add(Calendar.YEAR, age);
		// If this year's birthday has not happened yet, subtract one from age
		if (today.before(dateOfBirth)) age--;
		return age;
	}

	public Grade getGrade() {return grade;}
	public void setGrade(Grade grade) {this.grade = grade;}

	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }

	public String getPhoneNumber() { return phoneNumber; }
	public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

	public String getPIN() { return PIN; }
	public void setPIN(String PIN) { this.PIN = PIN; }

	public String getSchool() { return school; }
	public void setSchool(String school) { this.school = school; }

	public Date getActivatedDate() { return activatedDate; }
	public void setActivatedDate(Date activatedDate) { this.activatedDate = activatedDate; }

	public boolean isAcceptedTerm() { return acceptedTerm; }
	public void setAcceptedTerm(boolean acceptedTerm) { this.acceptedTerm = acceptedTerm; }

	public String getEmailAddress() { return emailAddress; }
	public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

	public int getTotalWordLearnt() {return totalWordLearnt;}
	public void setTotalWordLearnt(int totalWordLearnt) {this.totalWordLearnt = totalWordLearnt;}

	public Date getCreatedDate() { return createdDate; }
	public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

	public Collection getReceipts() { return receipts; }
	public void setReceipts(Collection receipts) { this.receipts = receipts; }
	public void addReceipt(Receipt receipt) {
		if (receipt == null) throw new IllegalArgumentException("Can't add a null receipt.");

		receipt.setOwner(this);				// bidirectional mapping
		this.receipts.add(receipt);
	}

	public List getPhoneticPractices() { return phoneticPractices; }
	public void setPhoneticPractices(List phoneticPractices) { this.phoneticPractices = phoneticPractices; }
	public void addPhoneticPractices(PhoneticPracticeHistory phoneticPractice) {
		if (phoneticPractice == null) throw new IllegalArgumentException("Can't add a null phonetic practice.");
		phoneticPractice.setMember(this);
		this.phoneticPractices.add(phoneticPractice);
	}


	public List<MemberGroup> getGroups() {return groups;}
	public void setGroups(List<MemberGroup> groups) {this.groups = groups;}
	public void addGroup(MemberGroup group) {
		if (group == null) throw new IllegalArgumentException("Can't add a null group.");
		group.addMember(this);
		this.groups.add(group);
	}

	public List getPracticeResults() { return practiceResults; }
	public void setPracticeResults(List practiceResults) { this.practiceResults = practiceResults; }

	public String getLoginedSessionId() {return loginedSessionId;}
	public void setLoginedSessionId(String loginedSessionId) {this.loginedSessionId = loginedSessionId;}

	public String getImagePath() {return imagePath;}
	public void setImagePath(String imagePath) {this.imagePath = imagePath;}

	// ********************** Common Methods ********************** //
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof Member)) return false;

		final Member member = (Member) o;
		return this.id.equals(member.getId());
	}

	@Override
	public int hashCode()
	{
		return id==null ? System.identityHashCode(this) : id.hashCode();
	}

	@Override
	public String toString() {
		return  "Member ('" + getId() + "'), " +
		"User ID: '" + getUserId() + "' " +
		"Name: '" + getName() + "' ";
	}

	public void copy(Member member) {
		this.id = member.id;
		this.userId = member.userId;
		this.name = member.name;
		this.birthday = member.birthday;
		this.address = member.address;
		this.phoneNumber = member.phoneNumber;
		this.PIN = member.PIN;
		this.school = member.school;
		this.activatedDate = member.activatedDate;
		this.acceptedTerm = member.acceptedTerm;
		this.emailAddress = member.emailAddress;
		this.grade = member.grade;
		this.receipts = member.receipts;
		this.phoneticPractices = member.phoneticPractices;
		this.createdDate = member.createdDate;
	}


}
