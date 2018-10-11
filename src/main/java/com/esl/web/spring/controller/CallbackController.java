package com.esl.web.spring.controller;

import com.auth0.IdentityVerificationException;
import com.auth0.SessionUtils;
import com.auth0.Tokens;
import com.auth0.json.auth.UserInfo;
import com.esl.dao.GradeDAO;
import com.esl.dao.MemberDAO;
import com.esl.model.Member;
import com.esl.model.Name;
import com.esl.web.jsf.controller.Auth0Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Controller
public class CallbackController {
    private static Logger logger = LoggerFactory.getLogger(CallbackController.class);

    @Autowired
    MemberDAO memberDAO;

    @Autowired
    GradeDAO gradeDAO;

    @Autowired
    private Auth0Controller controller;
    private final String redirectOnFail;
    private final String redirectOnSuccess;

    public CallbackController() {
        this.redirectOnFail = "/login";
        this.redirectOnSuccess = "/member/index.jsf";
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    protected void getCallback(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        handle(req, res);
    }

    @RequestMapping(value = "/callback", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    protected void postCallback(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        handle(req, res);
    }

    public void handle(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            Tokens tokens = controller.handle(req);
            SessionUtils.set(req, "accessToken", tokens.getAccessToken());
            SessionUtils.set(req, "idToken", tokens.getIdToken());
            res.sendRedirect(redirectOnSuccess);
            controller.getUserInfo(tokens.getAccessToken()).ifPresent(u ->
                    req.getSession().setAttribute("MEMBER", retrieveOrCreateMember(u))
            );
        } catch (IdentityVerificationException e) {
            e.printStackTrace();
            res.sendRedirect(redirectOnFail);
        }
    }

    //@RequestMapping(value="/callback" ,method = RequestMethod.GET)
    //protected void callback(@RequestParam(value = "redirect", defaultValue = "") String redirectUrl,
    //                        final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
    //    handle(redirectUrl, req, res);
    //    Auth0User user  = SessionUtils.getAuth0User(req);
    //    logger.info("Auth0User callback: [{}]", user);
    //    logger.info("redirectUrl: {}", redirectUrl);
    //    req.getSession().setAttribute("MEMBER", retrieveOrCreateMember(user));
    //}
//
    //public void handle(String redirectUrl, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    //    try {
    //        if(this.isValidRequest(req)) {
    //            Tokens ex = this.fetchTokens(req);
    //            Auth0User auth0User = this.auth0Client.getUserProfile(ex);
    //            this.store(ex, auth0User, req);
    //            NonceUtils.removeNonceFromStorage(req);
//
    //            if (StringUtils.isEmpty(redirectUrl))
    //                this.onSuccess(req, res);
    //            else
    //                res.sendRedirect(req.getContextPath() + redirectUrl);
    //        } else {
    //            this.onFailure(req, res, new IllegalStateException("Invalid state or error"));
    //        }
    //    } catch (RuntimeException var5) {
    //        this.onFailure(req, res, var5);
    //    }
//
    //}
//
    private Member retrieveOrCreateMember(UserInfo user) {
        if (user == null) return null;

        Optional<Member> member = memberDAO.getMemberByEmail((String) user.getValues().get("email"));
        return member.orElseGet(() -> createMemberFrom(user));
    }

    private Member createMemberFrom(UserInfo user) {
        logger.info("Create member from Auth0User: [{}]", user);

        Member member = new Member((String)user.getValues().get("sub"), new Name((String)user.getValues().getOrDefault("family_name", ""), (String)user.getValues().getOrDefault("given_name","")));
        member.setEmailAddress((String)user.getValues().get("email"));
        member.setGrade(gradeDAO.getFirstLevelGrade());
        memberDAO.persist(member);
        return member;
    }


}