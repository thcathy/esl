package com.esl.util.practice;

import java.net.HttpURLConnection;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.esl.model.PhoneticQuestion;
import com.esl.util.web.CambridgeDictionaryParser;
import com.esl.util.web.DictionaryParser;
import com.esl.util.web.HttpURLConnectionBuilder;

@Service("phoneticQuestionUtil")
public class PhoneticQuestionUtil {	
	private static Logger logger = LoggerFactory.getLogger("ESL");

	public PhoneticQuestionUtil() {}

	public void findIPA(PhoneticQuestion question) {
		DictionaryParser parser = new CambridgeDictionaryParser(question.getWord());
		if (parser.parse()) {
			question.setIPA(parser.getIpa());
			question.setPronouncedLink(parser.getAudioLink());
			logger.debug("Found IPA [{}] and PronounceLink [{}]", question.getIPA(), question.getPronouncedLink());
		}
	}

	public class FindIPAAndPronoun implements Runnable {
		List<PhoneticQuestion> questions;
		PhoneticQuestion question;
		String rootPath;
		String contextPath;

		public FindIPAAndPronoun(List<PhoneticQuestion> questions,
				PhoneticQuestion question, String rootPath, String contextPath) {
			super();
			this.questions = questions;
			this.question = question;
			this.rootPath = rootPath;
			this.contextPath = contextPath;
		}

		public void run() {
			try{
				if (question.notEnriched()) {
					logger.info("FindIPAAndPronoun.run: Do not have IPA, Start getting IPA");
					findIPA(question);
				}
				if (question.notEnriched()) {
					logger.info("FindIPAAndPronoun.run: Do not have IPA, Start get text 2 speech");
					question.setIPAUnavailable(true);
					//generatePronounceLink(question, rootPath, contextPath);
					question.setPronouncedLink(getText2SpeechPronounceLink(question.getWord()));
				}
				synchronized (this) {
					logger.info("FindIPAAndPronoun.run: Add question to list: " + question);
					if (question.getPronouncedLink() != null) questions.add(question);
				}
			} catch (Exception e) {
				logger.error("FindIPAAndPronoun.run: " + e, e);
			}
		}
	}
	
	public String getText2SpeechPronounceLink(String word) {
		String text2SpeechURL = "http://www.text2speech.org/";
		String paramUrl = "speech=" + word.replace(" ", "+") + "&voice=nitech_us_rms_arctic_hts&volume_scale=5&make_audio=Convert+Text+To+Speech";
		
		Document doc = null;		
		try {						
			HttpURLConnection connection = new HttpURLConnectionBuilder().setURL(text2SpeechURL).setEncodedParams(paramUrl).createConnection();		
			doc = Jsoup.parse(connection.getInputStream(), "utf-8", connection.getURL().getPath());			
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return text2SpeechURL + doc.select("a[href$=mp3]").get(0).attr("href");
	}

}
