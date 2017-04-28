package com.esl.service.practice

import com.esl.BaseSpec
import com.esl.ESLApplication
import com.esl.enumeration.VocabDifficulty
import com.esl.model.practice.PhoneticSymbols
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(classes=ESLApplication.class)
@ActiveProfiles("dev")
public class PhoneticSymbolPracticeServiceSpec extends BaseSpec {
    @Autowired PhoneticSymbolPracticeService service

    def setup()  {
    }


    @Test
    def "Calculate score by difficulty and level"(VocabDifficulty difficulty, PhoneticSymbols.Level level, int expectedScore) {
        println "Test with difficulty $difficulty and Level $level"

        expect:
        service.calculateScore(difficulty, level) == expectedScore

        where:
        difficulty                  | level                         | expectedScore
        VocabDifficulty.Beginner    | PhoneticSymbols.Level.Rookie  | 1
        VocabDifficulty.Easy        | PhoneticSymbols.Level.Rookie  | 2
        VocabDifficulty.Normal      | PhoneticSymbols.Level.Rookie  | 2
        VocabDifficulty.Hard        | PhoneticSymbols.Level.Rookie  | 3
        VocabDifficulty.VeryHard    | PhoneticSymbols.Level.Rookie  | 4
        VocabDifficulty.Beginner    | PhoneticSymbols.Level.Low     | 3
        VocabDifficulty.Easy        | PhoneticSymbols.Level.Low     | 3
        VocabDifficulty.Normal      | PhoneticSymbols.Level.Low     | 4
        VocabDifficulty.Hard        | PhoneticSymbols.Level.Low     | 5
        VocabDifficulty.VeryHard    | PhoneticSymbols.Level.Low     | 6
        VocabDifficulty.Beginner    | PhoneticSymbols.Level.Medium  | 5
        VocabDifficulty.Easy        | PhoneticSymbols.Level.Medium  | 5
        VocabDifficulty.Normal      | PhoneticSymbols.Level.Medium  | 6
        VocabDifficulty.Hard        | PhoneticSymbols.Level.Medium  | 7
        VocabDifficulty.VeryHard    | PhoneticSymbols.Level.Medium  | 8
        VocabDifficulty.Beginner    | PhoneticSymbols.Level.Full    | 7
        VocabDifficulty.Easy        | PhoneticSymbols.Level.Full    | 8
        VocabDifficulty.Normal      | PhoneticSymbols.Level.Full    | 8
        VocabDifficulty.Hard        | PhoneticSymbols.Level.Full    | 9
        VocabDifficulty.VeryHard    | PhoneticSymbols.Level.Full    | 10
    }
}