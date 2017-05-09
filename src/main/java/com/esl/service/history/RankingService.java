package com.esl.service.history;

import com.esl.dao.repository.MemberScoreRepository;
import com.esl.entity.practice.MemberScore;
import com.esl.entity.practice.MemberScoreRanking;
import com.esl.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class RankingService {
    private static Logger log = LoggerFactory.getLogger(RankingService.class);

    @Autowired MemberScoreRepository memberScoreRepository;

    @Value("${RankingService.numOfScore}") int numOfScoreInRank;

    @Async
    public CompletableFuture<MemberScoreRanking> myScoreRanking(MemberScore base) {
        log.debug("start create score ranking for member: {}", base.getMember());

        CompletableFuture<List<MemberScore>> highersFutures = memberScoreRepository.findTop5ByScoreYearMonthAndScoreGreaterThanEqual(base.getScoreYearMonth(), base.getScore());
        CompletableFuture<List<MemberScore>> lowersFutures = memberScoreRepository.findTop5ByScoreYearMonthAndScoreLessThanEqual(base.getScoreYearMonth(), base.getScore());

        List<MemberScore> scores = CollectionUtil.concatLists(numOfScoreInRank, base, lowersFutures.join(), highersFutures.join());

        return CompletableFuture.completedFuture(new MemberScoreRanking(base, scores, false));
    }

}
