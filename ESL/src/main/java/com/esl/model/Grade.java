package com.esl.model;

import java.io.Serializable;
import java.util.*;

public class Grade implements Serializable, Comparable  {
	private Long id;
	private String title;
	private String longTitle;
	private String description;
	private int level;
	private int phoneticPracticeLvUpRequire;
	private int phoneticSymbolPracticeLvUpRequire;
	private List<PhoneticQuestion> phoneticQuestions = new ArrayList<PhoneticQuestion>();
	private Date createdDate = new Date();


	// ********************** Constructors ********************** //
	Grade() {}

	public Grade(String title, int level) {
		this.title = title;
		this.level = level;
	}

	// ********************** Accessor Methods ********************** //
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public Date getCreatedDate() { return createdDate; }
	private void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public String getLongTitle() {return longTitle;}
	public void setLongTitle(String longTitle) {this.longTitle = longTitle;}

	public int getLevel() { return level; }
	public void setLevel(int level) { this.level = level; }

	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;	}

	public int getPhoneticPracticeLvUpRequire() {return phoneticPracticeLvUpRequire;}
	public void setPhoneticPracticeLvUpRequire(int phoneticPracticeLvUpRequire) {this.phoneticPracticeLvUpRequire = phoneticPracticeLvUpRequire;}

	public int getPhoneticSymbolPracticeLvUpRequire() {	return phoneticSymbolPracticeLvUpRequire;}
	public void setPhoneticSymbolPracticeLvUpRequire(int phoneticSymbolPracticeLvUpRequire) {this.phoneticSymbolPracticeLvUpRequire = phoneticSymbolPracticeLvUpRequire;}

	public List<PhoneticQuestion> getPhoneticQuestions() { return phoneticQuestions; }
	public void setPhoneticQuestions(List<PhoneticQuestion> phoneticQuestions) { this.phoneticQuestions = phoneticQuestions; }
	public void addPhoneticQuestions(PhoneticQuestion question)
	{
		if (question == null) throw new IllegalArgumentException("Can't add a null question.");

		question.getGrades().add(this);
		this.phoneticQuestions.add(question);
	}

	// ********************** Common Methods ********************** //
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof Grade)) return false;

		final Grade grade = (Grade) o;
		return this.id.doubleValue() == grade.getId().doubleValue();
	}

	@Override
	public int hashCode()
	{
		return id==null ? System.identityHashCode(this) : id.hashCode();
	}

	@Override
	public String toString() {
		return  "Grade ('" + getId() + "'), " +
		"Title: '" + getTitle() + "' " +
		"Description: '" + getDescription() + "' " +
		"Level: '" + getLevel() + "' ";
	}

	public int compareTo(Object o) {
		if (o instanceof Grade) {
			return this.getLevel() - ((Grade)o).getLevel();
		}
		return 0;
	}

}
