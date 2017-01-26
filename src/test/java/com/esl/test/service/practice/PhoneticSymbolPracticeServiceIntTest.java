package com.esl.test.service.practice;

import com.esl.model.PhoneticPractice;
import com.esl.model.PhoneticQuestion;
import com.esl.service.practice.IPhoneticPracticeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PhoneticSymbolPracticeServiceIntTest {
    @Qualifier("phoneticPracticeService")
    @Autowired private IPhoneticPracticeService service;

    @Test
    public void generatePractice_allQuestionsUseVocabImage() {
        PhoneticPractice practice = service.generatePractice(null, "P1");
        assert practice != null;
        for (PhoneticQuestion question : practice.getQuestions()) {
            String images = question.getPicsFullPathsInString();
            assert images.contains("data:image");
            assert !images.contains("http://");
            assert !images.contains("https://");
        }

    }
}
