package com.esl.web.jsf.controller.practice

import com.esl.ESLApplication
import com.esl.dao.IMemberDAO
import com.esl.service.JSFService
import com.esl.web.jsf.controller.dictation.DictationEditController
import com.esl.web.model.UserSession
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import reactor.bus.EventBus
import spock.lang.Specification
import spock.mock.DetachedMockFactory

@ContextConfiguration(classes=ESLApplication.class)
@SpringBootTest
public class SelfDictationControllerSpec extends Specification {
    @Autowired DictationEditController dictationEditController
    @Autowired SelfDictationController selfDictationController
    @Autowired IMemberDAO memberDAO
    @Autowired UserSession session
    @Autowired JSFService jsfService

    @TestConfiguration
    static class MockConfig {
        def detachedMockFactory = new DetachedMockFactory()

        @Bean EventBus eventBus() { return detachedMockFactory.Mock(EventBus) }
        @Bean JSFService jsfService() { return detachedMockFactory.Mock(JSFService) }
    }

    @Test
    def "create dictation from self dictation without login, will redirect to login page with callback url"() {
        when:
        session.member = null
        selfDictationController.inputVocab = ["apple", "banana", "cat"]
        def result = selfDictationController.createDictation()

        then:
        1 * jsfService.redirectTo(_)
        dictationEditController.vocabs == "apple,banana,cat"
        result == null
    }

    @Test
    def "create dictation from self dictation after login, will open edit page"() {
        when:
        session.member = memberDAO.getMemberByUserID("tester")
        selfDictationController.inputVocab = ["apple", "banana", "cat"]
        def result = selfDictationController.createDictation()

        then:
        result == "/member/dictation/edit"
        dictationEditController.editDictation != null
    }
}