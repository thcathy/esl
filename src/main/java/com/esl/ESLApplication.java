package com.esl;

import org.ocpsoft.rewrite.servlet.RewriteFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
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
@EnableScheduling
public class ESLApplication {

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
        ConfigurableApplicationContext c = SpringApplication.run(ESLApplication.class, args);
    }
}