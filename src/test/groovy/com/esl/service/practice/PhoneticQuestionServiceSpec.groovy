package com.esl.service.practice

import com.esl.ESLApplication
import com.esl.dao.IPhoneticQuestionDAO
import com.esl.model.PhoneticQuestion
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@SpringBootTest
@ContextConfiguration(classes=ESLApplication.class)
class PhoneticQuestionServiceSpec extends Specification {
    @Autowired
    PhoneticQuestionService service

    @Autowired
    IPhoneticQuestionDAO questionDAO

    @Value('${NAImage.data}')
    String NAImage

    def "Get image for a vocab missing image, will enrich an image for that"() {
        when: "Enrich a word without image"
        PhoneticQuestion question = questionDAO.getPhoneticQuestionByWord("a")
        service.enrichVocabImage([question])

        then: "Use NA image"
        question.picsFullPathsInString == NAImage
        println "pic: $question.picsFullPathsInString"
    }

}