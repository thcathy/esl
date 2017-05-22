package com.esl.web.jsf.controller.dictation

import com.esl.BaseSpec
import com.esl.ESLApplication
import com.esl.entity.dictation.SentenceHistory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

@ContextConfiguration(classes=ESLApplication.class)
@SpringBootTest
class ArticleDictationPracticeControllerSpec extends BaseSpec {
    @Autowired ArticleDictationPracticeController controller

    def "start with normal article will return practice page"() {
        def article = '''
            Lorem ipsum dolor sit amet,
            consectetur adipiscing elit,
            sed do eiusmod tempor incididunt
            ut labore et dolore magna aliqua.
            '''

        when: "start a dictation practice"
        String page = controller.start(article)

        then: "the sentences are separated by newline"
        page == "/practice/selfdictation/articlepractice"
        controller.currentSentence == 0
        controller.dictation.sentences.size() == 4
    }

    @Unroll
    def "check answer: input=#input"(String input, List<Boolean> isCorrect) {
        def article = '''1.  I hate to complain, but this hamburger tastes bad.            
            2.  Were you following the directions when you made it?           
            3.  Because you had a coupon for free snails doesn't mean you should put the creatures in my food!'''

        when: "start a dictation practice and answer the question"
        controller.start(article)
        controller.answer = input
        controller.submitAnswer()
        SentenceHistory history = controller.history[0]

        then: "updated current sentence and add a question history"
        controller.currentSentence == 1
        controller.answer == ""
        history.answer == input
        history.question == "1.  I hate to complain, but this hamburger tastes bad."
        history.questionSegments.size() == history.isCorrect.size()
        history.isCorrect == isCorrect

        where:
        input | isCorrect
        "i hate to complain but this hamburger tastes bad" | [true, true, true, true, true, true, true, true, true, true, true]
        "i hate to complain but these hamburger tastes bad" | [true, true, true, true, true, true, true, false, true, true, true]
        "1. i hate to complain but these hamburger tastes bad" | [true, true, true, true, true, true, true, false, true, true, true]
        "1. i hate to complain but these hamburger tastes" | [true, true, true, true, true, true, true, false, true, true, false]
        "" | [true, false, false, false, false, false, false, false, false, false, false]
        "abcd" | [true, true, false, false, false, false, false, false, false, false, false]
    }

    @Unroll
    def "check answer (another practice): input=#input"(String input, List<Boolean> isCorrect) {
        def article = '''Listen to the first file - Just Listen. I will speak, quite quickly, in a natural voice.
                            Listen to second file - listen and write. I will speak more slowly.
                            Listen to the first file again - Check and correct.'''

        when: "start a dictation practice and answer the question"
        controller.start(article)
        controller.answer = input
        controller.submitAnswer()
        SentenceHistory history = controller.history[0]

        then: "updated current sentence and add a question history"
        history.answer == input
        history.questionSegments.size() == history.isCorrect.size()

        when: "submit answer "

        where:
        input | isCorrect
        "Listen to the file" | [true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false]
    }

}
