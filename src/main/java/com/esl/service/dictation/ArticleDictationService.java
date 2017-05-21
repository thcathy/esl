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
            // no more answer
            if (answerPosition >= answerSegments.size()) {
                isCorrect.add(false);
                continue;
            }

            String questionSegment = questionSegments.get(questionPosition);
            String questionAlphabet = ValidationUtil.alphabetOnly(questionSegment);
            String answerAlphabet = ValidationUtil.alphabetOnly(answerSegments.get(answerPosition));

            if (!isBlank(questionAlphabet) && !ValidationUtil.wordEqual(questionAlphabet, answerAlphabet))
                isCorrect.add(false);
            else
                isCorrect.add(true);

            if ((!isBlank(questionAlphabet) && !isBlank(answerAlphabet))
                    || (isBlank(questionAlphabet) && isBlank(answerAlphabet)))
                answerPosition++;
        }

        log.info("compare result: segments {}", StringUtils.join(questionSegments));
        log.info("compare result: isCorrect {}", StringUtils.join(isCorrect));

        return new SentenceHistory(question, answer, questionSegments, isCorrect);
    }
}
