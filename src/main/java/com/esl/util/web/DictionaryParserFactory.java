package com.esl.util.web;

public class DictionaryParserFactory {

	public DictionaryParser yahooParserWith(String word) {
		return YahooDictionaryParser.toParse(word);
	}
	
	public DictionaryParser cambridgeParserWith(String word) {
		return new CambridgeDictionaryParser(word);
	}
}
