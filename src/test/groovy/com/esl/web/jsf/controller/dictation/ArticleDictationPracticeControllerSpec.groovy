package com.esl.web.jsf.controller.dictation

import com.esl.BaseSpec
import com.esl.ESLApplication
import com.esl.entity.dictation.Dictation
import com.esl.entity.dictation.SentenceHistory
import com.esl.service.JSFService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll
import spock.mock.DetachedMockFactory

@ContextConfiguration(classes=ESLApplication.class)
@SpringBootTest
class ArticleDictationPracticeControllerSpec extends BaseSpec {
    @Autowired ArticleDictationPracticeController controller
    @Autowired JSFService jsfService

    @TestConfiguration
    static class MockConfig {
        def detachedMockFactory = new DetachedMockFactory()

        @Bean JSFService jsfService() { return detachedMockFactory.Mock(JSFService) }
    }

    def "start with normal article will return practice page"() {
        def article = '''
            Lorem ipsum dolor sit amet,
            consectetur adipiscing elit,
            sed do eiusmod tempor incididunt
            ut labore et dolore magna aliqua.
            '''

        when: "start a dictation practice"
        Dictation dic = new Dictation()
        dic.setArticle(article)
        String page = controller.start(dic)

        then: "the sentences are separated by newline"
        page == "/practice/selfdictation/articlepractice"
        controller.currentSentence == 0
        controller.dictation.sentences.size() == 4
    }

    @Unroll
    def "check answer: input=#input"(String input, List<Boolean> isCorrect, int correctPercentage) {
        def article = '''1.  I hate to complain, but this hamburger tastes bad.            
            2.  Were you following the directions when you made it?           
            3.  Because you had a coupon for free snails doesn't mean you should put the creatures in my food!'''

        when: "start a dictation practice and answer the question"
        Dictation dic = new Dictation()
        dic.setArticle(article)
        controller.start(dic)
        controller.answer = input
        controller.submitAnswer()
        SentenceHistory history = controller.history[0]

        then: "updated current sentence and add a question history"
        controller.currentSentence == 1
        controller.answer == ""
        history.answer == input
        history.question == "I hate to complain, but this hamburger tastes"
        history.questionSegments.size() == history.isCorrect.size()
        history.isCorrect == isCorrect
        controller.correctPercentage == correctPercentage

        where:
        input | isCorrect | correctPercentage
        "i hate to complain but this hamburger tastes bad" | [true, true, true, true, true, true, true, true] | 100
        "i hate to complain but these hamburger tastes bad" | [true, true, true, true, true, false, true, true] | 87
        "1. i hate to complain but these hamburger tastes" | [true, true, true, true, true, false, true, true] | 87
        "" | [false, false, false, false, false, false, false, false] | 0
        "abcd" | [false, false, false, false, false, false, false, false] | 0
    }

    @Unroll
    def "check answer (another practice): input=#input"(String input, List<Boolean> isCorrect) {
        def article = '''Listen to the first file - Just Listen. I will speak, quite quickly, in a natural voice.'''

        when: "start a dictation practice and answer the question"
        Dictation dic = new Dictation()
        dic.setArticle(article)
        controller.start(dic)
        controller.answer = input
        controller.submitAnswer()
        SentenceHistory history = controller.history[0]

        then: "updated current sentence and add a question history"
        controller.dictation.sentences.size() == 3
        history.answer == input
        history.questionSegments.size() == history.isCorrect.size()

        when: "submit answer 2 more times"
        controller.submitAnswer()
        controller.submitAnswer()

        then: "redirect to result page"
        1 * jsfService.redirectToJSF(*_) >> { args ->
            assert args[0] == "/practice/selfdictation/articlepracticeresult"
        }

        where:
        input | isCorrect
        "Listen to the file" | [true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false]
    }

}
