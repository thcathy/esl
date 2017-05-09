package com.esl.web.jsf.controller.practice

import com.esl.BaseSpec
import com.esl.ESLApplication
import com.esl.dao.IGradeDAO
import com.esl.dao.IMemberWordDAO
import com.esl.dao.IPhoneticQuestionDAO
import com.esl.entity.event.UpdatePracticeHistoryEvent
import com.esl.enumeration.ESLPracticeType
import com.esl.enumeration.VocabDifficulty
import com.esl.model.Member
import com.esl.service.JSFService
import com.esl.web.model.UserSession
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import reactor.bus.Event
import reactor.bus.EventBus
import spock.mock.DetachedMockFactory

@ContextConfiguration(classes=ESLApplication.class)
@SpringBootTest
public class PhoneticPracticeG2ControllerSpec extends BaseSpec {
    @Autowired
    @Qualifier("phoneticPracticeG2Controller")
    PhoneticPracticeG2Controller controller
    @Autowired IMemberWordDAO memberWordDAO
    @Autowired IPhoneticQuestionDAO phoneticQuestionDAO
    @Autowired IGradeDAO gradeDAO
    @Autowired EventBus eventBus
    @Autowired JSFService jsfService

    Member tester
    UserSession session

    @TestConfiguration
    static class MockConfig {
        def detachedMockFactory = new DetachedMockFactory()

        @Bean EventBus eventBus() { return detachedMockFactory.Mock(EventBus) }
        @Bean JSFService jsfService() { return detachedMockFactory.Mock(JSFService) }
    }

    def setup()  {
        tester = memberDAO.getMemberByUserID("tester")
        session = new UserSession()
        session.setMember(tester)

        controller.userSession = session

        def phoneticPracticeController = Mock(PhoneticPracticeController)
        phoneticPracticeController.selectedDifficulty >> VocabDifficulty.Normal
        controller.phoneticPracticeController = phoneticPracticeController
    }

    @Test
    def "phonetic practice use vocab images"() {
        when:
        controller.start()
        String images = controller.question.picsFullPathsInString

        then:
        assert images.contains("data:image")
        assert !images.contains("http://")
        assert !images.contains("https://")
    }

    @Test
    def "submit update history event when submit answer"() {
        when: "submit wrong answer"
        controller.start()
        controller.submitAnswer()

        then: "update event without marks"
        1 * eventBus.notify(*_) >> { arguments ->
            assert arguments[0] == "addHistory"
            final Event<UpdatePracticeHistoryEvent> event = arguments[1]
            assert event.data.isCorrect == false
            assert event.data.score == 0
            assert event.data.type == ESLPracticeType.PhoneticPractice
        }

        when: "submit correct answer"
        controller.answer = controller.question.word
        controller.submitAnswer()

        then: "update event with marks"
        1 * eventBus.notify(*_) >> { arguments ->
            final Event<UpdatePracticeHistoryEvent> event = arguments[1]
            assert event.data.isCorrect == true
            assert event.data.score == 4
        }

        when: "submit correct and end"
        controller.answer = controller.question.word
        controller.submitAndEnd()

        then: "update event with marks"
        1 * jsfService.redirectToJSF(_)
        1 * eventBus.notify(*_) >> { arguments ->
            final Event<UpdatePracticeHistoryEvent> event = arguments[1]
            assert event.data.isCorrect == true
            assert event.data.score == 4
        }
    }
}