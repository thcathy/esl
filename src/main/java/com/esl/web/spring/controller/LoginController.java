package com.esl.web.spring.controller;

import com.auth0.NonceUtils;
import com.auth0.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ESLAuth0Config appConfig;

    @Autowired
    public LoginController(ESLAuth0Config appConfig) {
        this.appConfig = appConfig;
    }

    @RequestMapping(value="/login", method = RequestMethod.GET)
    protected String login(@RequestParam(value="signup", defaultValue = "false") boolean isSignup, final Map<String, Object> model, final HttpServletRequest req) {
        logger.info("Open login page");
        detectError(model);
        // add a Nonce value to session storage
        NonceUtils.addNonceToStorage(req);
        model.put("clientId", appConfig.getClientId());
        model.put("clientDomain", appConfig.getDomain());
        model.put("loginCallback", appConfig.getLoginCallback());
        model.put("state", SessionUtils.getState(req));
        model.put("allowLogin", !isSignup);
        return "login";
    }

    private void detectError(final Map<String, Object> model) {
        if (model.get("error") != null) {
            model.put("error", true);
        } else {
            model.put("error", false);
        }
    }


}
