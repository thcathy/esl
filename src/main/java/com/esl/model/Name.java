package com.esl.model;

import java.io.Serializable;

public class Name implements Serializable {
	private String lastName;
	private String firstName;
	
	// ********************** Constructors ********************** //
	public Name() {}
	public Name(String lastName, String firstName) {
		this.lastName = lastName;
		this.firstName = firstName;
	}
	
	// ********************** Accessor Methods ********************** //
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }
	
	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }
	
	// ********************** Common Methods ********************** //
	public String toString() {	return getFullName(); }
	
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Name)) return false;
		final Name name = (Name) o;
		if (getFullName() != null ? !getFullName().equals(name.getFullName()) : name.getFullName().equals(" ")) return false;			
		return true;
	}
	
	public String getFullName() { return firstName + " " + lastName; }
}
