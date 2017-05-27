package com.esl.service.dictation;

import com.esl.entity.dictation.SentenceHistory;
import com.esl.util.ValidationUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class ArticleDictationService {
    private static Logger log = LoggerFactory.getLogger(ArticleDictationService.class);

    public SentenceHistory compare(String question, String answer) {
        log.info("compare: question [{}]", question);
        log.info("compare: answer   [{}]", answer);

        List<String> questionSegments = CollectionUtils.arrayToList(question.split(" "));
        List<String> answerSegments = CollectionUtils.arrayToList(answer.split(" "));
        List<Boolean> isCorrect = new ArrayList<>(questionSegments.size());

        int answerPosition = 0;
        for (int questionPosition=0; questionPosition < questionSegments.size(); questionPosition++) {
            boolean correct = false;
            // no more answer
            if (answerPosition >= answerSegments.size()) {
                isCorrect.add(correct);
                continue;
            }

            String questionSegment = questionSegments.get(questionPosition);
            String questionAlphabet = ValidationUtil.alphabetOnly(questionSegment);
            String answerAlphabet = ValidationUtil.alphabetOnly(answerSegments.get(answerPosition));

            if (isBlank(questionAlphabet))
                correct = true;
            else {
                for (int i = answerPosition; i < answerSegments.size(); i++) {
                    String subAnswerAlphabet = ValidationUtil.alphabetOnly(answerSegments.get(i));
                    if (ValidationUtil.wordEqual(questionAlphabet, subAnswerAlphabet)) {
                        correct = true;
                        answerPosition = i-1;
                        break;
                    }
                }
            }

            if (questionAndAnsNotBlank(questionAlphabet, answerAlphabet)
                    || questionAndAnsBlank(questionAlphabet, answerAlphabet))
                if (correct || !answerSizeLeftIsSmaller(questionPosition, questionSegments.size(), answerPosition, answerSegments.size()))
                    answerPosition++;

            isCorrect.add(correct);
        }

        log.info("compare result: segments {}", StringUtils.join(questionSegments));
        log.info("compare result: isCorrect {}", StringUtils.join(isCorrect));

        return new SentenceHistory(question, answer, questionSegments, isCorrect);
    }

    private boolean answerSizeLeftIsSmaller(int questionPosition, int questionSize, int answerPosition, int ansSize) {
        return (ansSize - answerPosition) < (questionSize - questionPosition);
    }

    private boolean questionAndAnsBlank(String questionAlphabet, String answerAlphabet) {
        return isBlank(questionAlphabet) && isBlank(answerAlphabet);
    }

    private boolean questionAndAnsNotBlank(String questionAlphabet, String answerAlphabet) {
        return !isBlank(questionAlphabet) && !isBlank(answerAlphabet);
    }
}
