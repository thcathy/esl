package com.esl.model;

import java.io.Serializable;
import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.esl.util.WebUtil;

public class PhoneticQuestion implements Serializable {
	private static Logger logger = Logger.getLogger("ESL");

	public final static String SYMBOL_GIF_PREFIX = "l.yimg.com/mq/i/dic/";
	public final static String SYMBOL_GIF_SUFFIX =  ".gif";
	public final static String PIC_FILE_FOLDER_PATH = "/ESL/images/graphic/word/";

	private Long id = null;
	private String IPA;
	private String pronouncedLink;
	private String word;
	private String picFileName;
	private List<Grade> grades = new ArrayList<Grade>();
	private Date createdDate = new Date();

	// not persist in DB
	private boolean IPAUnavailable = false;
	private String[] phonics;
	private String[] picsFullPaths;

	// ********************** Constructors ********************** //
	public PhoneticQuestion() {}

	public PhoneticQuestion(String word, String IPA, String pronouncedLink) {
		this.word = word;
		this.IPA = IPA;
		this.pronouncedLink = pronouncedLink;
	}

	public PhoneticQuestion(String word, String IPA) {
		this.word = word;
		this.IPA = IPA;
	}

	// ********************** Accessor Methods ********************** //
	public Long getId() { return id; }
	private void setId(Long id) { this.id = id; }

	public Date getCreatedDate() { return createdDate; }
	private void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

	public String getWord() { return word; }
	public void setWord(String word) { this.word = word; }

	public String getIPA() {return IPA; }
	public void setIPA(String IPA) {
		this.IPA = IPA;
		setPhonics(IPA);
	}

	public String getPronouncedLink() { return pronouncedLink; }
	public void setPronouncedLink(String pronouncedLink) { this.pronouncedLink = pronouncedLink; }

	public String getPicFileName() {return picFileName;}
	public void setPicFileName(String picFileName) {this.picFileName = picFileName;}

	public String[] getPhonics() {	return phonics;	}
	public void setPhonics(String[] phonics) {	this.phonics = phonics;	}

	public List<Grade> getGrades() { return grades; }
	public void setGrades(List<Grade> grades) { this.grades = grades; }
	public void addGrades(Grade grade) {
		if (grade == null) throw new IllegalArgumentException("Can't add a null grade.");

		grade.getPhoneticQuestions().add(this);
		this.grades.add(grade);
	}

	public String[] getPicsFullPaths() {
		if (picsFullPaths != null) return picsFullPaths;

		if (StringUtils.hasText(picFileName)) {
			picsFullPaths = new String[]{PIC_FILE_FOLDER_PATH + picFileName};
		} else {
			picsFullPaths = WebUtil.getThumbnailsFromBing(word);
		}
		return picsFullPaths;
	}
	public void setPicsFullPaths(String[] picsFullPaths) {this.picsFullPaths = picsFullPaths;}

	public void setPicsFullPathsInString(String dummy) {}
	public String getPicsFullPathsInString() {
		StringBuilder sb = new StringBuilder();
		getPicsFullPaths();
		for (String path : picsFullPaths) {
			sb.append(path + ",");
		}
		if (sb.length() > 1) sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

	public boolean isIPAUnavailable() {	return IPAUnavailable;}
	public void setIPAUnavailable(boolean IPAUnavailable) {this.IPAUnavailable = IPAUnavailable; }

	public String getSuffledWord() {
		Character[] chars = new Character[word.length()];

		for (int i = 0; i < word.length(); i++)
			chars[i] = word.charAt(i);
		List<Character> list = Arrays.asList(chars);

		int maxTrial = word.length() / 2;
		int trial = 0;
		boolean similarWord = false;

		do
		{
			Collections.shuffle(list);
			trial++;
			similarWord = false;

			// check shuffle word is too similar
			int sameChar = 0;
			for (int i=0; i < list.size(); i++) {
				if (list.get(i).equals(word.charAt(i))) {
					sameChar++;
				}
				if (sameChar > maxTrial)  {
					similarWord = true;
					break;
				}
			}
		} while (trial < maxTrial && similarWord == true);

		// reverse word if shuffle word same
		StringBuilder sb = new StringBuilder();
		if (similarWord) {
			sb.append(word);
			sb.reverse();
		} else {
			for (Character c : list) sb.append(c);
		}

		return sb.toString();
	}

	// ********************** Supporting Methods ********************//
	private void setPhonics(String IPA) {
		int startPos, endPos;
		List<String> phonicList = new ArrayList<String>();

		startPos = IPA.indexOf(SYMBOL_GIF_PREFIX);
		while (startPos >= 0) {
			endPos = IPA.indexOf(SYMBOL_GIF_SUFFIX, startPos);
			String symbolId = IPA.substring(startPos + SYMBOL_GIF_PREFIX.length(), endPos);
			phonicList.add(symbolId);
			startPos = IPA.indexOf(SYMBOL_GIF_PREFIX, endPos);
		}
		phonics = phonicList.toArray(new String[phonicList.size()]);
		logger.info("setPhonics: phonics[" + Arrays.toString(phonics) + "]");
	}

	// ********************** Common Methods ********************** //
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof PhoneticQuestion)) return false;

		final PhoneticQuestion question = (PhoneticQuestion) o;
		return this.id.equals(question.getId());
	}

	@Override
	public int hashCode()
	{
		return id==null ? System.identityHashCode(this) : id.hashCode();
	}

	@Override
	public String toString() {
		return  "Phonetic Question (" + getId() + "): " +
				"Word[" + getWord() + "]," +
				"Pic[" + getPicFileName() + "]," +
				"IPA[" + getIPA() + "]," +
				"Pronounced File Link[" + getPronouncedLink() + "]";
	}

	public static void main(String[] args)
	{
		PhoneticQuestion q = new PhoneticQuestion();
		q.setWord("apple");
	}
}
