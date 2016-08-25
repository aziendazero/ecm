package it.tredi.ecm;

import javax.servlet.Filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

import it.tredi.ecm.config.UserChangePasswordCheckFilter;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	public PersistenceAnnotationBeanPostProcessor persistenceBeanPostProcessor() {
	    return new PersistenceAnnotationBeanPostProcessor();
	}
	
	@Bean
    public FilterRegistrationBean myFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(changePasswordFilter());
        registration.addUrlPatterns("/*");
        registration.setName("changePasswordFilter");
        registration.setOrder(1);
        return registration;
    }
	
	@Bean(name = "changePasswordFilter")
	public Filter changePasswordFilter() {
	    return new UserChangePasswordCheckFilter();
	}
	
	@Bean
    public Java8TimeDialect java8TimeDialect() {
        return new Java8TimeDialect();
    }
}
