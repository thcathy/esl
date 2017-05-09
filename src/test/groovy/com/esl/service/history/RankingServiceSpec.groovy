package com.esl.service.history

import com.esl.BaseSpec
import com.esl.ESLApplication
import com.esl.dao.repository.MemberScoreRepository
import com.esl.entity.practice.MemberScore
import com.esl.entity.practice.MemberScoreRanking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

import java.util.concurrent.CompletableFuture

@SpringBootTest
@ContextConfiguration(classes=ESLApplication.class)
class RankingServiceSpec extends BaseSpec {
    @Autowired RankingService service
    @Autowired MemberScoreRepository memberScoreRepository

    def "Create ranking for tester"() {
        MemberScore testerLatestScore = getLatestScore(tester)

        when: "generate a practice"
        CompletableFuture<MemberScoreRanking> completableFuture = service.myScoreRanking(testerLatestScore)
        MemberScoreRanking result = completableFuture.join()
        result.scores.each {println it}

        then: "practice is valid"
        result.base == testerLatestScore
        result.scores.findAll { it.getMember().getId() == testerLatestScore.getMember().getId() }.size() == 1
        result.isTop() == false
    }

}