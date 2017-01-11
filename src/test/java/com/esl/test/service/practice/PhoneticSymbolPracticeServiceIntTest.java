package com.esl.test.service.practice;

import com.esl.model.PhoneticPractice;
import com.esl.model.PhoneticQuestion;
import com.esl.service.practice.IPhoneticPracticeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.boot.test.context;

import javax.annotation.Resource;

@ContextConfiguration(locations={"classpath:/com/esl/ESL-context.xml"})
@SpringBootTest
public class PhoneticSymbolPracticeServiceIntTest {
    @Resource(name="phoneticPracticeService") private IPhoneticPracticeService service;

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
