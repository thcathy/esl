package com.esl.service.practice

import com.esl.service.dictation.ArticleDictationService
import spock.lang.Specification
import spock.lang.Unroll


class ArticleDictationServiceSpec extends Specification {
    ArticleDictationService service = new ArticleDictationService()

    @Unroll
    def "compare sentence: input=#input"(String input, List<Boolean> isCorrect) {
        String question = '''Jane Bailey described Miss Tweddle-Taylor, 51, as a "well-loved member of staff" and "wonderful friend and colleague".'''

        expect:
        service.compare(question, input).isCorrect == isCorrect

        where:
        input | isCorrect
        "Jane Bailey described Miss Tweddle-Taylor, 51, as a well-loved member of staff and wonderful friend and colleague." | [true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true]
        "Bailey described Miss Tweddle-Taylor, 51, as a well-loved member of staff and wonderful friend and colleague." | [false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true]
        "Tweddle-Taylor member" | [false, false, false, false, true, true, false, false, false, true, false, false, false, false, false, false, false]
        "abc Tweddle-Taylor member" | [false, false, false, false, true, true, false, false, false, true, false, false, false, false, false, false, false]
        "abc bailey described as a well lived member of staff and wonderful friend and colleague" | [false, true, true, false, false, true, true, true, false, true, true, true, true, true, true, true, true]
        "Jane Bailey described Miss Tweddle-Taylor, five one, as a well-loved member of staff and wonderful friend and colleague." | [true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true]
    }
}
