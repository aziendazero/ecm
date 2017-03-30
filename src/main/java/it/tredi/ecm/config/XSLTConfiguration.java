package it.tredi.ecm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.xslt.XsltView;
import org.springframework.web.servlet.view.xslt.XsltViewResolver;

@Configuration
public class XSLTConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public ViewResolver getXSLTViewResolver() {

    	 XsltViewResolver xsltResolover = new XsltViewResolver();
         xsltResolover.setOrder(1);
         xsltResolover.setSourceKey("xmlSource");

         xsltResolover.setViewClass(XsltView.class);
         xsltResolover.setViewNames(new String[] {"XSLTView"});
         xsltResolover.setPrefix("classpath:/xsltResolver/");
         xsltResolover.setSuffix(".xsl");

         return xsltResolover;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
