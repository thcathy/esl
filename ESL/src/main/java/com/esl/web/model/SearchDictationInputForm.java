package com.esl.web.model;

import java.io.Serializable;
import java.util.Date;

import com.esl.model.Member;

public class SearchDictationInputForm implements Serializable {
	private String keyword;
	private boolean searchTitle = true;
	private boolean searchDescription = true;
	private boolean searchTags = true;
	private int minAge = 0;
	private int maxAge = 30;
	private Date minDate;
	private Date maxDate;
	private String creatorName;
	private boolean accessible;
	private Member currentUser;
	private boolean notRequirePassword;
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public boolean isSearchTitle() {
		return searchTitle;
	}
	public void setSearchTitle(boolean searchTitle) {
		this.searchTitle = searchTitle;
	}
	public boolean isSearchDescription() {
		return searchDescription;
	}
	public void setSearchDescription(boolean searchDescription) {
		this.searchDescription = searchDescription;
	}
	public boolean isSearchTags() {
		return searchTags;
	}
	public void setSearchTags(boolean searchTags) {
		this.searchTags = searchTags;
	}
	public int getMinAge() {
		return minAge;
	}
	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}
	public int getMaxAge() {
		return maxAge;
	}	
	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}
	public Date getMinDate() {
		return minDate;
	}
	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}
	public Date getMaxDate() {
		return maxDate;
	}
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public boolean isAccessible() {
		return accessible;
	}
	public void setAccessible(boolean accessible) {
		this.accessible = accessible;
	}
	public boolean isNotRequirePassword() {
		return notRequirePassword;
	}
	public void setNotRequirePassword(boolean notRequirePassword) {
		this.notRequirePassword = notRequirePassword;
	}
	public Member getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(Member currentUser) {
		this.currentUser = currentUser;
	}
	
}
