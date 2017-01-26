package com.esl.service.practice;

import com.esl.dao.dictation.*;
import com.esl.entity.dictation.*;
import com.esl.entity.dictation.Dictation.AgeGroup;
import com.esl.exception.IllegalParameterException;
import com.esl.model.Member;
import com.esl.model.PhoneticPractice;
import com.esl.model.PhoneticQuestion;
import com.esl.util.ValidationUtil;
import com.esl.util.practice.PhoneticQuestionUtil;
import com.esl.util.practice.PhoneticQuestionUtil.FindIPAAndPronoun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.File;
import java.util.*;

@Transactional
@Service("selfDictationService")
public class SelfDictationService implements ISelfDictationService {
	private static Logger logger = LoggerFactory.getLogger("ESL");

	@Value("${SelfDictationService.MaxQuestions}")
	private int maxQuestions = 1;

	@Resource private IDictationDAO dictationDAO;
	@Resource private IVocabDAO vocabDAO;
	@Resource private IMemberDictationHistoryDAO memberDictationHistoryDAO;
	@Resource private IVocabHistoryDAO vocabHistoryDAO;
	@Resource private IDictationHistoryDAO dictationHistoryDAO;

	// ============== Constructor ================//
	public SelfDictationService() {}

	// ============== Functions ================//

	// Retrieve vocabularies and return practice for self dictation
	@Transactional(readOnly=true)
	public PhoneticPractice generatePractice(Member member, List<String> inputVocabularies, ServletContext context) {
		if (inputVocabularies == null || inputVocabularies.size() < 1) {
			logger.info("generatePractice: inputVocabularies: No input");
			return null;
		}

		List<PhoneticQuestion> questions = new ArrayList<PhoneticQuestion>();
		List<Thread> threads = new ArrayList<Thread>();
		PhoneticQuestionUtil pqUtil = new PhoneticQuestionUtil();

		// Retrieve vocab and generate questions
		for (String vocab : inputVocabularies) {
			logger.info("generatePractice: word: " + vocab);

			if (ValidationUtil.isValidWord(vocab))
			{
				PhoneticQuestion question = new PhoneticQuestion();
				question.setWord(vocab.trim());

				// Create Thread to get IPA or Generate pronounce
				FindIPAAndPronoun finder = pqUtil.new FindIPAAndPronoun(questions, question, context.getRealPath("/"), context.getContextPath());
				Thread newThread = new Thread(finder);
				logger.info("generatePractice: a new Thread created: Thread ID" + newThread.getId());
				newThread.start();
				threads.add(newThread);
			}
		}

		while (threads.size() > 0) {
			logger.info("generatePractice: waiting threads: threads.size:" + threads.size() + ", questions.size:" + questions.size());
			try {
				synchronized (this) {
					for (int i = 0; i < threads.size(); i++) {
						logger.info("generatePractice: Check a thread is completed: Thread ID:" + threads.get(i).getId() + " isAlive: " + threads.get(i).isAlive());
						if (!threads.get(i).isAlive()) {
							logger.info("generatePractice: a thread completed: Thread ID:" + threads.get(i).getId());
							threads.remove(i);
						}
					}
					Thread.sleep(500);	// 0.5 sec
				}
			} catch (InterruptedException e) {
				logger.warn("generatePractice: " + e);
			}
		}

		if (questions.size() < 1) {
			logger.info("generatePractice: no question");
			return null;
		}

		// Generate practice
		Collections.shuffle(questions);
		PhoneticPractice practice = new PhoneticPractice();
		practice.setMember(member);
		practice.setQuestions(questions);
		practice.setTotalQuestions(questions.size());
		logger.info("generatePractice: final question.size:" + questions.size());
		return practice;
	}

	public PhoneticPractice generatePractice(List<Vocab> vocabs, ServletContext context) {
		final String logPrefix = "generatePractice: ";
		logger.info(logPrefix + "START");
		if (vocabs == null || context == null) throw new IllegalParameterException(new String[]{"vocabs","context"}, new Object[]{vocabs, context});

		List<PhoneticQuestion> questions = new ArrayList<PhoneticQuestion>();
		List<Thread> threads = new ArrayList<Thread>();
		PhoneticQuestionUtil pqUtil = new PhoneticQuestionUtil();

		// Retrieve vocab and generate questions
		for (Vocab vocab : vocabs) {
			logger.info(logPrefix + "vocab [" + vocab.getWord() + "]");

			PhoneticQuestion question = new PhoneticQuestion();
			question.setWord(vocab.getWord().trim());

			// Create Thread to get IPA or Generate pronounce
			FindIPAAndPronoun finder = pqUtil.new FindIPAAndPronoun(questions, question, context.getRealPath("/"), context.getContextPath());
			Thread newThread = new Thread(finder);
			logger.info("generatePractice: a new Thread created: Thread ID" + newThread.getId());
			newThread.start();
			threads.add(newThread);
		}

		while (threads.size() > 0) {
			logger.info(logPrefix + "waiting threads: threads.size:" + threads.size() + ", questions.size:" + questions.size());
			try {
				synchronized (this) {
					for (int i = 0; i < threads.size(); i++) {
						logger.info(logPrefix + "Check a thread is completed: Thread ID:" + threads.get(i).getId() + " isAlive: " + threads.get(i).isAlive());
						if (!threads.get(i).isAlive()) {
							logger.info(logPrefix + "a thread completed: Thread ID:" + threads.get(i).getId());
							threads.remove(i);
						}
					}
					Thread.sleep(500);	// 0.5 sec
				}
			} catch (InterruptedException e) {
				logger.warn(logPrefix + e);
			}
		}

		if (questions.size() < 1) {
			logger.info(logPrefix + "no question");
			return null;
		}

		// Generate practice
		Collections.shuffle(questions);
		PhoneticPractice practice = new PhoneticPractice();
		practice.setQuestions(questions);
		practice.setTotalQuestions(questions.size());
		logger.info(logPrefix + "final question.size:" + questions.size());
		return practice;
	}

	public void completedPractice(List<PhoneticQuestion> questions, ServletContext context) {
		if (questions == null) {
			logger.info("completedPractice: No questions");
			return;
		}

		Thread newThread = new Thread(this.new DeletedGeneratedQuestions(questions, context));
		logger.info("completedPractice: Start DeletedGeneratedQuestions Thread: Thread ID: " + newThread.getId());
		newThread.start();

		return;
	}

	public MemberDictationHistory updateMemberDictationHistory(Dictation dictation, Member member, PhoneticPractice practice) {
		final String logPrefix = "updateDictationHistory: ";
		logger.info(logPrefix + "START");
		if (dictation == null || practice == null) throw new IllegalParameterException(new String[]{"dictation","practice"}, new Object[]{dictation, practice});

		// update dictation
		logger.info(logPrefix + "update dictation [" + dictation.getId() + "]");
//		dictationDAO.refresh(dictation);
		dictation.setTotalAttempt(dictation.getTotalAttempt() + 1);
		dictation.setLastPracticeDate(new Date());
		dictationDAO.merge(dictation);
		logger.info(logPrefix + "updated value total attempt[" + dictation.getTotalAttempt() + "], last practice date[" + dictation.getLastPracticeDate() + "]");

		// gen word-vocab lookup map
		Map<String, Vocab> wordVocabMap = new HashMap<String, Vocab>();
		for (Vocab v : dictation.getVocabs()) {	wordVocabMap.put(v.getWord(), v);}

		for (int i=0; i < practice.getTotalQuestions(); i++) {
			Vocab v = wordVocabMap.get(practice.getQuestions().get(i).getWord());
//			vocabDAO.refresh(v);
			logger.debug(logPrefix + v);

			if ((Boolean)practice.getCorrects().get(i)) {
				v.setTotalCorrect(v.getTotalCorrect() + 1);
			} else {
				v.setTotalWrong(v.getTotalWrong() + 1);
			}
			logger.debug(logPrefix + "updated vocab value correct[" + v.getTotalCorrect() + "], wrong[" + v.getTotalWrong() +"]");
			vocabDAO.merge(v);
		}


		// return if no member
		if (member == null) {
			logger.info(logPrefix + "no member, ENDED");
			return null;
		}

		// update member dictation history
		MemberDictationHistory mdh = memberDictationHistoryDAO.loadByDictationMember(member, dictation);
		if (mdh == null) mdh = createMemberDictationHistory(member, dictation);

		mdh.setLastMark(practice.getMark());
		mdh.setLastFullMark(practice.getMaxQuestions());
		mdh.setLastPracticeDate(new Date());
		mdh.setTotalAttempt(mdh.getTotalAttempt() + 1);
		logger.debug(logPrefix + "Updated member dictation history: mark[" + mdh.getLastMark() + "] full mark[" + mdh.getLastFullMark() + "] attempt[" + mdh.getTotalAttempt() + "]");

		// update vocab history
		// gen word-vocabHistory lookup map
		Map<String, VocabHistory> wordVocabHistoryMap = new HashMap<String, VocabHistory>();
		for (VocabHistory vh : mdh.getVocabHistories()) {wordVocabHistoryMap.put(vh.getVocab().getWord(), vh);}

		for (int i=0; i < practice.getTotalQuestions(); i++) {
			VocabHistory vh = wordVocabHistoryMap.get(practice.getQuestions().get(i).getWord());
			if (vh == null) {
				vh = new VocabHistory();
				mdh.addVocabHistory(vh);
				vh.setVocab(wordVocabMap.get(practice.getQuestions().get(i).getWord()));
			}
			logger.debug(logPrefix + vh);

			if ((Boolean)practice.getCorrects().get(i)) {
				vh.setTotalCorrect(vh.getTotalCorrect() + 1);
			} else {
				vh.setTotalWrong(vh.getTotalWrong() + 1);
			}
			logger.debug(logPrefix + "updated vocab history correct[" + vh.getTotalCorrect() + "], wrong[" + vh.getTotalWrong() +"]");
			vocabHistoryDAO.persist(vh);
		}
		memberDictationHistoryDAO.persist(mdh);
		return mdh;
	}

	public DictationHistory createDictationHistory(Dictation dictation, Member member, PhoneticPractice practice) {
		final String logPrefix = "createDictationHistory: ";
		logger.info(logPrefix + "START");
		if (dictation == null || practice == null) throw new IllegalParameterException(new String[]{"dictation","practice"}, new Object[]{dictation, practice});

		// create a new dictation history
		DictationHistory history = new DictationHistory();
		history.setDictation(dictation);
		history.setMark(practice.getMark());
		if (member != null) {
			logger.info(logPrefix + "history have member");
			history.setPracticer(member);
			history.setPracticerName(member.getName().toString());
			history.setPracticerSchool(member.getSchool());
			history.setPracticerAgeGroup(AgeGroup.getAgeGroup(member.getAge()));
		} else {
			logger.info(logPrefix + "history without member");
		}
		dictationDAO.attachSession(dictation);
		dictationHistoryDAO.persist(history);
		return history;
	}

	// ============== Supporting Functions ================//
	public class DeletedGeneratedQuestions implements Runnable {
		List<PhoneticQuestion> questions;
		ServletContext context;

		public DeletedGeneratedQuestions(List<PhoneticQuestion> questions, ServletContext context) {
			this.questions = questions;
			this.context = context;
		}

		public void run() {
			logger.info("DeletedGeneratedQuestions.run: START, questions.size: " + questions.size());
			for (PhoneticQuestion question : questions) {
				if (question.getIPA() == null || question.getIPA().equals("")) {
					String filePath = context.getRealPath(".." + question.getPronouncedLink());
					logger.info("DeletedGeneratedQuestions.run: filePath: " + filePath);
					try {
						File f = new File(filePath);
						if (f.exists()) f.delete();
						File f2 = new File(filePath.substring(0, filePath.length() - 4) + ".wav");
						if (f2.exists()) f2.delete();
					} catch (Throwable e) {
						logger.warn("DeletedGeneratedQuestions.run: " + e);
					}
				}
			}
			logger.info("DeletedGeneratedQuestions.run: END");
		}
	}

	private MemberDictationHistory createMemberDictationHistory(Member member, Dictation dictation) {
		MemberDictationHistory mdh = new MemberDictationHistory();
		mdh.setDictation(dictation);
		mdh.setOwner(member);
		return mdh;
	}

	// ============== Setter / Getter ================//
	public int getMaxQuestions() {return maxQuestions;}
	public void setMaxQuestions(int maxQuestions) {	this.maxQuestions = maxQuestions;}

}
