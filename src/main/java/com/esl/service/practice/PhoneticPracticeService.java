package com.esl.service.practice;

import com.esl.dao.*;
import com.esl.dao.practice.IMemberPracticeScoreCardDAO;
import com.esl.entity.VocabImage;
import com.esl.entity.practice.MemberPracticeScoreCard;
import com.esl.enumeration.ESLPracticeType;
import com.esl.exception.IllegalParameterException;
import com.esl.model.*;
import com.esl.model.TopResult.OrderType;
import com.esl.util.ValidationUtil;
import com.esl.util.practice.PhoneticQuestionUtil;
import com.esl.util.practice.PhoneticQuestionUtil.FindIPAAndPronoun;
import com.esl.web.model.practice.VocabPracticeSummary;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service("phoneticPracticeService")
@Transactional
public class PhoneticPracticeService implements IPhoneticPracticeService {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger("ESL");

	@Resource protected IMemberDAO memberDAO = null;
	@Resource protected IGradeDAO gradeDAO = null;
	@Resource protected IPhoneticQuestionDAO phoneticQuestionDAO = null;
	@Resource protected IPhoneticPracticeHistoryDAO phoneticPracticeHistoryDAO = null;
	@Resource protected IPhoneticPracticeQuestionHistoryDAO phoneticPracticeQuestionHistoryDAO = null;
	@Resource protected IPracticeResultDAO practiceResultDAO = null;
	@Resource protected IMemberPracticeScoreCardDAO scoreCardDAO;
	@Resource protected IVocabImageDAO vocabImageDAO;

	public List<Grade> getUserAvailableGrades(String userId) {
		List<Grade> grades = getAllGrades();

		// return the first grades if user ID do not set or member do not find
		if (userId == null || userId.equals("")) return grades.subList(0, 1);

		Member member = memberDAO.getMemberByUserID(userId);
		if (member == null) return grades;

		// return all grades if member do not have grading
		Grade memberGrade = member.getGrade();
		if (memberGrade == null) return grades;

		int lastIndex = grades.indexOf(memberGrade);
		return grades.subList(0, lastIndex+1);
	}

	// Generate the questions for phonetic practice, null if any error
	public PhoneticPractice generatePractice(Member member, String gradeTitle) {
		Grade practiceGrade = gradeDAO.getGradeByTitle(gradeTitle);

		if (practiceGrade == null) return null;

		boolean isRandom = true;

		logger.info("generatePractice: PhoneticPractice.TOTAL_QUESTIONS[" + PhoneticPractice.MAX_QUESTIONS + "]");
		List questions = phoneticQuestionDAO.getRandomQuestionsByGrade(practiceGrade, PhoneticPractice.MAX_QUESTIONS, isRandom);
		logger.info("generatePractice: questions.size:" + questions.size());
		if (questions == null) {
			logger.warn("generatePractice: No question return by DAO");
			return null;
		}

		enrichVocabImageForQuestions(questions);

		PhoneticPractice practice = new PhoneticPractice();
		practice.setMember(member);
		practice.setGrade(practiceGrade);
		practice.setQuestions(questions);

		return practice;
	}

	private void enrichVocabImageForQuestions(List<PhoneticQuestion> questions) {
		for (PhoneticQuestion question : questions) {
			List<String> images = vocabImageDAO.listByWord(question.getWord()).stream()
					.map(VocabImage::getBase64Image)
					.collect(Collectors.toList());
			Collections.shuffle(images);

			question.setPicsFullPaths(images.toArray(new String[1]));
		}
	}

	// Check Answer
	public String checkAnswer(PhoneticPractice practice, String answer) {
		try
		{
			if (answer.length() > 0 && !ValidationUtil.isValidWord(answer)) {
				logger.info("checkAnswer: INVALID_INPUT: user input answer:" + answer);
				return INVALID_INPUT;
			}			
			boolean isCorrect = practice.getCurrentQuestionObject().wordEqual(answer);
			if (isCorrect) {
				logger.info("checkAnswer: addMark: practice.mark" + practice.getMark());
				practice.addMark(1);
			}
			practice.addAnswers(answer);
			practice.addCorrects(isCorrect);
			practice.setCurrentQuestion(practice.getCurrentQuestion()+1);

			if (isCorrect) return CORRECT_ANSWER;
			else return WRONG_ANSWER;
		}
		catch (Exception e) {
			logger.warn("checkAnswer: " + e);
			return SYSTEM_ERROR;
		}
	}

	/**
	 * Save History
	 */
	public String saveHistory(PhoneticPractice practice) {
		Member member = practice.getMember();
		Grade grade = practice.getGrade();

		if (member == null) {
			logger.info("saveHistory: No need to save History");
			logger.info("saveHistory: Total Questions:" + practice.getTotalQuestions());
			return SAVE_HISTORY_COMPLETED;
		}
		try
		{
			// Save Practice History
			/*
			PhoneticPracticeHistory history = new PhoneticPracticeHistory();
			history.setMark(practice.getMark());
			history.setFullMark(practice.getQuestions().size());
			history.setGrade(grade);
			history.setStartedTime(practice.getStartTime());
			history.setCompletedTime(new Date());

			for (int i=0; i<practice.getQuestions().size(); i++) {
				PhoneticPracticeQuestionHistory questionHistory =
					new PhoneticPracticeQuestionHistory(
							Boolean.getBoolean(practice.getCorrects().get(i).toString()),
							(PhoneticQuestion) practice.getQuestions().get(i),
							history
					);
				logger.info("saveHistory: start make question History persistent: question[" + questionHistory + "]");
				phoneticPracticeQuestionHistoryDAO.makePersistent(questionHistory);
				history.addQuestionHistories(questionHistory);
			}
			logger.info("saveHistory: start make phonetic practice History persistent: question[" + history + "]");
			phoneticPracticeHistoryDAO.makePersistent(history);
			logger.info("saveHistory: start make member persistent: member:" + member);
			memberDAO.makePersistent(member);
			member.addPhoneticPractices(history);
			 */

			// Update Practice Result With Grade
			PracticeResult resultWithGrade= practiceResultDAO.getPracticeResult(member, grade, PracticeResult.PHONETICPRACTICE);

			// Create new result if not found
			if (resultWithGrade == null) {
				logger.info("saveHistory: Create a new Practice Result with Grade: grade: " + grade);
				resultWithGrade = new PracticeResult(member, grade, PracticeResult.PHONETICPRACTICE);
			}

			resultWithGrade.addResult(practice.getMark(), practice.getQuestions().size());
			logger.info("saveHistory: start make practice result persistent: resultWithGrade:" + resultWithGrade);
			practiceResultDAO.makePersistent(resultWithGrade);

			// Update Practice Result for all Grade
			PracticeResult resultWithoutGrade= practiceResultDAO.getPracticeResult(member, null, PracticeResult.PHONETICPRACTICE);

			// Create new result if not found
			if (resultWithoutGrade == null) {
				logger.info("saveHistory: Create a new Practice Result without Grade");
				resultWithoutGrade = new PracticeResult(member, null, PracticeResult.PHONETICPRACTICE);
			}

			resultWithoutGrade.addResult(practice.getMark(), practice.getQuestions().size());
			logger.info("saveHistory: start make practice result persistent: resultWithoutGrade:" + resultWithoutGrade);
			practiceResultDAO.makePersistent(resultWithoutGrade);

		}
		catch (Exception e) {
			logger.warn("saveHistory: " + e, e);
			return SYSTEM_ERROR;
		}
		return SAVE_HISTORY_COMPLETED;
	}

	// pass null to grade for return result of all grades
	// Get the total result of a selected grade of one member
	public PracticeResult getTotalResultByGrade(Member member, Grade grade) {
		if (member == null) {
			logger.info("getTotalResultByGrade: Member is null, return null");
			return null;
		}

		int questionsDone = 0;
		int corrects = 0;
		int totalPractices = 0;

		try {
			List<PhoneticPracticeHistory> historys = member.getPhoneticPractices();
			logger.info("PhoneticPracticeService.getTotalResultByGrade: history.size()=" + historys.size());
			for (int i=0; i<historys.size(); i++) {
				PhoneticPracticeHistory history = historys.get(i);
				if (grade == null || grade.equals(history.getGrade())) {
					questionsDone += history.getFullMark();
					corrects += history.getMark();
					totalPractices++;
				}
			}
		}
		catch (Exception e) {
			logger.warn("getTotalResultByGrade: " + e);
			return null;
		}
		PracticeResult result = new PracticeResult();
		result.setGrade(grade);
		result.setFullMark(questionsDone);
		result.setMark(corrects);
		result.setTotalPractices(totalPractices);
		return result;
	}

	public String checkLevelUp(Member member, PhoneticPractice practice, PracticeResult result) {
		Grade grade = practice.getGrade();

		// Input checking
		if (member == null || grade == null || !PracticeResult.PHONETICPRACTICE.equals(result.getPracticeType())) {
			logger.info("checkLevelUp: INVALID_INPUT");
			return INVALID_INPUT;
		}


		if (grade.getLevel() != member.getGrade().getLevel()) {
			logger.info("checkLevelUp: LEVEL_RETAIN: practice grade:" + grade + ", member.grade:" + member.getGrade());
			return LEVEL_RETAIN;
		}

		// Check level up requirement
		if (result.getMark() >= grade.getPhoneticPracticeLvUpRequire())
		{
			Grade upperGrade = gradeDAO.getGradeByLevel(grade.getLevel() + 1);
			logger.info("checkLevelUp: LEVEL_UP: new grade:" + upperGrade);
			if (upperGrade != null) {
				member.setGrade(upperGrade);
				logger.info("checkLevelUp: start member persistent");
				memberDAO.makePersistent(member);
				return LEVEL_UP;
			}
		}
		logger.info("checkLevelUp: LEVEL_RETAIN: practice grade:" + grade + ", marks:" + result.getMark());
		return LEVEL_RETAIN;
	}
	/*
	public ChartValues getSummaryChartValues(List<PracticeResult> results) {
		// input checking and loggging
		if (results == null || results.size() < 1) {
			logger.info("getSummaryChartValues: Input list is null or < 1, return null");
			return null;
		}
		logger.info("getSummaryChartValues: PracticeResults.size: " + results.size());

		List<Double> totalPractices = new ArrayList<Double>();
		List<Double> marks = new ArrayList<Double>();
		List<String> seriesLabels = new ArrayList<String>();

		for (PracticeResult result : results)
		{
			logger.info("getSummaryChartValues: add Practice Result to list: result[" + result + "]");
			totalPractices.add(Double.valueOf((double)result.getTotalPractices()));
			marks.add(Double.valueOf((double)result.getMark())+0.0001);				// add 0.0001 as the pie chart do not show 0.0 value
			seriesLabels.add(result.getGrade().getDescription());
		}

		List<List<Double>> yValues = new ArrayList<List<Double>>();
		yValues.add(totalPractices);
		yValues.add(marks);

		ChartValues chartValues = new ChartValues();
		chartValues.setYValues(yValues);
		chartValues.setSeriesLabels(seriesLabels);
		return chartValues;
	}
	 */
	// Get total result of member most frequently grade of phonetic practice
	public PracticeResult getTotalResultByFrequentGrade(Member member) {
		return getTotalResultByGrade(member, phoneticPracticeHistoryDAO.getMostFrequentGradeIDbyMember(member));
	}

	/**
	 * Create the new Practice Result for new member
	 */
	public String createPracticeResult(Member member) {
		logger.info("createPracticeResult: START");

		// input checking
		if (member == null) {
			logger.warn("createPracticeResult: member is null!");
			return SYSTEM_ERROR;
		}

		createPracticeResult(member, PracticeResult.PHONETICPRACTICE);
		createPracticeResult(member, PracticeResult.PHONETICSYMBOLPRACTICE);

		return COMPLETED;
	}

	/**
	 * A single thread function for finding IPA and pronoun
	 */
	public void findIPAAndPronoun(PhoneticQuestion question) {
		logger.info("findIPAAndPronoun: START find for word [" + question.getWord() + "]");
		PhoneticQuestionUtil pqUtil = new PhoneticQuestionUtil();
		FindIPAAndPronoun service = pqUtil.new FindIPAAndPronoun(new ArrayList<PhoneticQuestion>(), question, null, null);
		service.run();
		logger.info("findIPAAndPronoun: END: question[" + question + "]");
	}

	/**
	 * Return a question -> boolean map with all false
	 */
	public Map<PhoneticQuestion, Boolean> getUnSavedMap(PhoneticPractice practice) {
		if (practice == null) return null;
		Map<PhoneticQuestion, Boolean> map = new HashMap<PhoneticQuestion, Boolean>();
		for (PhoneticQuestion question : practice.getQuestions()) {
			map.put(question, false);
		}
		logger.info("getUnSavedMap: return map size[" + map.size() + "]");
		return map;
	}

	public VocabPracticeSummary getVocabPracticeSummary(Member member) {
		final String logPrefix = "getVocabPracticeSummary: ";
		logger.debug(logPrefix + "START");
		if (member == null) return null;

		VocabPracticeSummary summary = new VocabPracticeSummary();

		PracticeResult existingPracticeResult = practiceResultDAO.getHighestGradingByMember(member, PracticeResult.PHONETICPRACTICE);
		PracticeResult favourPracticeResult = practiceResultDAO.getFavourPracticeResultByMember(member, PracticeResult.PHONETICPRACTICE);

		summary.setOverallPracticeResult(practiceResultDAO.getPracticeResult(member, null, PracticeResult.PHONETICPRACTICE));
		summary.setFavourGrade(favourPracticeResult.getGrade());
		summary.setExistingGrade(existingPracticeResult.getGrade());
		summary.setExistingScoreRank(practiceResultDAO.getPosition(OrderType.Score, existingPracticeResult));
		summary.setExistingRateRank(practiceResultDAO.getPosition(OrderType.Rate, existingPracticeResult));
		return summary;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)	// commit the score card asap
	public MemberPracticeScoreCard updateScoreCard(Member member, Date today, boolean isCorrect, PhoneticQuestion question) {
		final String logPrefix = "updateScoreCard: ";
		logger.debug("{}START", logPrefix);
		if (member == null || today==null || question==null) throw new IllegalParameterException(new String[]{"member, today, question"}, new Object[]{member, today, question});

		phoneticQuestionDAO.makePersistent(question);
		int gradeLevel = question.getGrades().get(0).getLevel();
		logger.debug("{}input userId[{}], today[{}], isCorrect[{}], gradeLevel[{}]", new Object[] {member.getUserId(), today, isCorrect, gradeLevel});

		MemberPracticeScoreCard scoreCard = scoreCardDAO.getScoreCard(member, ESLPracticeType.VocabPractice, today);
		if (isCorrect) {
			scoreCard.addScore(gradeLevel);
			scoreCardDAO.persist(scoreCard);
			logger.debug("{}Added [{}] to scoreCard [{}]", gradeLevel, scoreCard);
		}

		return scoreCard;
	}

	//******************* Supporting Class ************************//
	private List getAllGrades() {
		List grades = gradeDAO.getAll();
		logger.info("getAllGrades: Grade.size:" + grades.size());
		Collections.sort(grades);
		return grades;
	}

	protected void createPracticeResult(Member member, String practiceType) {
		logger.info("createPracticeResult: practiceType[" + practiceType + "]");

		Grade firstGrade = gradeDAO.getFirstLevelGrade();
		//	Check practice result is created
		PracticeResult resultWithGrade= practiceResultDAO.getPracticeResult(member, firstGrade, practiceType);
		PracticeResult resultWithoutGrade= practiceResultDAO.getPracticeResult(member, null, practiceType);
		if (resultWithGrade != null || resultWithoutGrade != null) {
			logger.warn("createPracticeResult: practice result is exist. member[" + member + "]");
			return;
		}

		// Create new practice result
		resultWithGrade = new PracticeResult(member, firstGrade, practiceType);
		resultWithoutGrade = new PracticeResult(member, null, practiceType);

		if (logger.isInfoEnabled()) {
			logger.info("createPracticeResult: start make practice result persistent: resultWithGrade:" + resultWithGrade);
			logger.info("createPracticeResult: start make practice result persistent: resultWithoutGrade:" + resultWithoutGrade);
		}
		practiceResultDAO.makePersistent(resultWithGrade);
		practiceResultDAO.makePersistent(resultWithoutGrade);
	}

	/*
	private List randomQuestions(Member member, List allQuestions, int total) {
		List questions = new ArrayList();
		int i = 0;
		int maxlength = allQuestions.size();
		Random generator = new Random();

		if (member == null) {
			if (maxlength < total)
				questions = allQuestions;
			else
				questions = allQuestions.subList(0, total);
			for (i=0; i<questions.size(); i++)
				PhoneticQuestionUtil.findPronouncedLink((PhoneticQuestion)questions.get(i));
			return questions;
		}

		while (i < total && i < allQuestions.size()) {
			PhoneticQuestion question = (PhoneticQuestion) allQuestions.get(generator.nextInt(maxlength));
			if (!questions.contains(question)) {
				PhoneticQuestionUtil.findPronouncedLink(question);
				questions.add(question);
				i++;
			}
		}
		return questions;
	}*/

	public static void main(String[] args)
	{
		/*
		HibernateUtil.beginTransaction();
		IPhoneticPracticeService s = (IPhoneticPracticeService) SpringUtil.getContext().getBean("phoneticPracticeService");
		IMemberDAO mdao = (MemberDAO) SpringUtil.getContext().getBean("memberDAO");
		IGradeDAO gdao = (GradeDAO) SpringUtil.getContext().getBean("gradeDAO");
		try
		{
			Member member = (Member) mdao.findAll().get(0);
			Grade grade = (Grade) gdao.getFirstLevelGrade();

		}
		catch (Exception e)
		{
			e.printStackinfo();
		}
		HibernateUtil.commitTransaction();*/
	}
}
