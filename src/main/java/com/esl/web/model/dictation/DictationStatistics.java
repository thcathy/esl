package com.esl.web.model.dictation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.esl.entity.dictation.Dictation;

/**
 * Use for member summary page
 */
@Service("dictationStatistics")
public class DictationStatistics implements Serializable {
	public enum Type {
		MostPracticed, NewCreated, MostRecommended, LatestPracticed;
	}

	private static final long serialVersionUID = -6625468559875226206L;
	private Type type;
	private List<Dictation> dictations = new ArrayList<Dictation>();

	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public List<Dictation> getDictations() {
		return dictations;
	}
	public void setDictations(List<Dictation> dictations) {
		this.dictations = dictations;
	}
}
