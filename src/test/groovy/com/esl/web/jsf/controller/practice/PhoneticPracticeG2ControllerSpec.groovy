package com.esl.web.jsf.controller.practice

import com.esl.BaseSpec
import com.esl.dao.IGradeDAO
import com.esl.dao.IMemberDAO
import com.esl.dao.IMemberWordDAO
import com.esl.dao.IPhoneticQuestionDAO
import com.esl.model.Member
import com.esl.web.model.UserSession
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

//@ContextConfiguration(locations = "/com/esl/ESL-context.xml")
@SpringBootTest
public class PhoneticPracticeG2ControllerSpec extends BaseSpec {
    @Autowired PhoneticPracticeG2Controller phoneticPracticeG2Controller
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

        phoneticPracticeG2Controller.userSession = session

        def phoneticPracticeController = Mock(PhoneticPracticeController)
        phoneticPracticeController.getSelectedGrade() >> "K3"
        phoneticPracticeG2Controller.phoneticPracticeController = phoneticPracticeController
    }

    @Test
    def "phonetic practice use vocab images"() {
        when:
        phoneticPracticeG2Controller.start()
        String images = phoneticPracticeG2Controller.question.picsFullPathsInString

        then:
        images.contains("data:image")
        !images.contains("http://")
        !images.contains("https://")
    }
}