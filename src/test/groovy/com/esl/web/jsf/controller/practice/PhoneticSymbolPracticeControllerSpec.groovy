package com.esl.web.jsf.controller.practice

import com.esl.BaseSpec
import com.esl.ESLApplication
import com.esl.dao.IGradeDAO
import com.esl.dao.IMemberWordDAO
import com.esl.dao.IPhoneticQuestionDAO
import com.esl.dao.repository.MemberScoreRepository
import com.esl.dao.repository.QuestionHistoryRepository
import com.esl.enumeration.VocabDifficulty
import com.esl.model.Member
import com.esl.model.PhoneticQuestion
import com.esl.model.practice.PhoneticSymbols
import com.esl.web.model.UserSession
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import reactor.bus.EventBus
import spock.mock.DetachedMockFactory

@SpringBootTest
@ContextConfiguration(classes=ESLApplication.class)
@ActiveProfiles("dev")
public class PhoneticSymbolPracticeControllerSpec extends BaseSpec {
    @Autowired PhoneticSymbolPracticeController controller
    @Autowired IMemberWordDAO memberWordDAO
    @Autowired IPhoneticQuestionDAO phoneticQuestionDAO
    @Autowired IGradeDAO gradeDAO
    @Autowired QuestionHistoryRepository questionHistoryRepository
    @Autowired MemberScoreRepository memberScoreRepository
    @Autowired EventBus eventBus

    @TestConfiguration
    static class MockConfig {
        def detachedMockFactory = new DetachedMockFactory()

        @Bean
        EventBus eventBus() {
            return detachedMockFactory.Mock(EventBus)
        }
    }

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
        controller.question = firstQuestion
        controller.answer = firstQuestion.getIPA()
        controller.submitAnswer()

        then: "updated history"
        2 * eventBus.notify(*_)
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