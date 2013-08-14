package com.esl.entity.practice;

import java.io.Serializable;

import org.springframework.data.neo4j.annotation.*;

@NodeEntity
public class PersistentGrammarPractice implements Serializable {
	private static final long serialVersionUID = 1L;

	@GraphId Long id;

	String title;

	String passage;

	int suitableMinAge;

	int suitableMaxAge;

	@Indexed Long creatorId;

	// ********************** Constructors ********************** //
	public PersistentGrammarPractice() {}

	// ********************** Accessor Methods ********************** //
	public Long getId() {return id;}
	public void setId(Long id) {this.id = id;}

	public String getTitle() {return title;}
	public void setTitle(String title) {this.title = title;}

	public String getPassage() {return passage;}
	public void setPassage(String passage) {this.passage = passage;}

	public int getSuitableMinAge() {return suitableMinAge;}
	public void setSuitableMinAge(int suitableMinAge) {this.suitableMinAge = suitableMinAge;}

	public int getSuitableMaxAge() {return suitableMaxAge;}
	public void setSuitableMaxAge(int suitableMaxAge) {this.suitableMaxAge = suitableMaxAge;}

	public Long getCreatorId() {return creatorId;}
	public void setCreatorId(Long creatorId) {this.creatorId = creatorId;}


	// ********************** Common Methods ********************** //


	// ********************** Comparator ********************** //

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PersistentGrammarPractice other = (PersistentGrammarPractice) obj;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		return true;
	}



}
