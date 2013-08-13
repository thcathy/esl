package com.esl.util.practice;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import javax.sound.sampled.AudioFileFormat.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.esl.model.PhoneticQuestion;
import com.esl.model.practice.PhoneticSymbols;
import com.esl.util.web.CambridgeDictionaryParser;
import com.esl.util.web.DictionaryParser;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;

// Use for Command line
class StreamGobbler extends Thread
{
	InputStream is;
	String type;

	StreamGobbler(InputStream is, String type)
	{
		this.is = is;
		this.type = type;
	}

	@Override
	public void run()
	{
		try
		{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line=null;
			while ( (line = br.readLine()) != null)
				System.out.println(type + ">" + line);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}

@Service("phoneticQuestionUtil")
public class PhoneticQuestionUtil {
	private static String Provider = "Google";
	private static Logger logger = LoggerFactory.getLogger("ESL");

	private static String wavFolder = "sound";
	private static String YAHOODictionaryURL = "http://www.google.com.hk/dictionary?langpair=en|en&q=";
	private static String IPAPrefix = "<span class=\"dct-tp\">/";
	private static String IPASuffix = "/";
	private static String audioPrefix = "<param name=\"flashvars\" value=\"sound_name=";
	private static String audioSuffix = ".mp3";
	private static String ttsVoice = "kevin16";
	private static String lamePath = "WEB-INF\\bin\\lame.exe";
	private static String lameParamaters = "-h -b 16kb";
	private static int soundFileLength = 10;

	// private use only
	private static List<String> fileList1 = new ArrayList<String>();
	private static List<String> fileList2 = new ArrayList<String>();
	private static int activeList = 1;

	public PhoneticQuestionUtil() {}

	@Value("${PhoneticQuestionUtil.WavFolder}")  public void setWavFolder(String wavFolder) { this.wavFolder = wavFolder; }
	public void setYAHOODictionaryURL(String url) { this.YAHOODictionaryURL = url; }
	public void setIPAPrefix(String prefix) { this.IPAPrefix = prefix; }
	public void setIPASuffix(String suffix) { this.IPASuffix = suffix; }
	public void setAudioPrefix(String prefix) { this.audioPrefix = prefix; }
	public void setAudioSuffix(String suffix) { this.audioSuffix = suffix; }
	public void setTtsVoice(String ttsVoice) { this.ttsVoice = ttsVoice; }
	public void setLamePath(String lamePath) {this.lamePath = lamePath;}
	public void setLameParamaters(String lameParamaters) {this.lameParamaters = lameParamaters;	}
	public void setSoundFileLength(int soundFileLength) {	this.soundFileLength = soundFileLength;	}

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
				//if (question.getIPA() == null) {
				logger.info("FindIPAAndPronoun.run: Do not have IPA, Start getting IPA");
				findIPA(question);
				//}
				if (question.getIPA() == null && question.getPronouncedLink() == null) {
					logger.info("FindIPAAndPronoun.run: Do not have IPA, Start generate prononuce");
					question.setIPAUnavailable(true);
					generatePronounceLink(question, rootPath, contextPath);
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

	public void findPronouncedLink(PhoneticQuestion question) {
		int startPosition, endPosition;

		try
		{
			URL enquiryURL = new URL(YAHOODictionaryURL + question.getWord());

			/*
			// Using proxy
			InetSocketAddress addr = new InetSocketAddress("172.27.1.213",80);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);

			HttpURLConnection connection = (HttpURLConnection) enquiryURL.openConnection(proxy);
			 */
			// Without proxy
			HttpURLConnection connection = (HttpURLConnection) enquiryURL.openConnection();

			// Read stream reader
			InputStream in = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String aLine = br.readLine();
			while (aLine != null) {
				if (aLine.contains(audioPrefix)) {
					startPosition = aLine.indexOf(audioPrefix);
					endPosition = aLine.indexOf(audioSuffix, startPosition + 1) + 4;
					question.setPronouncedLink(aLine.substring(startPosition, endPosition));
					logger.info("findPronouncedLink: returned Pronounced Link: " + question.getPronouncedLink());
				}
				aLine = br.readLine();
			}
		}catch (Exception e) {
			logger.error("findPronouncedLink: " + e,e);
		}
	}

	// Generate the pronounciation by tts
	public void generatePronounceLink(PhoneticQuestion question, String root, String contextRoot) throws Exception {
		String filePath = getNewFilePath(root + wavFolder);
		try {
			generateTTSWavFile(question.getWord(), filePath);
			convertWavToMP3(root, filePath);
			question.setPronouncedLink(contextRoot + "/" + wavFolder + "/" + filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length()) + ".mp3");
			logger.info("generatePronounceLink: generated Pronounced Link: " + question.getPronouncedLink());
		} catch (Exception e) {
			throw e;
		}
	}

	private void generateTTSWavFile(String word, String filePath) throws Exception {
		try {

			VoiceManager voiceManager = VoiceManager.getInstance();
			Voice voice = voiceManager.getVoice(ttsVoice);

			if (voice == null)
				logger.warn("generateTTSWavFile: Cannot find a voice named. Please specify a different voice.");

			SingleFileAudioPlayer player = new SingleFileAudioPlayer(filePath, new Type("WAVE","wav"));
			voice.setAudioPlayer(player);
			voice.allocate();
			voice.setRate(120);
			voice.speak(word);
			player.close();
			voice.deallocate();
		} catch (Exception e) {
			throw e;
		}
	}

	private void convertWavToMP3(String root, String filePath) {
		try {
			String command = root + lamePath + " " + lameParamaters + " " + filePath + ".wav " + filePath + ".mp3";
			logger.info("convertWavToMP3: command: " + command);

			String[] cmd = new String[3];
			cmd[0] = "cmd.exe" ;
			cmd[1] = "/C" ;
			cmd[2] = command;
			Process process = Runtime.getRuntime().exec(cmd);

			StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");
			StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");

			// kick them off
			errorGobbler.start();
			outputGobbler.start();

			int exitVal = process.waitFor();
			logger.info("convertWavToMP3: exitVal=[" + exitVal + "]");

			// Remove .wav first
			//File f = new File(filePath + ".wav");
			//if (f.exists()) f.delete();
		} catch (Exception e) {
			logger.error("convertWavToMP3: " + e, e);
		}
	}

	private String getNewFilePath(String folderPath) {
		String newFilePath;
		do {
			newFilePath = folderPath + "\\" + getRandomString();
		}
		while (existFilePath(newFilePath));
		addFilePath(newFilePath);
		logger.info("getNewFilePath: generated file path: " + newFilePath);
		return newFilePath;
	}

	private boolean setGoogleIPAAndPhonetic(PhoneticQuestion question) throws Exception {
		final String GoogleDictionaryURL = "http://www.google.com.hk/dictionary?langpair=en|zh-TW&hl=en&aq=f&q=";
		final String GoogleIPAPrefix = "<span class=\"dct-tp\">/";
		final String GoogleIPASuffix = "/";
		final String GoogleaudioPrefix = "<param name=\"flashvars\" value=\"sound_name=";
		final String GoogleaudioSuffix = ".mp3";

		URL enquiryURL = new URL(GoogleDictionaryURL + question.getWord().replaceAll(" ", "+"));

		HttpURLConnection connection = (HttpURLConnection) enquiryURL.openConnection();

		InputStream in = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		boolean isIPAFound = false;
		boolean isPhoneticFound = false;
		int startPosition, endPosition;

		String aLine = br.readLine();
		while (!(isIPAFound && isPhoneticFound) && aLine != null) {

			if (!isIPAFound && aLine.contains(GoogleIPAPrefix)) {
				startPosition = aLine.indexOf(GoogleIPAPrefix) + GoogleIPAPrefix.length();
				endPosition = aLine.indexOf(GoogleIPASuffix, startPosition + 1);
				String IPA = aLine.substring(startPosition, endPosition);
				IPA = IPA.replace("(r)", "");
				IPA = PhoneticSymbols.convertGoogleIPA(IPA);
				question.setIPA(PhoneticSymbols.filterIPA(IPA));
				logger.info("findIPA: returned IPA: " + question.getIPA());
				isIPAFound = true;
			}

			if (!isPhoneticFound && aLine.startsWith(GoogleaudioPrefix)) {
				startPosition = aLine.indexOf(GoogleaudioPrefix) + GoogleaudioPrefix.length();
				endPosition = aLine.indexOf(GoogleaudioSuffix, startPosition + 1) + GoogleaudioSuffix.length();
				question.setPronouncedLink(aLine.substring(startPosition, endPosition));
				logger.info("findIPA: returned Pronounced Link: " + question.getPronouncedLink());
				isPhoneticFound = true;
			}
			aLine = br.readLine();
		}

		if (isIPAFound || isPhoneticFound) {
			question.setIPAUnavailable(false);
			return true;
		}
		return false;
	}

	private boolean setYahooIPAAndPhonetic(PhoneticQuestion question) throws Exception {
		URL enquiryURL = new URL(YAHOODictionaryURL + question.getWord().replaceAll(" ", "+"));
		int startPosition, endPosition;

		/*
		// Using proxy
		InetSocketAddress addr = new InetSocketAddress("172.27.1.213",80);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);

		HttpURLConnection connection = (HttpURLConnection) enquiryURL.openConnection(proxy);
		 */
		// Without proxy
		HttpURLConnection connection = (HttpURLConnection) enquiryURL.openConnection();


		// Read stream reader
		InputStream in = connection.getInputStream();
		DataInputStream dis = new DataInputStream(in);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String aLine = dis.readUTF();
		boolean isIPAFound = false;
		boolean isPhoneticFound = false;


		if (aLine.contains(IPAPrefix)) {
			startPosition = aLine.indexOf(IPAPrefix) + IPAPrefix.length();
			endPosition = aLine.indexOf(IPASuffix, startPosition);
			String IPA = aLine.substring(startPosition, endPosition);
			/*
				IPA = IPA.replaceAll("http://hk.yimg.com/i/dic", "/ESL/images/phoneticsymbols");
				IPA = IPA.replaceAll("border=0", "border=\"0\"");
				IPA = IPA.replaceAll("align=absmiddle", "align=\"absmiddle\"");
				IPA = IPA.replaceAll(">", "/>");*/
			question.setIPA(IPA);
			logger.info("findIPA: returned IPA: " + question.getIPA());
			isIPAFound = true;
		}
		if (aLine.contains(audioPrefix)) {
			startPosition = aLine.indexOf(audioPrefix);
			endPosition = aLine.indexOf(audioSuffix, startPosition + 1) + 4;
			question.setPronouncedLink(aLine.substring(startPosition, endPosition));
			logger.info("findIPA: returned Pronounced Link: " + question.getPronouncedLink());
			isPhoneticFound = true;
		}


		if (isIPAFound || isPhoneticFound) {
			question.setIPAUnavailable(false);
			return true;
		}
		return false;
	}

	private void addFilePath(String path) {
		if (activeList == 1)
			fileList1.add(path);
		else
			fileList2.add(path);
	}

	private boolean existFilePath(String path) {
		return (fileList1.contains(path) || fileList2.contains(path));
	}

	private String getRandomString()
	{
		Random seed = new Random();
		String str = "";
		for (int i = 0; i < soundFileLength; i++)
			str += (char)(seed.nextInt(26)+65);

		return str;
	}

	// for testing propose
	public static void main(String[] args) {
		PhoneticQuestionUtil p = new PhoneticQuestionUtil();
		p.convertWavToMP3("E:\\Tomcat6SIT\\wtpwebapps\\ESL\\", "E:\\Tomcat6SIT\\wtpwebapps\\ESL\\sound\\ELQABDGCPR");
		System.out.println("aaaa");
	}



}
