package com.esl.dao.repository;


import com.esl.entity.practice.MemberScore;
import com.esl.model.Member;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberScoreRepository extends PagingAndSortingRepository<MemberScore, String> {
    Optional<MemberScore> findByMemberAndScoreYearMonth(Member member, int scoreYearMonth);
}