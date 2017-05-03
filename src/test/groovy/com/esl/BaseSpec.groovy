package com.esl

import com.esl.dao.repository.MemberScoreRepository
import com.esl.entity.practice.MemberScore
import com.esl.model.Member
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

class BaseSpec extends Specification {
    @Autowired MemberScoreRepository memberScoreRepository

    MemberScore getLatestScore(Member member) {
        Optional<MemberScore> optMemberScore = memberScoreRepository.findByMemberAndScoreYearMonth(member, MemberScore.thisMonth())
        MemberScore memberScore = optMemberScore.orElseGet({
            MemberScore newScore = new MemberScore(member, MemberScore.thisMonth())
            newScore.setCreatedDate(new Date())
            newScore.setLastUpdatedDate(new Date())
            return newScore
        })
    }
}
