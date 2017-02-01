package com.esl.web.spring.controller;

import com.auth0.web.Auth0Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class ESLAuth0Config extends com.auth0.web.Auth0Config {

    @Bean
    public FilterRegistrationBean filterRegistration() {
        final FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new Auth0Filter(this));
        registration.addUrlPatterns(securedRoute);
        registration.addInitParameter("redirectOnAuthError", loginRedirectOnFail);
        registration.setName("Auth0Filter");
        return registration;
    }

}
