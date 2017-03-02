package com.esl.web.spring.controller;

import com.auth0.Auth0User;
import com.auth0.NonceUtils;
import com.auth0.SessionUtils;
import com.auth0.Tokens;
import com.auth0.web.Auth0CallbackHandler;
import com.esl.dao.GradeDAO;
import com.esl.dao.MemberDAO;
import com.esl.model.Member;
import com.esl.model.Name;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
    protected void callback(@RequestParam(value = "redirect", defaultValue = "") String redirectUrl,
                            final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        handle(redirectUrl, req, res);
        Auth0User user  = SessionUtils.getAuth0User(req);
        logger.info("Auth0User callback: [{}]", user);
        logger.info("redirectUrl: {}", redirectUrl);
        req.getSession().setAttribute("MEMBER", retrieveOrCreateMember(user));
    }

    public void handle(String redirectUrl, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        try {
            if(this.isValidRequest(req)) {
                Tokens ex = this.fetchTokens(req);
                Auth0User auth0User = this.auth0Client.getUserProfile(ex);
                this.store(ex, auth0User, req);
                NonceUtils.removeNonceFromStorage(req);

                if (StringUtils.isEmpty(redirectUrl))
                    this.onSuccess(req, res);
                else
                    res.sendRedirect(req.getContextPath() + redirectUrl);
            } else {
                this.onFailure(req, res, new IllegalStateException("Invalid state or error"));
            }
        } catch (RuntimeException var5) {
            this.onFailure(req, res, var5);
        }

    }

    private Member retrieveOrCreateMember(Auth0User user) {
        if (user == null) return null;

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