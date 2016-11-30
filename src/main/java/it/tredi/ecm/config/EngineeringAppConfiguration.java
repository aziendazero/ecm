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
	@Value("${tipo.dovuto.evento}")
	private String tipoDovutoEvento = "";
	@Value("${tipo.dovuto.quotaannua}")
	private String tipoDovutoQuotaAnnua = "";
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
	@Value("${causale.length}")
	private int causaleLength = 140;

	@Value("${firma.url}")
	private String firmaUrl = "";
	@Value("${firma.idclassificazione}")
	private String firmaIdclassificazione = "";
	@Value("${firma.referer}")
	private String firmaReferer = "";

	@Bean
	public EngineeringProperties engineeringProperties(){
		EngineeringProperties engineeringProperties = new EngineeringProperties();

		engineeringProperties.setIpa(ipa);
		engineeringProperties.setPassword(password);
		engineeringProperties.setServizio(servizio);
		engineeringProperties.setEndpointPagamenti(endpointPagamenti);
		engineeringProperties.setDatiSpecificiRiscossione(datiSpecificiRiscossione);
		engineeringProperties.setTipoDovutoEvento(tipoDovutoEvento);
		engineeringProperties.setTipoDovutoQuotaAnnua(tipoDovutoQuotaAnnua);
		engineeringProperties.setUseProxy(useProxy);
		engineeringProperties.setProxyHost(proxyHost);
		engineeringProperties.setProxyPort(proxyPort);
		engineeringProperties.setProxyUsername(proxyUsername);
		engineeringProperties.setProxyPassword(proxyPassword);
		engineeringProperties.setCausaleLength(causaleLength);

		engineeringProperties.setFirmaUrl(firmaUrl);
		engineeringProperties.setFirmaIdclassificazione(firmaIdclassificazione);
		engineeringProperties.setFirmaReferer(firmaReferer);

		return engineeringProperties;
	}
}
