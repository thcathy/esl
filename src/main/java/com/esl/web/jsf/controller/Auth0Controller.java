package com.esl.web.jsf.controller;

import com.auth0.AuthenticationController;
import com.auth0.IdentityVerificationException;
import com.auth0.Tokens;
import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class Auth0Controller {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${com.auth0.domain}")
    private String domain;

    @Value(value = "${com.auth0.clientId}")
    private String clientId;

    @Value(value = "${com.auth0.clientSecret}")
    private String clientSecret;

    private AuthenticationController controller;
    private String userInfoAudience;
    private AuthAPI client;

    @Autowired
    public Auth0Controller() {
    }

    @PostConstruct
    public void postConstruct() {
        controller = com.auth0.AuthenticationController.newBuilder(domain, clientId, clientSecret).build();
        userInfoAudience = String.format("https://%s/userinfo", domain);
        client = new AuthAPI(domain, clientId, clientSecret);
    }

    public Tokens handle(HttpServletRequest request) throws IdentityVerificationException {
        return controller.handle(request);
    }

    public Optional<UserInfo> getUserInfo(String accessToken) {
        try {
            return Optional.of((UserInfo)this.client.userInfo(accessToken).execute());
        } catch (Auth0Exception e) {
            logger.warn("cannot get userinfo: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public String buildAuthorizeUrl(HttpServletRequest request, String redirectUri, String locale) {
        return controller
                .buildAuthorizeUrl(request, redirectUri)
                .withAudience(userInfoAudience)
                .withScope("openid name nickname email profile")
                .withParameter("language_base_url",locale)
                .build();
    }

}
