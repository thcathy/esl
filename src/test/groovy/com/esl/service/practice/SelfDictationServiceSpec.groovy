package com.esl.service.practice

import com.esl.ESLApplication
import com.esl.entity.dictation.Vocab
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@SpringBootTest
@ContextConfiguration(classes=ESLApplication.class)
class SelfDictationServiceSpec extends Specification {
    @Autowired
    SelfDictationService service

    @Value('${NAImage.data}')
    public String NAImage

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
        assert jakarta.getPronouncedLink() == "http://audio.oxforddictionaries.com/en/mp3/jakarta_1_gb_1.mp3"
        assert jakarta.getPicsFullPaths().every {it.startsWith("http")}
    }


    def "When generate practice for self dictation, should use local vocab images if vocab is found from database"() {
        when:
        def practice = service.generatePractice(null, ["boy", "fish"], true)

        then:
        assert practice.questions.size() == 2
        practice.questions.each {
            assert it.picsFullPathsInString.contains("data:image")
            assert !it.picsFullPathsInString.contains("http://")
            assert !it.picsFullPathsInString.contains("https://")
        }
    }

    def "When generate practice for self dictation without image, the image is enrich to NAImage"() {
        when:
        def practice = service.generatePractice(null, ["boy", "fish", "jakarta"], false)

        then:
        assert practice.questions.size() == 3
        practice.questions.each {
            assert it.picsFullPathsInString == NAImage
        }
    }

    def "When generate practice for self dictation, get image from web if vocab is not found from DB"() {
        when:
        def practice = service.generatePractice(null, ["xxxyyyzzz", "bus-stop", "jakarta"], true)
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
        assert busStop.isIPAUnavailable() == false
        assert busStop.getIPA() == "ˈbʌs ˌstɒp"
        assert busStop.getPronouncedLink() == "http://dictionary.cambridge.org/media/english/uk_pron/u/ukc/ukcld/ukcld00151.mp3"
        assert jakarta.isIPAUnavailable() == false
        assert jakarta.getIPA() == "dʒəˈkɑːtə"
        assert jakarta.getPronouncedLink() == "http://audio.oxforddictionaries.com/en/mp3/jakarta_1_gb_1.mp3"
        assert jakarta.getPicsFullPaths().every {it.startsWith("http")}
    }
}