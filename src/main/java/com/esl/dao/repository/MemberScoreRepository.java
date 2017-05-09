package com.esl.dao.repository;


import com.esl.entity.practice.MemberScore;
import com.esl.model.Member;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
public interface MemberScoreRepository extends PagingAndSortingRepository<MemberScore, String> {
    Optional<MemberScore> findByMemberAndScoreYearMonth(Member member, int scoreYearMonth);

    @Async
    CompletableFuture<List<MemberScore>> findByMemberAndScoreYearMonthGreaterThanEqual(Member member, int greaterEqualScoreYearMonth);

    @Async
    CompletableFuture<List<MemberScore>> findTop5ByScoreYearMonthAndScoreGreaterThanEqual(int scoreYearMonth, int minScore);

    @Async
    CompletableFuture<List<MemberScore>> findTop5ByScoreYearMonthAndScoreLessThanEqual(int scoreYearMonth, int minScore);
}