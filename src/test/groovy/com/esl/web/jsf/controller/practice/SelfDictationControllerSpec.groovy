package com.esl.web.jsf.controller.practice

import com.esl.ESLApplication
import com.esl.dao.IMemberDAO
import com.esl.service.JSFService
import com.esl.web.jsf.controller.dictation.DictationEditController
import com.esl.web.model.UserSession
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.any

@ContextConfiguration(classes=ESLApplication.class)
@SpringBootTest
public class SelfDictationControllerSpec extends Specification {
    @Autowired public DictationEditController dictationEditController
    @Autowired public SelfDictationController selfDictationController
    @Autowired public IMemberDAO memberDAO
    @Autowired public UserSession session

    @MockBean private JSFService jsfService;

    @Test
    def "create dictation from self dictation without login, will redirect to login page with callback url"() {
        given(jsfService.redirectTo(any())).willReturn(null);

        when:
        session.member = null
        selfDictationController.inputVocab = ["apple", "banana", "cat"]
        def result = selfDictationController.createDictation()

        then:
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