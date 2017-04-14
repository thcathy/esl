package com.esl.service.practice

import com.esl.enumeration.VocabDifficulty
import com.esl.model.PhoneticPractice
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class PhoneticPracticeServiceSpec extends Specification {
    @Autowired
    @Qualifier("phoneticPracticeService")
    PhoneticPracticeService service

    def "Generate practice using vocab difficutly"() {
        when: "generate a practice"
        def practice = service.generatePractice(VocabDifficulty.Beginner)

        then: "practice is valid"
        practice.questions.size() == PhoneticPractice.MAX_QUESTIONS
        practice.questions.each {
            assert it.picsFullPathsInString.contains("data:image")
            assert !it.picsFullPathsInString.contains("http://")
            assert !it.picsFullPathsInString.contains("https://")
            assert it.isIPAUnavailable() == false
            assert StringUtils.isNotBlank(it.getPronouncedLink())
        }
        practice.difficulty == VocabDifficulty.Beginner
    }

}