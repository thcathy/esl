package com.esl.entity.dictation;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is decorator of dictation that use by practice controller
 */
public class DictationPractice  {
	public final Dictation dictation;
	public final List<String> sentences;

	public Dictation getDictation() { return dictation; }
	public List<String> getSentences() {return sentences;}

	public DictationPractice(Dictation dictation) {
		this.dictation = dictation;
		this.sentences = deriveArticleToSentences(dictation);
	}

	private List<String> deriveArticleToSentences(Dictation dictation) {
		if (StringUtils.isBlank(dictation.getArticle())) return Collections.EMPTY_LIST;

		return Arrays.stream(dictation.getArticle().split("\\r?\\n"))
				.map(s -> s.replaceAll("\t",""))
				.map(String::trim)
				.flatMap(this::splitLongLineByFullstop)
				.map(String::trim)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toList());
	}

	private Stream<String> splitLongLineByFullstop(String input) {
		if (input.length() < 100)
			return Stream.of(input);
		else
			return Arrays.stream(input.split("\\. "))
							.map(s -> s.endsWith(".") ? s : s + ".");
	}
}
