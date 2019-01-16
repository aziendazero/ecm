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
	private boolean debugBrokeProtocollo;
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

	// ERM012514
	private int RelazioneAnnualeGiornoPeriodoNuovo;
	private int RelazioneAnnualeMesePeriodoNuovo;

	// ERM014776
	private int AccreditamentoNumeroGiorniDopoChiusura;

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

	// EVENTO_VERSIONE
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

	// ERM012514 - 01/01-30/06 anno scorso; 01/07 - 31/12 anno corrente
	public int getAnnoDiRiferimentoRA_rispettoDataCorrente() {

		// calcolo con anno corente poi sposto in dietro, cosi risparmio operazioni
		int currentYear = LocalDate.now().getYear();
		if (LocalDate.now().isAfter(LocalDate.of(currentYear, this.getRelazioneAnnualeMesePeriodoNuovo(),
				this.getRelazioneAnnualeGiornoPeriodoNuovo()))) {
			++currentYear;
		}
		// anno di riferimento va un anno in dietro
		return currentYear - 1;
	}

	// ERM014776
	public LocalDate espandiDataPerGiorniChiusura(LocalDate d) {
		return d.plusDays(getAccreditamentoNumeroGiorniDopoChiusura());
	}

}
