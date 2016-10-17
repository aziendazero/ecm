package it.tredi.ecm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.tredi.ecm.cogeaps.CogeapsWsRestClient;

@Configuration
//@PropertySource("classpath:mail.properties")
public class CogeapsConfiguration {

    @Value("${cogeaps.protocol}")
    private String protocol;
    
    @Value("${cogeaps.host}")
    private String host;
    
    @Value("${cogeaps.port}")
    private int port;
    
    @Value("${cogeaps.rest_service.carica}")
    private String carica_service;
    
    @Value("${cogeaps.rest_service.stato_elaborazione}")
    private String stato_elaborazione_service;
    
    @Value("${cogeaps.username}")
    private String username;
    
    @Value("${cogeaps.password}")
    private String password;
    
    @Bean
    public CogeapsWsRestClient cogeapsWsRestClient() {
    	CogeapsWsRestClient cogeapsWsRestClient = new CogeapsWsRestClient();
    	cogeapsWsRestClient.setProtocol(protocol);
    	cogeapsWsRestClient.setHost(host);
    	cogeapsWsRestClient.setPort(port);
    	cogeapsWsRestClient.setCarica_service(carica_service);
    	cogeapsWsRestClient.setStato_elaborazione_service(stato_elaborazione_service);
    	cogeapsWsRestClient.setUsername(username);
    	cogeapsWsRestClient.setPassword(password);
    	return cogeapsWsRestClient;
    }

}
