package com.esl.service.history

import com.esl.BaseSpec
import com.esl.ESLApplication
import com.esl.dao.MemberDAO
import com.esl.dao.repository.MemberScoreRepository
import com.esl.entity.practice.MemberScore
import com.esl.entity.practice.MemberScoreRanking
import com.esl.model.Member
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
    @Autowired
    MemberDAO memberDAO


    def "Setup static data for test"() {
        when: "setup static data"
        addIfNotExist(tester, MemberScore.thisMonth())

        addIfNotExist(tester, 201601, 100)

        addIfNotExist(memberDAO.getMemberById(18), 201602, 3, new Date(2016, 2, 1))
        addIfNotExist(tester, 201602, 2, new Date(2016, 2, 2))

        addIfNotExist(memberDAO.getMemberById(18), 201603, 3, new Date(2016, 3, 1))
        addIfNotExist(tester, 201603, 2, new Date(2016, 3, 2))
        addIfNotExist(memberDAO.getMemberById(19), 201603, 1, new Date(2016, 3, 3))

        addIfNotExist(tester, 201604, 10, new Date(2016, 4, 1))
        addIfNotExist(memberDAO.getMemberById(18), 201604, 3, new Date(2016, 4, 2))
        addIfNotExist(memberDAO.getMemberById(19), 201604, 1, new Date(2016, 4, 3))

        addIfNotExist(memberDAO.getMemberById(18), 201605, 3, new Date(2016, 5, 1))
        addIfNotExist(memberDAO.getMemberById(19), 201605, 3, new Date(2016, 5, 2))
        addIfNotExist(tester, 201605, 2, new Date(2016, 2, 3))
        addIfNotExist(memberDAO.getMemberById(20), 201605, 1, new Date(2016, 5, 4))
        addIfNotExist(memberDAO.getMemberById(21), 201605, 1, new Date(2016, 5, 5))

        addIfNotExist(memberDAO.getMemberById(22), 201606, 102, new Date(2016, 6, 1))
        addIfNotExist(memberDAO.getMemberById(18), 201606, 101, new Date(2016, 6, 2))
        addIfNotExist(memberDAO.getMemberById(19), 201606, 101, new Date(2016, 6, 3))
        addIfNotExist(tester, 201606, 14, new Date(2016, 6, 4))
        addIfNotExist(memberDAO.getMemberById(20), 201606, 11, new Date(2016, 6, 5))
        addIfNotExist(memberDAO.getMemberById(21), 201606, 10, new Date(2016, 6, 6))

        addIfNotExist(memberDAO.getMemberById(22), 201607, 1, new Date(2016, 7, 1))
        addIfNotExist(memberDAO.getMemberById(18), 201607, 1, new Date(2016, 7, 2))
        addIfNotExist(memberDAO.getMemberById(19), 201607, 1, new Date(2016, 7, 3))
        addIfNotExist(memberDAO.getMemberById(20), 201607, 1, new Date(2016, 7, 4))
        addIfNotExist(memberDAO.getMemberById(21), 201607, 1, new Date(2016, 7, 5))
        addIfNotExist(tester, 201607, 1, new Date(2016, 7, 6))

        then: "always success"
        1 == 1
    }

    void addIfNotExist(Member member, int yearMonth, int score = 1, Date lastUpdatedDate = new Date()) {
         if (!memberScoreRepository.findByMemberAndScoreYearMonth(member, yearMonth).isPresent()) {
             MemberScore memberScore = new MemberScore(member, yearMonth).addScore(score)
             memberScore.setLastUpdatedDate(lastUpdatedDate)
             memberScoreRepository.save(memberScore)
         }
    }

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