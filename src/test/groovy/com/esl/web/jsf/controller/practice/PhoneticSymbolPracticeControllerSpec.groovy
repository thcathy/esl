package com.esl.web.jsf.controller.practice

import com.esl.BaseSpec
import com.esl.dao.IGradeDAO
import com.esl.dao.IMemberDAO
import com.esl.dao.IMemberWordDAO
import com.esl.dao.IPhoneticQuestionDAO
import com.esl.model.Member
import com.esl.model.practice.PhoneticSymbols
import com.esl.web.model.UserSession
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
public class PhoneticSymbolPracticeControllerSpec extends BaseSpec {
    @Autowired PhoneticSymbolPracticeController phoneticSymbolPracticeController
    @Autowired IMemberDAO memberDAO
    @Autowired IMemberWordDAO memberWordDAO
    @Autowired IPhoneticQuestionDAO phoneticQuestionDAO
    @Autowired IGradeDAO gradeDAO

    Member tester
    UserSession session

    def setup()  {
        tester = memberDAO.getMemberByUserID("tester")
        session = new UserSession()
        session.setMember(tester)

        phoneticSymbolPracticeController.userSession = session
        phoneticSymbolPracticeController.selectedGrade = "K3"
        /*def phoneticPracticeController = Mock(PhoneticPracticeController)
        phoneticPracticeController.getSelectedGrade() >> "K3"
        phoneticSymbolPracticeController.phoneticPracticeController = phoneticPracticeController*/

    }

    @Test
    def "phonetic symbol practice use vocab images"() {
        when:
        phoneticSymbolPracticeController.selectedLevel = PhoneticSymbols.Level.Rookie
        String view = phoneticSymbolPracticeController.start()
        String images = phoneticSymbolPracticeController.question.picsFullPathsInString

        then:
        assert view == "/practice/phoneticsymbolpractice/practice"
        images.contains("data:image")
        !images.contains("http://")
        !images.contains("https://")
    }
}