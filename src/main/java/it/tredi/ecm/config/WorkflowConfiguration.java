package it.tredi.ecm.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import it.tredi.bonita.api.IBonitaAPIWrapper;
import it.tredi.bonita7.Bonita7APIWrapper;

@Configuration
@PropertySource("classpath:workflow.properties")
public class WorkflowConfiguration {

	/*
	bonita.serverformurl=https://salute.regione.veneto.it
	bonita.serverformapplicationname=auac-bpm
	
	bonita.sessioncookiedomain=regione.veneto.it
	bonita.sessioncookiename=JSESSIONID
	*/
	
	@Value("${bonita.serverurl}")
	private String serverUrl;

	@Value("${bonita.applicationname}")
	private String applicationName;

	@Value("${bonita.admin.username}")
	private String adminUsername;

	@Value("${bonita.admin.password}")
	private String adminPassword;

	@Value("${bonita.users.password}")
	private String usersPassword;

	@Value("${bonita.bonitaviewserverurl}")
	private String bonitaViewServerUrl;

	//IBonitaAPIWrapper bonitaAPIWrapper
    @Bean
    public IBonitaAPIWrapper bonitaAPIWrapper() throws Exception {
    	IBonitaAPIWrapper bonitaAPIWrapper = new Bonita7APIWrapper(serverUrl, applicationName, adminUsername, adminPassword);
        return bonitaAPIWrapper;
    }
}
