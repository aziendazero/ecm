package it.tredi.ecm.service.bean;

import java.time.LocalDate;
import java.util.Set;

import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcmProperties {
	private int accountExpiresDay;
	private int multipartMaxFileSize;
	private int multipartMaxFileSize4MB;
	private int sedutaValidationMinutes;
	private String applicationBaseUrl;
	private String emailSegreteriaEcm;
	private boolean debugTestMode;
	private boolean debugSaltaProtocollo;
	private boolean debugSaltaScheduledTasks;
	private int giorniIntegrazioneMin;
	private int giorniIntegrazioneMax;
	private int numeroReferee;
	private int giorniMinEventoProviderA;
	private int giorniMinEventoProviderB;
	private int giorniMinEventoRiedizione;
	private int numeroMassimoResponsabiliEvento;
	private int giorniMaxEventoFSC;

	private int giorniMaxEventoFscVersione2;
	private int giorniMaxEventoFscVersione2AttivitaDiRicerca;
	
	private long giorniMaxEventoFAD;
	private int numeroMinimoPartecipantiConvegnoCongressoRES;
	private int numeroMassimoPartecipantiWorkshopSeminarioRES;
	private int numeroMassimoPartecipantiCorsoAggiornamentoRES;
	private int numeroMassimoPartecipantiGruppiMiglioramentoFSC;
	private int numeroMassimoPartecipantiAuditClinicoFSC;
	private long durataMinimaEventoRES;
	private long durataMinimaAuditClinicoFSC;
	private long durataMinimaGruppiMiglioramentoFSC;
	private long durataMinimaProgettiMiglioramentoFSC;
	private int giorniPrimaBloccoEditRiedizione;
	private int giorniPrimaBloccoEditGruppoA;
	private int giorniPrimaBloccoEditGruppoB;
	private boolean taskSendAlertEmail;
	private int pianoFormativoGiornoFineModifica;
	private int pianoFormativoMeseFineModifica;
	private int RelazioneAnnualeGiornoFineModifica;
	private int RelazioneAnnualeMeseFineModifica;
	private int valutazioniNonDateLimit;
	private int giorniVariazioneDatiAccreditamento;
	private String proxyProtocol;
	private String proxyHost;
	private int proxyPort;
	private boolean proxyAuthenticated;
	private String proxyUsername;
	private String proxyPassword;
	private boolean conteggioGiorniAvanzatoAbilitato;
	private boolean conteggioGiorniAvanzatoBeforeDayMode;
	private EventoVersioneEnum eventoVersioneDefault;
	private LocalDate eventoDataPassaggioVersioneDue;
	private Set<EventoVersioneEnum> eventoVersioniRieditabili;
	
	private LocalDate eventoFadDataFineMaxTriennio;
	private LocalDate eventoFscDataFineMaxTriennio;
	
	private int numeroMassimoEspertiEvento;
	private int numeroMassimoCoordinatoriEvento;
	
	private int numeroGiorniUltimaModificaEvento;
	
	private int giorniPossibilitaPosticipoDaInizioEventoProviderA = 4;
	private int giorniPossibilitaPosticipoDaInizioEventoProviderB = 10;
	
}
