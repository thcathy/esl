package com.esl.web.spring.controller;

import com.esl.web.jsf.controller.Auth0Controller;
import com.esl.web.model.UserSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private Auth0Controller controller;

    @Autowired
    public LoginController() {
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    protected String login(final HttpServletRequest req) {
        logger.debug("Performing login");
        String redirectUri = StringUtils.replace(req.getRequestURL().toString(), req.getRequestURI(), "") + "/callback";
        String authorizeUrl = controller.buildAuthorizeUrl(req, redirectUri, getAuth0Locale(req.getSession()));
        return "redirect:" + authorizeUrl;
    }

    //@RequestMapping(value="/login", method = RequestMethod.GET)
    //protected String login(@RequestParam(value="signup", defaultValue = "false") boolean isSignup,
    //                       @RequestParam(value="redirect", defaultValue = "") String reDirectUrl,
    //                       final Map<String, Object> model, final HttpServletRequest req) {
    //    logger.info("Open login page");
    //    detectError(model);
    //    // add a Nonce value to session storage
    //    String state = secureRandomString();
    //    model.put("clientId", appConfig.getClientId());
    //    model.put("clientDomain", appConfig.getDomain());
    //    model.put("loginCallback", appConfig.getLoginCallback());
    //    model.put("state", SessionUtils.getState(req));
    //    model.put("allowLogin", !isSignup);
    //    model.put("reDirectUrl", reDirectUrl);
    //    model.put("auth0Locale", getAuth0Locale(req.getSession()));
    //    return "login";
    //}

    private String getAuth0Locale(HttpSession session) {
        if (session.getAttribute("userSession") == null)
            return "en";

        UserSession userSession = (UserSession) session.getAttribute("userSession");
        return userSession.getAuth0Locale();
    }

    private void detectError(final Map<String, Object> model) {
        if (model.get("error") != null) {
            model.put("error", true);
        } else {
            model.put("error", false);
        }
    }


}
