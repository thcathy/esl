package com.esl.web.jsf.controller.practice

import com.esl.ESLApplication
import com.esl.dao.IMemberDAO
import com.esl.dao.IMemberWordDAO
import com.esl.dao.IPhoneticQuestionDAO
import com.esl.model.Member
import com.esl.model.MemberWord
import com.esl.model.PhoneticQuestion
import com.esl.web.model.UserSession
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes=ESLApplication.class)
@SpringBootTest
public class MyVocabPracticeControllerSpec extends Specification {
    @Autowired public MyVocabPracticeController myVocabPracticeController
    @Autowired public IMemberDAO memberDAO
    @Autowired public IMemberWordDAO memberWordDAO
    @Autowired public IPhoneticQuestionDAO phoneticQuestionDAO

    Member tester
    PhoneticQuestion question
    MemberWord memberWord
    UserSession session

    def setup()  {
        tester = memberDAO.getMemberByUserID("tester")
        session = new UserSession()
        session.setMember(tester)
        question = phoneticQuestionDAO.getPhoneticQuestionByWord("apple")
        memberWord = new MemberWord(tester, question)
        memberWordDAO.persist(memberWord)
    }

    def cleanup()  {
        memberWordDAO.transit(memberWord)
    }

    @Test
    public def "My vocab practice use vocab images"() {
        when:
        myVocabPracticeController.setUserSession(session)
        myVocabPracticeController.clearController()
        myVocabPracticeController.getRandomQuestion()
        String images = myVocabPracticeController.getMemberWord().getWord().getPicsFullPathsInString()

        then:
        images.contains("data:image")
        !images.contains("http://")
        !images.contains("https://")
    }
}