package com.esl.service.practice

import com.esl.ESLApplication
import com.esl.entity.dictation.Vocab
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@SpringBootTest
@ContextConfiguration(classes=ESLApplication.class)
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

    def "When generate practice for saved dictation, get image and dictionary from web if vocab is not found from DB"() {
        when:
        def practice = service.generatePractice([new Vocab("xxxyyyzzz"), new Vocab("jakarta")])
        def xxxyyyzzz = practice.questions.find { it.word == "xxxyyyzzz" }
        def jakarta = practice.questions.find { it.word == "jakarta" }

        then:
        assert practice.questions.size() == 2
        assert xxxyyyzzz.isIPAUnavailable() == true
        assert xxxyyyzzz.getIPA() == null
        assert xxxyyyzzz.getPronouncedLink() == null
        assert jakarta.isIPAUnavailable() == false
        assert jakarta.getIPA() == "dʒəˈkɑːtə"
        assert jakarta.getPronouncedLink() == "http://api.pearson.com/v2/dictionaries/assets/ldoce/gb_pron/p028-000006623.mp3"
        assert jakarta.getPicsFullPaths().every {it.startsWith("http")}
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

    def "When generate practice for self dictation, get image from web if vocab is not found from DB"() {
        when:
        def practice = service.generatePractice(null, ["xxxyyyzzz", "bus-stop", "jakarta"])
        def xxxyyyzzz = practice.questions.find { it.word == "xxxyyyzzz" }
        def busStop = practice.questions.find { it.word == "bus-stop" }
        def jakarta = practice.questions.find { it.word == "jakarta" }

        then:
        assert practice.questions.size() == 3
        practice.questions.each {
            assert it.picsFullPathsInString.contains("http://") || it.picsFullPathsInString.contains("https://")
        }
        assert xxxyyyzzz.isIPAUnavailable() == true
        assert xxxyyyzzz.getIPA() == null
        assert xxxyyyzzz.getPronouncedLink() == null
        assert busStop.isIPAUnavailable() == true
        assert busStop.getIPA() == null
        assert busStop.getPronouncedLink() ==null
        assert jakarta.isIPAUnavailable() == false
        assert jakarta.getIPA() == "dʒəˈkɑːtə"
        assert jakarta.getPronouncedLink() == "http://api.pearson.com/v2/dictionaries/assets/ldoce/gb_pron/p028-000006623.mp3"
        assert jakarta.getPicsFullPaths().every {it.startsWith("http")}
    }
}