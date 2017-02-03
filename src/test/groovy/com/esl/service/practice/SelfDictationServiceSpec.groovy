package com.esl.service.practice

import com.esl.entity.dictation.Vocab
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class SelfDictationServiceSpec extends Specification {
    @Autowired
    SelfDictationService service

    def "When generate practice for saved dictation, should use local vocab images if vocab is found from database"() {
        when:
        def practice = service.generatePractice([new Vocab("boy"), new Vocab("fish")])

        then:
        assert practice.questions.size() == 2
        practice.questions.each {
            assert it.picsFullPathsInString.contains("data:image")
            assert !it.picsFullPathsInString.contains("http://")
            assert !it.picsFullPathsInString.contains("https://")
        }
    }

    def "When generate practice for self dictation, should use local vocab images if vocab is found from database"() {
        when:
        def practice = service.generatePractice(null, ["boy", "fish"])

        then:
        assert practice.questions.size() == 2
        practice.questions.each {
            assert it.picsFullPathsInString.contains("data:image")
            assert !it.picsFullPathsInString.contains("http://")
            assert !it.picsFullPathsInString.contains("https://")
        }
    }

    def "When generate practice for self dictation, get image from web if vocab is not found from database"() {
        when:
        def practice = service.generatePractice(null, ["xxxyyyzzz"])

        then:
        assert practice.questions.size() == 1
        practice.questions.each {
            assert it.picsFullPathsInString.contains("http://") || it.picsFullPathsInString.contains("https://")
        }
    }
}