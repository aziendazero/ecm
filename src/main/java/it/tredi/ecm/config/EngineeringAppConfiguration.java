package it.tredi.ecm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import it.tredi.ecm.service.bean.EngineeringProperties;

@Configuration
@EntityScan(basePackages={"it.tredi.ecm.dao.entity","it.tredi.springdatautil"})
@EnableTransactionManagement
public class EngineeringAppConfiguration {
	@Value("${ipa}")
	private String ipa = "";
	@Value("${password}")
	private String password = "";
	@Value("${servizio}")
	private String servizio = "";
	@Value("${endpoint.pagamenti}")
	private String endpointPagamenti = "";
	@Value("${datispecifici.riscossione}")
	private String datiSpecificiRiscossione = "";
	@Value("${tipo.dovuti}")
	private String tipoDovuti = "";
	@Value("${proxy.attivo}")
	private boolean useProxy = false;
	@Value("${proxy.host}")
	private String proxyHost = "";
	@Value("${proxy.port}")
	private String proxyPort = "";
	@Value("${proxy.username}")
	private String proxyUsername = "";
	@Value("${proxy.password}")
	private String proxyPassword = "";

	@Bean
	public EngineeringProperties engineeringProperties(){
		EngineeringProperties engineeringProperties = new EngineeringProperties();
		
		engineeringProperties.setIpa(ipa);
		engineeringProperties.setPassword(password);
		engineeringProperties.setServizio(servizio);
		engineeringProperties.setEndpointPagamenti(endpointPagamenti);
		engineeringProperties.setDatiSpecificiRiscossione(datiSpecificiRiscossione);
		engineeringProperties.setTipoDovuti(tipoDovuti);
		engineeringProperties.setUseProxy(useProxy);
		engineeringProperties.setProxyHost(proxyHost);
		engineeringProperties.setProxyPort(proxyPort);
		engineeringProperties.setProxyUsername(proxyUsername);
		engineeringProperties.setProxyPassword(proxyPassword);
		
		return engineeringProperties;
	}
}
