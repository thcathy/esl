package com.esl.test.service.practice;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.esl.model.PhoneticQuestion;
import com.esl.service.practice.PhoneticSymbolPracticeService;

public class TestPhoneticSymbolPracticeService {

	@Test
	public void checkAnswer_givenNoStress_shouldRemainTrue() {
		PhoneticSymbolPracticeService service = new PhoneticSymbolPracticeService();
		PhoneticQuestion banana = new PhoneticQuestion("banana", "bəˋnɑ:nə");
		
		assertTrue(service.checkAnswer(banana, "bənɑ:nə"));
	}
}
