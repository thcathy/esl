package com.esl;

import com.esl.dao.PhoneticQuestionDAO;
import com.esl.model.PhoneticQuestion;
import com.esl.service.rest.WebParserRestService;
import org.ocpsoft.rewrite.servlet.RewriteFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@ImportResource("classpath:com/esl/ESL-context.xml")
@PropertySource("classpath:auth0.properties")
public class UpdatePhoneticQuetionCommand {
    private static Logger log = LoggerFactory.getLogger(UpdatePhoneticQuetionCommand.class);

    @Autowired
    public static PhoneticQuestionDAO phoneticQuestionDAO;

    @Autowired
    public static WebParserRestService webParserRestService;

    @Bean
    public FilterRegistrationBean rewriteFilter() {
        FilterRegistrationBean rwFilter = new FilterRegistrationBean(new RewriteFilter());
        rwFilter.setDispatcherTypes(EnumSet.of(DispatcherType.FORWARD, DispatcherType.REQUEST,
                DispatcherType.ASYNC, DispatcherType.ERROR));
        rwFilter.addUrlPatterns("/*");
        return rwFilter;
    }

    @Bean
    public Filter shallowETagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    @Bean
    public HibernateJpaSessionFactoryBean sessionFactory() {
        HibernateJpaSessionFactoryBean sessionFactory = new HibernateJpaSessionFactoryBean();
        return sessionFactory;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public ExecutorService executionPool () {
        return Executors.newFixedThreadPool(20);
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(UpdatePhoneticQuetionCommand.class)
                .web(false)
                .run(args);

        phoneticQuestionDAO = ctx.getBean(PhoneticQuestionDAO.class);
        webParserRestService = ctx.getBean(WebParserRestService.class);

        phoneticQuestionDAO.getAll().parallelStream()
                .filter(q -> q.getIPA().contains(","))
                .forEach(UpdatePhoneticQuetionCommand::convertIPA);

        System.exit(0);
    }

    private static void convertIPA(PhoneticQuestion question) {
        String firstIPA = question.getIPA().split(",")[0];

        log.info("update IPA from {} to {}", question.getIPA(), firstIPA);
        question.setIPA(firstIPA);
        phoneticQuestionDAO.persist(question);
    }

}