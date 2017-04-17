package com.esl.service.event.history;

import com.esl.dao.repository.MemberScoreRepository;
import com.esl.dao.repository.QuestionHistoryRepository;
import com.esl.entity.event.UpdatePracticeHistoryEvent;
import com.esl.entity.practice.MemberScore;
import com.esl.entity.practice.QuestionHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UpdatePracticeHistoryEventConsumer implements Consumer<Event<UpdatePracticeHistoryEvent>> {
    private static Logger log = LoggerFactory.getLogger(UpdatePracticeHistoryEventConsumer.class);

    @Autowired QuestionHistoryRepository questionHistoryRepository;
    @Autowired MemberScoreRepository memberScoreRepository;

    @Override
    public void accept(Event<UpdatePracticeHistoryEvent> event) {
        UpdatePracticeHistoryEvent payload = event.getData();
        log.info("consume event: {}", payload);

        updateQuestionHistory(payload);
        updateMemberScores(payload);
    }

    private void updateMemberScores(UpdatePracticeHistoryEvent payload) {
        if (payload.isCorrect) {
            updateMemberScore(payload, MemberScore.allTimesMonth());
            updateMemberScore(payload, MemberScore.thisMonth());
        }
    }

    private void updateMemberScore(UpdatePracticeHistoryEvent payload, int yearMonth) {
        MemberScore score = findOrCreateMemberScore(payload, yearMonth);
        score.setLastUpdatedDate(new Date());
        score.addScore(payload.score);
        memberScoreRepository.save(score);
    }

    private MemberScore findOrCreateMemberScore(UpdatePracticeHistoryEvent payload, int yearMonth) {
        Optional<MemberScore> score = memberScoreRepository.findByMemberAndScoreYearMonth(payload.member, yearMonth);
        return score.orElseGet(() -> {
            MemberScore newScore = new MemberScore(payload.member, yearMonth);
            newScore.setCreatedDate(new Date());
            return newScore;
        });
    }

    private void updateQuestionHistory(UpdatePracticeHistoryEvent payload) {
        QuestionHistory questionHistory = findOrCreateQuestionHistory(payload);
        questionHistory.setTotalAttempt(questionHistory.getTotalAttempt() + 1);
        if (payload.isCorrect)
            questionHistory.setTotalCorrect(questionHistory.getTotalCorrect() + 1);
        questionHistoryRepository.save(questionHistory);
    }

    private QuestionHistory findOrCreateQuestionHistory(UpdatePracticeHistoryEvent payload) {
        List<QuestionHistory> historyFromDB = questionHistoryRepository.findByMemberAndPracticeTypeAndWord(payload.member, payload.type, payload.question.getWord());
        if (historyFromDB.size() > 0)
            return historyFromDB.get(0);

        QuestionHistory history = new QuestionHistory();
        history.setMember(payload.member);
        history.setPracticeType(payload.type);
        history.setWord(payload.question.getWord());
        return history;
    }
}
