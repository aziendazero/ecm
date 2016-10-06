package it.tredi.ecm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import it.tredi.ecm.service.bean.EcmProperties;

@Configuration
@EntityScan(basePackages={"it.tredi.ecm.dao.entity","it.tredi.springdatautil"})
@EnableTransactionManagement
public class EcmAppConfiguration {
	@Value("${account.expires.days}")
	private int accountExpiresDay = 10;
	@Value("${file.multipart.maxFileSize}")
	private int multipartMaxFileSize = 2;
	@Value("${seduta.validation.minutes}")
	private int sedutaValidationMinutes = 30;
	@Value("${application.baseurl}")
	private String applicationBaseUrl = "http://localhost:8080/";
	@Value("${email.segreteriaEcm}")
	private String emailSegreteriaEcm = "segreteria@ecm.it";
	@Value("${debugTestMode}")
	private boolean debugTestMode = false;
	@Value("${giorni.integrazione.min}")
	private int giorniIntegrazioneMin = 5;
	@Value("${giorni.integrazione.max}")
	private int giorniIntegrazioneMax = 20;
	@Value("${numero.referee}")
	private int numeroReferee = 3;

	@Bean
	public EcmProperties ecmProperties(){
		EcmProperties ecmProperties = new EcmProperties();
		ecmProperties.setAccountExpiresDay(accountExpiresDay);
		ecmProperties.setMultipartMaxFileSize(multipartMaxFileSize*1024*1024);
		ecmProperties.setSedutaValidationMinutes(sedutaValidationMinutes);
		ecmProperties.setApplicationBaseUrl(applicationBaseUrl);
		ecmProperties.setEmailSegreteriaEcm(emailSegreteriaEcm);
		ecmProperties.setDebugTestMode(debugTestMode);
		ecmProperties.setGiorniIntegrazioneMin(giorniIntegrazioneMin);
		ecmProperties.setGiorniIntegrazioneMax(giorniIntegrazioneMax);
		ecmProperties.setNumeroReferee(numeroReferee);
		return ecmProperties;
	}
}
