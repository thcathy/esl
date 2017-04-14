package com.esl.web.jsf.controller.practice

import com.esl.BaseSpec
import com.esl.dao.IGradeDAO
import com.esl.dao.IMemberDAO
import com.esl.dao.IMemberWordDAO
import com.esl.dao.IPhoneticQuestionDAO
import com.esl.enumeration.VocabDifficulty
import com.esl.model.Member
import com.esl.model.practice.PhoneticSymbols
import com.esl.web.model.UserSession
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
public class PhoneticSymbolPracticeControllerSpec extends BaseSpec {
    @Autowired PhoneticSymbolPracticeController controller
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

        controller.userSession = session
        controller.selectedGrade = "K3"
        /*def phoneticPracticeController = Mock(PhoneticPracticeController)
        phoneticPracticeController.getSelectedGrade() >> "K3"
        phoneticSymbolPracticeController.phoneticPracticeController = phoneticPracticeController*/

    }

    @Test
    def "phonetic symbol practice use vocab images"() {
        when:
        controller.selectedDifficulty = VocabDifficulty.Beginner
        controller.selectedLevel = PhoneticSymbols.Level.Full
        String view = controller.start()
        String images = controller.question.picsFullPathsInString

        then:
        view == "/practice/phoneticsymbolpractice/practice"
        controller.question != null
        images.contains("data:image")
        !images.contains("http://")
        !images.contains("https://")
    }

    @Test
    def "start without select difficulty return error view"() {
        when:
        controller.selectedDifficulty = null
        String view = controller.start()

        then:
        view == "/error"
    }
}