package com.esl.service.dictation;

import java.util.*;
import com.esl.model.*;
import com.esl.entity.dictation.*;
import com.esl.model.group.MemberGroup;
import com.esl.web.model.SearchDictationInputForm;
import com.esl.web.model.dictation.DictationStatistics;
import com.esl.web.model.dictation.DictationSummary;
import com.esl.exception.BusinessValidationException;

public interface IDictationStatService {
	/**
	 * Search Dictations
	 * input the whole search form fields
	 */
	public List<Dictation> searchDictation(SearchDictationInputForm inputForm, int maxResult);
	
	/**
	 * Return the dictation summary model for member summary page
	 */
	public DictationSummary getDictationSummary(Member member);
	
	/**
	 * Random get a list of dictation base on newest, last practiced, highest rating, most practiced
	 */
	public DictationStatistics randomDictationStatistics(int maxResult);
	
	/**
	 * Use for schedule job to update the static list of dictation
	 */
	public void changeStaticDictationStatistics();
}
