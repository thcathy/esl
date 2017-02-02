package com.esl;

import org.ocpsoft.rewrite.servlet.RewriteFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

@SpringBootApplication
@ImportResource("classpath:com/esl/ESL-context.xml")
@PropertySource("classpath:auth0.properties")
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
    public HibernateJpaSessionFactoryBean sessionFactory() {
        HibernateJpaSessionFactoryBean sessionFactory = new HibernateJpaSessionFactoryBean();
        return sessionFactory;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext c = SpringApplication.run(ESLApplication.class, args);
    }
}