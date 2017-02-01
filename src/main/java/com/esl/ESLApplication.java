package com.esl;

import org.ocpsoft.rewrite.servlet.RewriteFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

@SpringBootApplication
@ImportResource("classpath:com/esl/ESL-context.xml")
public class ESLApplication {

    public void onStartup(ServletContext servletContext)
            throws ServletException {

        servletContext.setInitParameter("javax.faces.DEFAULT_SUFFIX", ".xhtml");
        servletContext.setInitParameter("javax.faces.PARTIAL_STATE_SAVING_METHOD", "true");
        servletContext.setInitParameter("javax.faces.FACELETS_REFRESH_PERIOD", "1");
        //servletContext.setInitParameter("com.sun.faces.forceLoadConfiguration", Boolean.TRUE.toString());
        servletContext.setInitParameter("javax.servlet.jsp.jstl.fmt.localizationContext", "resources.application");
        //servletContext.setInitParameter("javax.faces.FACELETS_LIBRARIES", "/META-INF/resources/WEB-INF/taglib/esl.taglib.xml");
    }

    /*@Bean
    public ServletRegistrationBean servletRegistrationBean() {
        FacesServlet servlet = new FacesServlet();
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(servlet);
        servletRegistrationBean.addUrlMappings("*.jsf","*.xhtml");
        return servletRegistrationBean;
    }*/

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