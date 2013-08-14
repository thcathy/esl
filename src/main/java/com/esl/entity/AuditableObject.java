package com.esl.entity;

import java.util.Date;

import javax.persistence.*;

@Embeddable
public abstract class AuditableObject {


	@Column(name = "CREATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate = new Date();

	@Column(name = "LAST_UPDATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdatedDate;



	public Date getCreatedDate() {return createdDate;}
	public void setCreatedDate(Date createdDate) {this.createdDate = createdDate;}

	public Date getLastUpdatedDate() {return lastUpdatedDate;}
	public void setLastUpdatedDate(Date lastUpdatedDate) {this.lastUpdatedDate = lastUpdatedDate;}



}
