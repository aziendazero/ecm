package it.tredi.ecm.config;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;
import it.tredi.ecm.service.bean.EcmProperties;

@Configuration
@EntityScan(basePackages={"it.tredi.ecm.dao.entity","it.tredi.springdatautil"})
@EnableTransactionManagement
public class EcmAppConfiguration {
	@Value("${account.expires.days}")
	private int accountExpiresDay = 10;
	@Value("${file.multipart.maxFileSize}")
	private int multipartMaxFileSize = 2;
	@Value("${file.multipart.maxFileSize4MB}")
	private int multipartMaxFileSize4MB = 4;
	@Value("${seduta.validation.minutes}")
	private int sedutaValidationMinutes = 30;
	@Value("${application.baseurl}")
	private String applicationBaseUrl = "http://localhost:8080/";
	@Value("${email.segreteriaEcm}")
	private String emailSegreteriaEcm = "segreteria@ecm.it";
	@Value("${debugTestMode}")
	private boolean debugTestMode = false;
	@Value("${debugSaltaProtocollo}")
	private boolean debugSaltaProtocollo = false;
	@Value("${giorni.integrazione.min}")
	private int giorniIntegrazioneMin = 5;
	@Value("${giorni.integrazione.max}")
	private int giorniIntegrazioneMax = 30;
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

	@Value("${giorni.max.evento.fsc.versione2}")
	private int giorniMaxEventoFscVersione2 = 365;
	@Value("${giorni.max.evento.fsc.versione2.attivitaDiRicerca}")
	private int giorniMaxEventoFscVersione2AttivitaDiRicerca = 730;
	
	
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
	@Value("${task.sendAlertEmail}")
	private boolean taskSendAlertEmail = false;
	@Value("${pianoFormativo.giornoFineModifica}")
	private int pianoFormativoGiornoFineModifica = 15;
	@Value("${pianoFormativo.meseFineModifica}")
	private int pianoFormativoMeseFineModifica = 12;
	@Value("${relazioneAnnuale.giornoFineModifica}")
	private int relazioneAnnualeGiornoFineModifica = 30;
	@Value("${relazioneAnnuale.meseFineModifica}")
	private int relazioneAnnualeMeseFineModifica = 4;
	@Value("${valutazioni.non.date.limite}")
	private int valutazioniNonDateLimit = 3;
	@Value("${giorni.variazione.dati.accreditamento}")
	private int giorniVariazioneDatiAccreditamento = 10;

	@Value("${http.proxy.protocol}")
	private String proxyProtocol = "http";
	@Value("${http.proxy.host}")
	private String proxyHost = "";
	@Value("${http.proxy.port}")
	private int proxyPort = 8000;
	@Value("${http.proxy.authenticated}")
	private boolean proxyAuthenticated = false;
	@Value("${http.proxy.username}")
	private String proxyUsername = "";
	@Value("${http.proxy.password}")
	private String proxyPassword = "";
	
	@Value("${conteggioGiorniAvanzatoAbilitato}")
	private boolean conteggioGiorniAvanzatoAbilitato;
	@Value("${conteggioGiorniAvanzatoBeforeDayMode}")
	private boolean conteggioGiorniAvanzatoBeforeDayMode;

	@Value("#{T(it.tredi.ecm.dao.enumlist.EventoVersioneEnum).getByNumeroVersione(${evento.numeroversione.default})}")
	private EventoVersioneEnum eventoVersioneDefault = EventoVersioneEnum.DUE_DAL_2018;
	@Value("#{T(java.time.LocalDate).parse(\"${evento.data.passaggio.versione.due}\", T(java.time.format.DateTimeFormatter).ofPattern(\"yyyyMMdd\"))}")
	private LocalDate eventoDataPassaggioVersioneDue = LocalDate.of(2018, 1, 1);

	@Value("#{T(it.tredi.ecm.dao.enumlist.EventoVersioneEnum).getSetByNumeroVersioni('${evento.versioni.rieditabili}'.split(','))}")
	private Set<EventoVersioneEnum> eventoVersioniRieditabili = new HashSet<EventoVersioneEnum>(Arrays.asList(EventoVersioneEnum.DUE_DAL_2018));
	
	@Value("#{T(java.time.LocalDate).parse(\"${evento.fad.data.fine.max.triennio}\", T(java.time.format.DateTimeFormatter).ofPattern(\"yyyyMMdd\"))}")
	private LocalDate eventoFadDataFineMaxTriennio = LocalDate.of(2019, 12, 31);
	@Value("#{T(java.time.LocalDate).parse(\"${evento.fsc.data.fine.max.triennio}\", T(java.time.format.DateTimeFormatter).ofPattern(\"yyyyMMdd\"))}")
	private LocalDate eventoFscDataFineMaxTriennio = LocalDate.of(2019, 12, 31);

	@Value("${numero.massimo.esperti.evento}")
	private int numeroMassimoEspertiEvento = 3;
	@Value("${numero.massimo.coordinatori.evento}")
	private int numeroMassimoCoordinatoriEvento = 3;

	@Value("${evento.lista.default.giorni.ultima.modifica}")
	private int numeroGiorniUltimaModificaEvento = 365;

	@Value("${giorni.possibilita.posticipo.da.inizio.evento.provider.A}")
	private int giorniPossibilitaPosticipoDaInizioEventoProviderA = 4;
	@Value("${giorni.possibilita.posticipo.da.inizio.evento.provider.B}")
	private int giorniPossibilitaPosticipoDaInizioEventoProviderB = 10;
	
	@Bean
	public EcmProperties ecmProperties(){
		EcmProperties ecmProperties = new EcmProperties();
		ecmProperties.setAccountExpiresDay(accountExpiresDay);
		ecmProperties.setMultipartMaxFileSize(multipartMaxFileSize*1024*1024);
		ecmProperties.setMultipartMaxFileSize4MB(multipartMaxFileSize4MB*1024*1024);
		ecmProperties.setSedutaValidationMinutes(sedutaValidationMinutes);
		ecmProperties.setApplicationBaseUrl(applicationBaseUrl);
		ecmProperties.setEmailSegreteriaEcm(emailSegreteriaEcm);
		ecmProperties.setDebugTestMode(debugTestMode);
		ecmProperties.setDebugSaltaProtocollo(debugSaltaProtocollo);
		ecmProperties.setGiorniIntegrazioneMin(giorniIntegrazioneMin);
		ecmProperties.setGiorniIntegrazioneMax(giorniIntegrazioneMax);
		ecmProperties.setNumeroReferee(numeroReferee);
		ecmProperties.setGiorniMinEventoProviderA(giorniMinEventoProviderA);
		ecmProperties.setGiorniMinEventoProviderB(giorniMinEventoProviderB);
		ecmProperties.setGiorniMinEventoRiedizione(giorniMinEventoRiedizione);
		ecmProperties.setNumeroMassimoResponsabiliEvento(numeroMassimoResponsabiliEvento);
		ecmProperties.setGiorniMaxEventoFSC(giorniMaxEventoFSC);
		ecmProperties.setGiorniMaxEventoFscVersione2(giorniMaxEventoFscVersione2);
		ecmProperties.setGiorniMaxEventoFscVersione2AttivitaDiRicerca(giorniMaxEventoFscVersione2AttivitaDiRicerca);
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
		ecmProperties.setTaskSendAlertEmail(taskSendAlertEmail);
		ecmProperties.setPianoFormativoGiornoFineModifica(pianoFormativoGiornoFineModifica);
		ecmProperties.setPianoFormativoMeseFineModifica(pianoFormativoMeseFineModifica);
		ecmProperties.setRelazioneAnnualeGiornoFineModifica(relazioneAnnualeGiornoFineModifica);
		ecmProperties.setRelazioneAnnualeMeseFineModifica(relazioneAnnualeMeseFineModifica);
		ecmProperties.setValutazioniNonDateLimit(valutazioniNonDateLimit);
		ecmProperties.setGiorniVariazioneDatiAccreditamento(giorniVariazioneDatiAccreditamento);

		ecmProperties.setProxyProtocol(proxyProtocol);
		ecmProperties.setProxyHost(proxyHost);
		ecmProperties.setProxyPort(proxyPort);
		ecmProperties.setProxyAuthenticated(proxyAuthenticated);
		ecmProperties.setProxyUsername(proxyUsername);
		ecmProperties.setProxyPassword(proxyPassword);

		ecmProperties.setConteggioGiorniAvanzatoAbilitato(conteggioGiorniAvanzatoAbilitato);
		ecmProperties.setConteggioGiorniAvanzatoBeforeDayMode(conteggioGiorniAvanzatoBeforeDayMode);
		ecmProperties.setEventoVersioneDefault(eventoVersioneDefault);
		ecmProperties.setEventoDataPassaggioVersioneDue(eventoDataPassaggioVersioneDue);
		ecmProperties.setEventoVersioniRieditabili(eventoVersioniRieditabili);
		ecmProperties.setEventoFadDataFineMaxTriennio(eventoFadDataFineMaxTriennio);
		ecmProperties.setEventoFscDataFineMaxTriennio(eventoFscDataFineMaxTriennio);
		
		ecmProperties.setNumeroMassimoEspertiEvento(numeroMassimoEspertiEvento);
		ecmProperties.setNumeroMassimoCoordinatoriEvento(numeroMassimoCoordinatoriEvento);

		ecmProperties.setNumeroGiorniUltimaModificaEvento(numeroGiorniUltimaModificaEvento);
		
		ecmProperties.setGiorniPossibilitaPosticipoDaInizioEventoProviderA(giorniPossibilitaPosticipoDaInizioEventoProviderA);
		ecmProperties.setGiorniPossibilitaPosticipoDaInizioEventoProviderB(giorniPossibilitaPosticipoDaInizioEventoProviderB);
		return ecmProperties;
	}

}
