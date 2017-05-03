package com.esl.web.jsf.controller.member

import com.esl.BaseSpec
import com.esl.ESLApplication
import com.esl.dao.IGradeDAO
import com.esl.dao.IMemberDAO
import com.esl.dao.IMemberWordDAO
import com.esl.dao.IPhoneticQuestionDAO
import com.esl.entity.practice.MemberScore
import com.esl.model.Member
import com.esl.service.JSFService
import com.esl.web.model.UserSession
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import reactor.bus.EventBus

@ContextConfiguration(classes=ESLApplication.class)
@SpringBootTest
public class SummaryControllerSpec extends BaseSpec {
    @Autowired SummaryController controller
    @Autowired IMemberDAO memberDAO
    @Autowired IMemberWordDAO memberWordDAO
    @Autowired IPhoneticQuestionDAO phoneticQuestionDAO
    @Autowired IGradeDAO gradeDAO
    @Autowired EventBus eventBus
    @Autowired JSFService jsfService

    Member tester
    UserSession session

    def setup()  {
        tester = memberDAO.getMemberByUserID("tester")
        session = new UserSession()
        session.setMember(tester)

        controller.userSession = session
        MemberScore latestScore = getLatestScore(tester)
        memberScoreRepository.save(latestScore)
    }

    @Test
    def "init will enrich data for display"() {
        when:
        controller.init()

        then:
        controller.monthlyScore[0].scoreYearMonth == MemberScore.thisMonth()
        controller.allTimesScore.scoreYearMonth == MemberScore.allTimesMonth()
    }
}