package com.esl.web.jsf.controller.practice

import com.esl.dao.IMemberDAO
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(locations = "/com/esl/ESL-context.xml")
@SpringBootTest
class MyVocabPracticeControllerSpec extends BaseSpec {
    @Autowired MyVocabPracticeController myVocabPracticeController
    @Autowired IMemberDAO memberDAO

    @Test
    def "Test spring setup"() {
        when:
        myVocabPracticeController.getRandomQuestion()

        then:
        myVocabPracticeController.memberWord.word.picsFullPathsInString != null
    }
}