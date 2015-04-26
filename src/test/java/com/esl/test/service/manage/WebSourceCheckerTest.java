package com.esl.test.service.manage;

import static org.easymock.EasyMock.*;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.esl.service.IMailService;
import com.esl.service.MailService;
import com.esl.service.manage.WebSourceChecker;
import com.esl.util.web.CambridgeDictionaryParser;

public class WebSourceCheckerTest extends EasyMockSupport {
	WebSourceChecker checker;
	IMailService mockMailService;
	CambridgeDictionaryParser parser;

	@Before
	public void setup() {
		checker = new WebSourceChecker();
		mockMailService = createMock(MailService.class);
		checker.setMailService(mockMailService);
		parser = createMock(CambridgeDictionaryParser.class);
	}


	@Test
	public void testNullCheck() {
		replay(mockMailService);
		WebSourceChecker checker = new WebSourceChecker();
		checker.checkAll();
		verify(mockMailService);
	}

	@Test
	public void testSuccessCheck() {
		checker.addTestContent(parser);
		expect(parser.parse()).andReturn(true);
		expect(parser.isContentCorrect()).andReturn(true);

		replayAll();
		checker.checkAll();
		verifyAll();
	}

	@Test
	public void testFailCheck() {
		checker.addTestContent(parser);
		expect(parser.parse()).andReturn(false);
		expect(parser.isContentCorrect()).andReturn(false);
		expect(parser.getSourceLink()).andReturn("");
		expect(parser.getParsedContent()).andReturn("");
		expect(parser.getParsedContentCheck()).andReturn("");
		expect(mockMailService.sendToHost((String)anyObject(),(String)anyObject())).andReturn(true);
		expectLastCall().once();

		replayAll();
		checker.checkAll();
		verifyAll();
	}

}
