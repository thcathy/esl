package com.esl.test.service.practice;

import com.esl.model.PhoneticQuestion;
import com.esl.service.practice.PhoneticSymbolPracticeService;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PhoneticSymbolPracticeServiceTest {

	@Test
	public void checkAnswer_givenNoStress_shouldRemainTrue() {
		PhoneticSymbolPracticeService service = new PhoneticSymbolPracticeService();
		PhoneticQuestion banana = new PhoneticQuestion("banana", "bəˋnɑ:nə");
		
		assertTrue(service.checkAnswer(banana, "bənɑ:nə"));
	}
}
