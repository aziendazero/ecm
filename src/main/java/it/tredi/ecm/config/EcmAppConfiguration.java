package it.tredi.ecm.config;

import java.io.File;

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
	@Value("${fileRootPath}")
	private String fileRootPath = System.getProperty("catalina.home") + File.separator + "ecmFiles";
	@Value("${giorni.min.evento.provider.A}")
	private int giorniMinEventoProviderA = 15;
	@Value("${giorni.min.evento.provider.B}")
	private int giorniMinEventoProviderB = 30;
	@Value("${numero.massimo.responsabili.evento}")
	private int numeroMassimoResponsabiliEvento = 3;

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
		ecmProperties.setFileRootPath(fileRootPath);
		ecmProperties.setGiorniMinEventoProviderA(giorniMinEventoProviderA);
		ecmProperties.setGiorniMinEventoProviderB(giorniMinEventoProviderB);
		ecmProperties.setNumeroMassimoResponsabiliEvento(numeroMassimoResponsabiliEvento);
		return ecmProperties;
	}
}
