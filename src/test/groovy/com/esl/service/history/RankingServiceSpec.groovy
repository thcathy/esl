package com.esl.service.history

import com.esl.BaseSpec
import com.esl.ESLApplication
import com.esl.dao.repository.MemberScoreRepository
import com.esl.entity.practice.MemberScore
import com.esl.entity.practice.MemberScoreRanking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

import java.util.concurrent.CompletableFuture

@SpringBootTest
@ContextConfiguration(classes = ESLApplication.class)
class RankingServiceSpec extends BaseSpec {
    @Autowired
    RankingService service
    @Autowired
    MemberScoreRepository memberScoreRepository

    def "Create ranking for tester"() {
        MemberScore testerLatestScore = getLatestScore(tester)

        when: "generate a practice"
        CompletableFuture<MemberScoreRanking> completableFuture = service.myScoreRanking(testerLatestScore)
        MemberScoreRanking result = completableFuture.join()
        result.scores.each { println it }

        then: "practice is valid"
        result.base == testerLatestScore
        result.scores.findAll {
            it.getMember().getId() == testerLatestScore.getMember().getId()
        }.size() == 1
        result.isTop() == false
    }

    // this test have data dependency on member score setup between Jan 2016 to Jul 2016
    @Unroll
    def "MemberScoreRanking for tester on [#scoreYearMonth] should have size [#expSize], first pos [#expFirstPos], base pos [#expBasePosition]"(
            int scoreYearMonth, int expSize, long expFirstPos, int expBasePosition) {
        MemberScore testerScore = memberScoreRepository.findByMemberAndScoreYearMonth(tester, scoreYearMonth).get()

        when: "create ranking"
        MemberScoreRanking ranking = service.myScoreRanking(testerScore).join()
        ranking.scores.each {println it}

        then:
        ranking.scores.size() == expSize
        ranking.firstPosition == expFirstPos
        ranking.scores.indexOf(testerScore) + 1 == expBasePosition
        verifyRankingOrder(ranking)

        where:
        scoreYearMonth | expSize | expFirstPos | expBasePosition
        201601         | 1       | 1           | 1
        201602         | 2       | 1           | 2
        201603         | 3       | 1           | 2
        201604         | 3       | 1           | 1
        201605         | 5       | 1           | 3
        201606         | 5       | 2           | 3
        201607         | 5       | 2           | 5
    }

    // this test have data dependency on member score setup between Jan 2016 to Jul 2016
    @Unroll
    def "List top 5 score on #scoreYearMonth"(int scoreYearMonth, int[] expScore) {
        when: "get top scores"
        MemberScoreRanking ranking = service.topScore(scoreYearMonth).join()

        then: "verify result"
        ranking.isTop()
        ranking.firstPosition == 1
        ranking.scores.size() == expScore.size()
        int endPosition = expScore.size()-1
        (0..endPosition).each {
            assert ranking.scores[it].score == expScore[it]
        }

        where:
        scoreYearMonth | expScore
        201601         | [100]
        201602         | [3, 2]
        201603         | [3, 2, 1]
        201604         | [10, 3, 1]
        201605         | [3, 3, 2, 1, 1]
        201606         | [102, 101, 101, 14, 11]
        201607         | [1, 1, 1, 1, 1]
    }

    @Unroll
    def "Test random ranking: #i times"(int i) {
        when: "get random top score"
        MemberScoreRanking ranking = service.randomTopScore().join()

        then: "verify"
        ranking.isTop()
        ranking.firstPosition == 1
        if (ranking.scores.size() > 0) {
            ranking.scores.each {
                println "$it"
                assert it.member != null
            }
        }

        where:
        i << (1..3)
    }

    void verifyRankingOrder(MemberScoreRanking memberScoreRanking) {
        for (int i=1; i < memberScoreRanking.scores.size(); i++) {
            assert (memberScoreRanking.scores[i-1].score > memberScoreRanking.scores[i].score
            || (
                    memberScoreRanking.scores[i-1].score == memberScoreRanking.scores[i].score
                            && memberScoreRanking.scores[i-1].lastUpdatedDate <= memberScoreRanking.scores[i].lastUpdatedDate)
            )
        }
    }
}