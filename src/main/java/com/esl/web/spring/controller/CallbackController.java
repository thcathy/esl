package com.esl.web.spring.controller;

import com.auth0.Auth0User;
import com.auth0.SessionUtils;
import com.auth0.web.Auth0CallbackHandler;
import com.esl.dao.GradeDAO;
import com.esl.dao.MemberDAO;
import com.esl.model.Member;
import com.esl.model.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Controller
public class CallbackController extends Auth0CallbackHandler {
    private static Logger logger = LoggerFactory.getLogger(CallbackController.class);

    @Autowired
    MemberDAO memberDAO;

    @Autowired
    GradeDAO gradeDAO;

    @RequestMapping(value="/callback" ,method = RequestMethod.GET)
    protected void callback(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        super.handle(req, res);
        Auth0User user  = SessionUtils.getAuth0User(req);
        logger.info("Auth0User callback: [{}]", user);
        req.getSession().setAttribute("MEMBER", retrieveOrCreateMember(user));
    }

    private Member retrieveOrCreateMember(Auth0User user) {
        Optional<Member> member = memberDAO.getMemberByEmail(user.getEmail());
        return member.orElseGet(() -> createMemberFrom(user));
    }

    private Member createMemberFrom(Auth0User user) {
        logger.info("Create member from Auth0User: [{}]", user);

        Member member = new Member(user.getUserId(), new Name(user.getFamilyName(), user.getGivenName()));
        member.setEmailAddress(user.getEmail());
        member.setGrade(gradeDAO.getFirstLevelGrade());
        memberDAO.persist(member);
        return member;
    }


}