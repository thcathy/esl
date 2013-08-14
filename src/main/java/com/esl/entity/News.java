package com.esl.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.esl.enumeration.ESLSupportedLocale;

@Entity
@Table(name = "news")
public class News implements Serializable {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id = null;

	@Column(name = "TITLE")
	private String title;

	@Column(name = "HTML_URL")
	private String htmlURL;

	@Column(name = "SHORT_DESCRIPTION")
	private String shortDescription;

	@Column(name = "TYPE")
	private String type;

	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "DEADLINE")
	private Date deadline;

	@Column(name = "LOCALE")
	@Enumerated(EnumType.STRING)
	private ESLSupportedLocale locale;

	@Column(name = "SHOW_NEW_IMAGE")
	private boolean showNewImage;

	@Transient
	private List<String> htmlContent;

	// ********************** Constructors ********************** //
	public News() {}

	// ********************** Getter Methods ********************** //
	public String getFormattedCreatedDate() {
		if (createdDate == null) return "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(createdDate);
	}

	// ********************** Accessor Methods ********************** //
	public Date getCreatedDate() {	return createdDate;}
	public void setCreatedDate(Date createdDate) {this.createdDate = createdDate;}

	public Date getDeadline() {return deadline;}
	public void setDeadline(Date deadline) {this.deadline = deadline;}

	public String getHtmlURL() {return htmlURL;}
	public void setHtmlURL(String htmlURL) {this.htmlURL = htmlURL;}

	public Long getId() {return id;}
	public void setId(Long id) {this.id = id;}

	public ESLSupportedLocale getLocale() {	return locale;}
	public void setLocale(ESLSupportedLocale locale) {this.locale = locale;}

	public String getShortDescription() {return shortDescription;}
	public void setShortDescription(String shortDescription) {this.shortDescription = shortDescription;	}

	public String getTitle() {return title;}
	public void setTitle(String title) {this.title = title;}

	public String getType() {return type;}
	public void setType(String type) {this.type = type;}

	public List<String> getHtmlContent() {return htmlContent;}
	public void setHtmlContent(List<String> htmlContent) {this.htmlContent = htmlContent;}

	public boolean isShowNewImage() {return showNewImage;}
	public void setShowNewImage(boolean showNewImage) {	this.showNewImage = showNewImage;}

	// ********************** Common Methods ********************** //
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof News)) return false;

		final News n = (News) o;
		return this.id.equals(n.getId());
	}

	@Override
	public int hashCode()
	{
		return id==null ? System.identityHashCode(this) : id.hashCode();
	}

	@Override
	public String toString() {
		return  "News (" + getId() + "), " +
				"title[" + getTitle() + "] " +
				"HTML URL[" + getHtmlURL() + "] " +
				"Short Description[" + getShortDescription()  + "] " +
				"Type[" + getType() + "] " +
				"Locale[" + getLocale() + "] " +
				"Created Date[" + getCreatedDate() + "] " +
				"Deadline[" + getDeadline() + "] ";
	}


}
