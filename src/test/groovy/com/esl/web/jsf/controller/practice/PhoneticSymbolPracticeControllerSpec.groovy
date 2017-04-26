package com.esl.web.jsf.controller.practice

import com.esl.BaseSpec
import com.esl.ESLApplication
import com.esl.dao.IGradeDAO
import com.esl.dao.IMemberDAO
import com.esl.dao.IMemberWordDAO
import com.esl.dao.IPhoneticQuestionDAO
import com.esl.dao.repository.MemberScoreRepository
import com.esl.dao.repository.QuestionHistoryRepository
import com.esl.entity.practice.MemberScore
import com.esl.entity.practice.QuestionHistory
import com.esl.enumeration.ESLPracticeType
import com.esl.enumeration.VocabDifficulty
import com.esl.model.Member
import com.esl.model.PhoneticQuestion
import com.esl.model.practice.PhoneticSymbols
import com.esl.web.model.UserSession
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

import static org.awaitility.Awaitility.await

@SpringBootTest
@ContextConfiguration(classes=ESLApplication.class)
@ActiveProfiles("dev")
public class PhoneticSymbolPracticeControllerSpec extends BaseSpec {
    @Autowired PhoneticSymbolPracticeController controller
    @Autowired IMemberDAO memberDAO
    @Autowired IMemberWordDAO memberWordDAO
    @Autowired IPhoneticQuestionDAO phoneticQuestionDAO
    @Autowired IGradeDAO gradeDAO
    @Autowired QuestionHistoryRepository questionHistoryRepository
    @Autowired MemberScoreRepository memberScoreRepository

    Member tester
    UserSession session

    def setup()  {
        tester = memberDAO.getMemberByUserID("tester")
        session = new UserSession()
        session.setMember(tester)

        controller.userSession = session
        controller.selectedGrade = "K3"
    }

    @Test
    def "phonetic symbol practice use vocab images"() {
        when:
        controller.selectedDifficulty = VocabDifficulty.Beginner
        controller.selectedLevel = PhoneticSymbols.Level.Full
        String view = controller.start()
        String images = controller.question.picsFullPathsInString

        then:
        view == "/practice/phoneticsymbolpractice/practice"
        controller.question != null
        images.contains("data:image")
        !images.contains("http://")
        !images.contains("https://")
    }

    @Test
    def "start without select difficulty return error view"() {
        when:
        controller.selectedDifficulty = null
        String view = controller.start()

        then:
        view == "/error"
    }

    @Test
    def "start practice and submit answer will update history"() {
        when: "start practice"
        controller.selectedDifficulty = VocabDifficulty.Beginner
        controller.selectedLevel = PhoneticSymbols.Level.Medium
        controller.start()
        PhoneticQuestion firstQuestion = controller.question

        then: "submit wrong answer"
        firstQuestion != null

        when: "submit wrong answer"
        controller.answer = "abc"
        controller.submitAnswer()
        sleep(100)
        QuestionHistory history = questionHistoryRepository.findByMemberAndPracticeTypeAndWord(tester, ESLPracticeType.PhoneticSymbolPractice,firstQuestion.word).first()

        then: "updated history"
        history != null
        println "First history: $history"

        when: "submit correct answer on same question"
        controller.question = firstQuestion
        controller.answer = firstQuestion.getIPA()
        controller.submitAnswer()
        sleep(100)
        QuestionHistory history2 = questionHistoryRepository.findByMemberAndPracticeTypeAndWord(tester, ESLPracticeType.PhoneticSymbolPractice,firstQuestion.word).first()
        MemberScore allTimesScore = memberScoreRepository.findByMemberAndScoreYearMonth(tester, MemberScore.allTimesMonth()).get()
        MemberScore latestScore = memberScoreRepository.findByMemberAndScoreYearMonth(tester, MemberScore.thisMonth()).get()

        then: "updated history"
        history2.totalAttempt == history.totalAttempt + 1
        history2.totalCorrect == history.totalCorrect + 1
        allTimesScore.score > 0
        latestScore.score > 0
    }

    @Test
    def "MemberScore update correct when answer correctly"(VocabDifficulty difficulty, PhoneticSymbols.Level level, int expectedScore) {
        println "Test with difficulty $difficulty and Level $level"

        when: "start practice and answer one question"
        MemberScore allTimesScore = memberScoreRepository.findByMemberAndScoreYearMonth(tester, MemberScore.allTimesMonth()).get()
        MemberScore latestScore = memberScoreRepository.findByMemberAndScoreYearMonth(tester, MemberScore.thisMonth()).get()
        controller.selectedDifficulty = difficulty
        controller.selectedLevel = level
        controller.start()
        controller.question = controller.question
        controller.answer = controller.question.getIPA()
        controller.submitAnswer()
        await().until { memberScoreRepository.findByMemberAndScoreYearMonth(tester, MemberScore.thisMonth()).get().lastUpdatedDate > latestScore.lastUpdatedDate }
        MemberScore allTimesScore2 = memberScoreRepository.findByMemberAndScoreYearMonth(tester, MemberScore.allTimesMonth()).get()
        MemberScore latestScore2 = memberScoreRepository.findByMemberAndScoreYearMonth(tester, MemberScore.thisMonth()).get()

        then: "updated history"
        allTimesScore2.score == allTimesScore.score + expectedScore
        latestScore2.score == latestScore.score + expectedScore

        where:
        difficulty                  | level                         | expectedScore
        VocabDifficulty.Beginner    | PhoneticSymbols.Level.Rookie  | 1
        VocabDifficulty.Easy        | PhoneticSymbols.Level.Rookie  | 2
        VocabDifficulty.Normal      | PhoneticSymbols.Level.Rookie  | 2
        VocabDifficulty.Hard        | PhoneticSymbols.Level.Rookie  | 3
        VocabDifficulty.VeryHard    | PhoneticSymbols.Level.Rookie  | 4
        VocabDifficulty.Beginner    | PhoneticSymbols.Level.Low     | 3
        VocabDifficulty.Easy        | PhoneticSymbols.Level.Low     | 3
        VocabDifficulty.Normal      | PhoneticSymbols.Level.Low     | 4
        VocabDifficulty.Hard        | PhoneticSymbols.Level.Low     | 5
        VocabDifficulty.VeryHard    | PhoneticSymbols.Level.Low     | 6
        VocabDifficulty.Beginner    | PhoneticSymbols.Level.Medium  | 5
        VocabDifficulty.Easy        | PhoneticSymbols.Level.Medium  | 5
        VocabDifficulty.Normal      | PhoneticSymbols.Level.Medium  | 6
        VocabDifficulty.Hard        | PhoneticSymbols.Level.Medium  | 7
        VocabDifficulty.VeryHard    | PhoneticSymbols.Level.Medium  | 8
        VocabDifficulty.Beginner    | PhoneticSymbols.Level.Full    | 7
        VocabDifficulty.Easy        | PhoneticSymbols.Level.Full    | 8
        VocabDifficulty.Normal      | PhoneticSymbols.Level.Full    | 8
        VocabDifficulty.Hard        | PhoneticSymbols.Level.Full    | 9
        VocabDifficulty.VeryHard    | PhoneticSymbols.Level.Full    | 10
    }

    @Test
    def "question of #secondDifficulty must be a longer word then #firstDifficulty"(VocabDifficulty firstDifficulty, VocabDifficulty secondDifficulty) {
        when: "start practice with different difficulty"
        controller.selectedDifficulty = firstDifficulty
        controller.selectedLevel = PhoneticSymbols.Level.Medium
        controller.start()
        PhoneticQuestion firstQuestion = controller.question
        controller.selectedDifficulty = secondDifficulty
        controller.selectedLevel = PhoneticSymbols.Level.Medium
        controller.start()
        PhoneticQuestion secondQuestion = controller.question

        then: "second question lenght > first question length"
        secondQuestion.word.length() > firstQuestion.word.length()

        where:
        firstDifficulty             | secondDifficulty
        VocabDifficulty.Beginner    | VocabDifficulty.Easy
        VocabDifficulty.Easy        | VocabDifficulty.Normal
        VocabDifficulty.Normal      | VocabDifficulty.Hard
        VocabDifficulty.Hard        | VocabDifficulty.VeryHard
    }
}