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
	@Value("${giorni.min.evento.provider.A}")
	private int giorniMinEventoProviderA = 15;
	@Value("${giorni.min.evento.provider.B}")
	private int giorniMinEventoProviderB = 30;
	@Value("${giorni.min.evento.riedizione}")
	private int giorniMinEventoRiedizione = 10;
	@Value("${numero.massimo.responsabili.evento}")
	private int numeroMassimoResponsabiliEvento = 3;
	@Value("${giorni.max.evento.fsc}")
	private int giorniMaxEventoFSC = 730;
	@Value("${giorni.max.evento.fad}")
	private int giorniMaxEventoFAD = 365;
	@Value("${numero.minimo.partecipanti.convegno.congresso.res}")
	private int numeroMinimoPartecipantiConvegnoCongressoRES = 200;
	@Value("${numero.massimo.partecipanti.workshop.seminario.res}")
	private int numeroMassimoPartecipantiWorkshopSeminarioRES = 100;
	@Value("${numero.massimo.partecipanti.corso.aggiornamento.res}")
	private int numeroMassimoPartecipantiCorsoAggiornamentoRES = 200;
	@Value("${numero.massimo.partecipanti.gruppi.miglioramento.fsc}")
	private int numeroMassimoPartecipantiGruppiMiglioramentoFSC = 25;
	@Value("${numero.massimo.partecipanti.audit.clinico.fsc}")
	private int numeroMassimoPartecipantiAuditClinicoFSC = 25;
	@Value("${durata.minima.evento.res}")
	private long durataMinimaEventoRES = 3L;
	@Value("${durata.minima.audit.clinico.fsc}")
	private long durataMinimaAuditClinicoFSC = 10L;
	@Value("${durata.minima.gruppi.miglioramento.fsc}")
	private long durataMinimaGruppiMiglioramentoFSC = 8L;
	@Value("${durata.minima.progetti.miglioramento.fsc}")
	private long durataMinimaProgettiMiglioramentoFSC = 8L;
	@Value("${giorni.prima.blocco.edit.riedizione}")
	private int giorniPrimaBloccoEditRiedizione = 4;
	@Value("${giorni.prima.blocco.edit.gruppoA}")
	private int giorniPrimaBloccoEditGruppoA = 4;
	@Value("${giorni.prima.blocco.edit.gruppoB}")
	private int giorniPrimaBloccoEditGruppoB = 10;

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
		ecmProperties.setGiorniMinEventoProviderA(giorniMinEventoProviderA);
		ecmProperties.setGiorniMinEventoProviderB(giorniMinEventoProviderB);
		ecmProperties.setGiorniMinEventoRiedizione(giorniMinEventoRiedizione);
		ecmProperties.setNumeroMassimoResponsabiliEvento(numeroMassimoResponsabiliEvento);
		ecmProperties.setGiorniMaxEventoFSC(giorniMaxEventoFSC);
		ecmProperties.setGiorniMaxEventoFAD(giorniMaxEventoFAD);
		ecmProperties.setNumeroMinimoPartecipantiConvegnoCongressoRES(numeroMinimoPartecipantiConvegnoCongressoRES);
		ecmProperties.setNumeroMassimoPartecipantiWorkshopSeminarioRES(numeroMassimoPartecipantiWorkshopSeminarioRES);
		ecmProperties.setNumeroMassimoPartecipantiCorsoAggiornamentoRES(numeroMassimoPartecipantiCorsoAggiornamentoRES);
		ecmProperties.setNumeroMassimoPartecipantiGruppiMiglioramentoFSC(numeroMassimoPartecipantiGruppiMiglioramentoFSC);
		ecmProperties.setNumeroMassimoPartecipantiAuditClinicoFSC(numeroMassimoPartecipantiAuditClinicoFSC);
		ecmProperties.setDurataMinimaEventoRES(durataMinimaEventoRES);
		ecmProperties.setDurataMinimaAuditClinicoFSC(durataMinimaAuditClinicoFSC);
		ecmProperties.setDurataMinimaGruppiMiglioramentoFSC(durataMinimaGruppiMiglioramentoFSC);
		ecmProperties.setDurataMinimaProgettiMiglioramentoFSC(durataMinimaProgettiMiglioramentoFSC);
		ecmProperties.setGiorniPrimaBloccoEditRiedizione(giorniPrimaBloccoEditRiedizione);
		ecmProperties.setGiorniPrimaBloccoEditGruppoA(giorniPrimaBloccoEditGruppoA);
		ecmProperties.setGiorniPrimaBloccoEditGruppoB(giorniPrimaBloccoEditGruppoB);
		return ecmProperties;
	}
}
