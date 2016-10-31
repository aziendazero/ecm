package it.tredi.ecm.web.validator;

import java.time.LocalDate;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.Partner;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.entity.PersonaFullEvento;
import it.tredi.ecm.dao.entity.Sponsor;
import it.tredi.ecm.dao.entity.VerificaApprendimentoFAD;
import it.tredi.ecm.dao.enumlist.ContenutiEventoEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.dao.enumlist.VerificaApprendimentoRESEnum;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoRESProgrammaGiornalieroWrapper;
import it.tredi.ecm.web.bean.EventoRESTipoDataProgrammaGiornalieroEnum;
import it.tredi.ecm.web.bean.EventoWrapper;

@Component
public class EventoValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventoValidator.class);

	@Autowired private EcmProperties ecmProperties;

	public void validate(Object target, EventoWrapper wrapper, Errors errors, String prefix){
		Evento evento = (Evento) target;
		validateCommon(evento, errors, prefix);

		if (evento instanceof EventoRES)
			validateRES(((EventoRES) evento), wrapper, errors, prefix);
		else if (evento instanceof EventoFSC)
			validateFSC(((EventoFSC) evento), errors, prefix);
		else if (evento instanceof EventoFAD)
			validateFAD(((EventoFAD) evento), errors, prefix);

		Utils.logDebugErrorFields(LOGGER, errors);

	}

	//validate delle parti in comune
	private void validateCommon(Evento evento, Errors errors, String prefix) {

		/* DESTINATARI EVENTO (campo obbligatorio)
		 * checkbox -> almeno un valore selezionato
		 * */
		if(evento.getDestinatariEvento() == null || evento.getDestinatariEvento().isEmpty())
			errors.rejectValue(prefix + "destinatariEvento", "error.empty");

		/* CONTENUTI EVENTO (campo obbligatorio)
		 * radio
		 * */
		if(evento.getContenutiEvento() == null)
			errors.rejectValue(prefix + "contenutiEvento", "error.empty");

		/* TITOLO (campo obbligatorio)
		 * campo testuale libero
		 * almeno 1 char
		 * */
		if(evento.getTitolo() == null || evento.getTitolo().isEmpty())
			errors.rejectValue(prefix + "titolo", "error.empty");

		/* DATA INIZIO (campo obbligatorio)
		 * un evento può essere inserito fino a 15 giorni dalla data di inizio per il gruppo A, 30 per il gruppo B
		 * */
		int minGiorni;
		if(evento.getProvider().getTipoOrganizzatore().getGruppo().equals("A"))
			minGiorni = ecmProperties.getGiorniMinEventoProviderA();
		else minGiorni = ecmProperties.getGiorniMinEventoProviderB();

		if(evento.getDataInizio() == null)
			errors.rejectValue(prefix + "dataInizio", "error.empty");
		else if(evento.getDataInizio().isBefore(LocalDate.now().plusDays(minGiorni)))
			errors.rejectValue(prefix + "dataInizio", "error.data_inizio_non_valida");

		/* OBIETTIVO FORMATIVO NAZIONALE (campo obbligatorio)
		 * selectpicker
		 * */
		if(evento.getObiettivoNazionale() == null)
			errors.rejectValue(prefix + "obiettivoNazionale", "error.empty");

		/* OBIETTIVO FORMATIVO REGIONALE (campo obbliagatorio)
		 * selectpicker
		 * */
		if(evento.getObiettivoRegionale() == null)
			errors.rejectValue(prefix + "obiettivoRegionale", "error.empty");

		/* PROFESSIONI/DISCIPLINE (campo obbligatorio)
		 * selectpicker (sono le discipline selezionate a creare le relative professioni)
		 * */
		if(evento.getDiscipline() == null || evento.getDiscipline().isEmpty())
			errors.rejectValue(prefix + "discipline", "error.empty");

		/* RESPONSABILI SCIENTIFICI (serie di campi obbligatori)
		 * ripetibile complesso di classe PersonaEvento
		 * minimo 1 - massimo 3
		 * devono avere tutti i campi inseriti
		 * */
		if(evento.getResponsabili() == null || evento.getResponsabili().isEmpty())
			errors.rejectValue(prefix + "responsabili", "error.empty");
		else if(evento.getResponsabili().size() > ecmProperties.getNumeroMassimoResponsabiliEvento())
				errors.rejectValue(prefix + "responsabili", "error.troppi_responsabili3");
		else {
			int counter = 0;
			boolean atLeastOneErrorPersonaEvento = false;
			for(PersonaEvento p : evento.getResponsabili()) {
				boolean hasError = validatePersonaEvento(p, "responsabile");
				if(hasError) {
					errors.rejectValue("responsabiliScientifici["+counter+"]", "error.campi_mancanti_persona");
					atLeastOneErrorPersonaEvento = true;
				}
				counter++;
			}
			if(atLeastOneErrorPersonaEvento)
				errors.rejectValue(prefix + "responsabili", "error.campi_mancanti_responsabili");
		}

		/* RESPONSABILE SEGRETERIA ORGANIZZATIVA (campo obbligatorio)
		 * singolo campo complesso di tipo PersonaFullEvento
		 * deve avere tutti i campi inseriti
		 * */
		if(evento.getResponsabileSegreteria() == null || evento.getResponsabileSegreteria().getAnagrafica() == null)
			errors.rejectValue(prefix + "responsabileSegreteria", "error.empty");
		else {
			boolean hasError = validatePersonaFullEvento(evento.getResponsabileSegreteria());
			if(hasError)
				errors.rejectValue(prefix + "responsabileSegreteria", "error.campi_mancanti_responsabile_segreteria");
		}

		/* RADIO EVENTO SPONSORIZZATO (campo obbligatorio)
		 * radio
		 * */
		if(evento.getEventoSponsorizzato() == null)
			errors.rejectValue(prefix + "eventoSponsorizzato", "error.empty");

		/* SPONSOR (campo obbligatorio)
		 * campo complesso ripetibile di tipo Sponsor
		 * deve avere tutti i campi inseriti
		 * se eventoSponsorizzato == true -> almeno 1 sponsor
		 * */
		if(evento.getEventoSponsorizzato() != null
				&& evento.getEventoSponsorizzato() == true
				&& (evento.getSponsors() == null
				|| evento.getSponsors().isEmpty()))
			errors.rejectValue(prefix + "sponsors", "error.empty");
		else {
			int counter = 0;
			boolean atLeastOneErrorSponsor = false;
			for(Sponsor s : evento.getSponsors()) {
				boolean hasError = validateSponsor(s);
				if(hasError) {
					errors.rejectValue("sponsors["+counter+"]", "error.campi_mancanti_sponsor");
					atLeastOneErrorSponsor = true;
				}
				counter++;
			}
			if(atLeastOneErrorSponsor)
				errors.rejectValue(prefix + "sponsors", "error.campi_mancanti_sponsor");
		}

		/* RADIO EVENTO SPONSORIZZATO DA AZIENDE CHE TRATTANO ALIMENTI PRIMA INFANZIA
		 * (campo obbligatorio se contenutiEvento == ALIMENTAZIONE_PRIMA_INFANZIA)
		 * radio
		 * */
		if(evento.getContenutiEvento() != null
				&& evento.getContenutiEvento() == ContenutiEventoEnum.ALIMENTAZIONE_PRIMA_INFANZIA
				&& evento.getEventoSponsorizzato() != null
				&& evento.getEventoSponsorizzato() == true
				&& evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() == null)
			errors.rejectValue(prefix + "eventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia", "error.empty");

		/* AUTOCERTIFICAZIONE ASSENZA SPONSOR PRIMA INFANZIA
		 * (campo obbligatorio se contenutiEvento == ALIMENTAZIONE_PRIMA_INFANZIA
		 * e eventoSponsorizzatoDaAziendeAlimentiPrimainfanzia == true)
		 * file allegato
		 * */
		if(evento.getContenutiEvento() != null
				&& evento.getContenutiEvento() == ContenutiEventoEnum.ALIMENTAZIONE_PRIMA_INFANZIA
				&& evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() != null
				&& evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() == false
				&& evento.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia() == null)
			errors.rejectValue("autocertificazioneAssenzaAziendeAlimentiPrimaInfanzia", "error.empty");

		/* AUTOCERTIFICAZIONE DI AUTORIZZAZIONE DEL MINISTERO
		 * (campo obbligatorio se contenutiEvento == ALIMENTAZIONE_PRIMA_INFANZIA
		 * e eventoSponsorizzatoDaAziendeAlimentiPrimainfanzia == false)
		 * file allegato
		 * */
		if(evento.getContenutiEvento() != null
				&& evento.getContenutiEvento() == ContenutiEventoEnum.ALIMENTAZIONE_PRIMA_INFANZIA
				&& evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() != null
				&& evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() == true
				&& evento.getAutocertificazioneAutorizzazioneMinisteroSalute() == null)
			errors.rejectValue("autocertificazioneAutorizzazioneMinisteroSalute", "error.empty");

		/* RADIO ALTRE FORME FINANZIAMENTO (campo obbligatorio)
		 * radio
		 * */
		if(evento.getAltreFormeFinanziamento() == null)
			errors.rejectValue(prefix + "altreFormeFinanziamento", "error.empty");

		/* ALLEGATO CONTRATTI ACCORDI CONVENZIONI
		 * (campo obbligatorio se altreFormeFinanziamento == true)
		 * file allegato
		 * */
		if(evento.getAltreFormeFinanziamento() != null
				&& evento.getAltreFormeFinanziamento() == true
				&& evento.getContrattiAccordiConvenzioni() == null)
			errors.rejectValue("contrattiAccordiConvenzioni", "error.empty");

		/* AUTOCERTIFICAZIONE ASSENZA FINANZIAMENTI
		 * (campo obbligatorio se altreFormeFinanziamento == false)
		 * file allegato
		 * */
		if(evento.getAltreFormeFinanziamento() != null
				&& evento.getAltreFormeFinanziamento() == false
				&& evento.getAutocertificazioneAssenzaFinanziamenti() == null)
			errors.rejectValue("autocertificazioneAssenzaFinanziamenti", "error.empty");

		/* RADIO EVENTO PARTNER (campo obbligatorio)
		 * radio
		 * */
		if(evento.getEventoAvvalePartner() == null)
			errors.rejectValue(prefix + "eventoAvvalePartner", "error.empty");

		/* PARTNERS (campo obbligatorio se eventoAvvalePartner == true)
		 * campo complesso di tipo Partner ripetibile
		 * almeno 1
		 * */
		if(evento.getEventoAvvalePartner() != null
				&& evento.getEventoAvvalePartner() == true
				&& (evento.getPartners() == null
				|| evento.getPartners().isEmpty()))
			errors.rejectValue(prefix + "partners", "error.empty");
		else {
			int counter = 0;
			boolean atLeastOneErrorPartner = false;
			for(Partner p : evento.getPartners()) {
				boolean hasError = validatePartner(p);
				if(hasError) {
					errors.rejectValue("partners["+counter+"]", "error.campi_mancanti_partner");
					atLeastOneErrorPartner = true;
				}
				counter++;
			}
			if(atLeastOneErrorPartner)
				errors.rejectValue(prefix + "partners", "error.campi_mancanti_partner");
		}

		/* DICHIARAZIONE ASSENZA CONFLITTO DI INTERESSE (campo obbligatorio)
		 * file allegato
		 * */
		if(evento.getDichiarazioneAssenzaConflittoInteresse() == null)
			errors.rejectValue("dichiarazioneAssenzaConflittoInteresse", "error.empty");

		/* PROCEDURA VERIFICA QUALITÀ (campo obbligatorio)
		 * spunta richiesta
		 * */
		if(evento.getProceduraVerificaQualitaPercepita() == null || evento.getProceduraVerificaQualitaPercepita() == false)
			errors.rejectValue(prefix + "proceduraVerificaQualitaPercepita", "error.empty");

		/* AUTORIZZAZIONE PRIVACY (campo obbligatorio)
		 * spunta richiesta
		 * */
		if(evento.getAutorizzazionePrivacy() == null || evento.getAutorizzazionePrivacy() == false)
			errors.rejectValue(prefix + "autorizzazionePrivacy", "error.empty");
	}

	//validate RES
	private void validateRES(EventoRES evento, EventoWrapper wrapper, Errors errors, String prefix) {

		/* SEDE (tutti campi obbligatori)
		 * provincia da selezione, comune da selezione, almeno 1 char indirizzo, almeno 1 char luogo
		 * */
		if(evento.getSedeEvento() == null) {
			errors.rejectValue(prefix + "sedeEvento.provincia", "error.empty");
			errors.rejectValue(prefix + "sedeEvento.comune", "error.empty");
			errors.rejectValue(prefix + "sedeEvento.indirizzo", "error.empty");
			errors.rejectValue(prefix + "sedeEvento.luogo", "error.empty");
		}
		else {
			if(evento.getSedeEvento().getProvincia() == null || evento.getSedeEvento().getProvincia().isEmpty())
				errors.rejectValue(prefix + "sedeEvento.provincia", "error.empty");
			if(evento.getSedeEvento().getComune() == null || evento.getSedeEvento().getComune().isEmpty())
				errors.rejectValue(prefix + "sedeEvento.comune", "error.empty");
			if(evento.getSedeEvento().getIndirizzo() == null || evento.getSedeEvento().getIndirizzo().isEmpty())
				errors.rejectValue(prefix + "sedeEvento.indirizzo", "error.empty");
			if(evento.getSedeEvento().getLuogo() == null || evento.getSedeEvento().getLuogo().isEmpty())
				errors.rejectValue(prefix + "sedeEvento.luogo", "error.empty");
		}

		/* DATA FINE (campo obbligatorio)
		 * la data di fine deve essere compresa nello stesso anno solare della data di inizio
		 * */
		if(evento.getDataFine() == null)
			errors.rejectValue(prefix + "dataFine", "error.empty");
		else if(evento.getDataInizio() != null && (evento.getDataFine().isBefore(evento.getDataInizio())))
			errors.rejectValue(prefix + "dataFine", "error.data_fine_non_valida");
		else if(evento.getDataInizio() != null && (evento.getDataFine().getYear() != evento.getDataInizio().getYear()))
			errors.rejectValue(prefix + "dataFine", "error.data_fine_res_non_valida");

		/* DATE INTERMEDIE (campo opzionale)
		 * le date intermedie devono essere comprese tra quella di inizio e quella di fine
		 * */
		if(evento.getDataInizio() != null && evento.getDataFine() != null) {
			for (LocalDate ld : evento.getDateIntermedie()) {
				if(ld.isAfter(evento.getDataFine()) || ld.isBefore(evento.getDataInizio())) {
					//ciclo alla ricerca di questa data per farmi dare la chiave nella mappa
					Long key = -1L;
					for(Entry<Long, EventoRESProgrammaGiornalieroWrapper> entry : wrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().entrySet()) {
						if(entry.getValue().getTipoData() == EventoRESTipoDataProgrammaGiornalieroEnum.INTERMEDIA
								&& entry.getValue().getProgramma().getGiorno() != null && entry.getValue().getProgramma().getGiorno().isEqual(ld)) {
							key = entry.getKey();
							break;
						}
					}
					errors.rejectValue("eventoRESDateProgrammiGiornalieriWrapper.sortedProgrammiGiornalieriMap[" + key + "]", "error.data_intermedia_res_non_valida");
				}
			}
		}

		/* TIPOLOGIA EVENTO (campo obbligatorio)
		 * selectpicker (influenza altri campi, ma il controllo su questo campo è banale)
		 * */
		if(evento.getTipologiaEvento() == null)
			errors.rejectValue(prefix + "tipologiaEvento", "error.empty");

		/* WORKSHOP/SEMINARI (campo obbligatorio se TIPOLOGIA EVENTO == CONVEGNO_CONGRESSO)
		 * radio
		 * */
		if(evento.getTipologiaEvento() != null
				&& evento.getTipologiaEvento() == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO
				&& evento.getWorkshopSeminariEcm() == null)
			errors.rejectValue(prefix + "workshopSeminariEcm", "error.empty");

		/* TITOLO CONVEGNO (campo obbligatorio se TIPOLOGIA EVENTO == WORKSHOP_SEMINARIO)
		 * campo testuale libero
		 * almeno 1 char
		 * */
		if(evento.getTipologiaEvento() != null
				&& evento.getTipologiaEvento() == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO
				&& (evento.getTitoloConvegno() == null || evento.getTitoloConvegno().isEmpty()))
			errors.rejectValue(prefix + "titoloConvegno", "error.empty");

		/* NUMERO DEI PARTECIPANTI (campo obbligatorio)
		 * campo valore numerico
		 * se la tipologia dell'evento è CONVEGNO_CONGRESSO -> minimo 200 partecipanti
		 * se la tipologia dell'evento è WORKSHOP_SEMINARIO -> massimo 100 partecipanti
		 * se la tipologia dell'evento è CORSO_AGGIORNAMENTO -> massimo 200 partecipanti
		 * */
		if(evento.getNumeroPartecipanti() == null)
			errors.rejectValue(prefix + "numeroPartecipanti", "error.empty");
		else if(evento.getTipologiaEvento() != null
				&& evento.getTipologiaEvento() == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO
				&& evento.getNumeroPartecipanti() < ecmProperties.getNumeroMinimoPartecipantiConvegnoCongressoRES())
			errors.rejectValue(prefix + "numeroPartecipanti", "error.pochi_partecipanti200");
		else if(evento.getTipologiaEvento() != null
				&& evento.getTipologiaEvento() == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO
				&& evento.getNumeroPartecipanti() > ecmProperties.getNumeroMassimoPartecipantiWorkshopSeminarioRES())
			errors.rejectValue(prefix + "numeroPartecipanti", "error.troppi_partecipanti100");
		else if(evento.getTipologiaEvento() != null
				&& evento.getTipologiaEvento() == TipologiaEventoRESEnum.CORSO_AGGIORNAMENTO
				&& evento.getNumeroPartecipanti() > ecmProperties.getNumeroMassimoPartecipantiCorsoAggiornamentoRES())
			errors.rejectValue(prefix + "numeroPartecipanti", "error.troppi_partecipanti200");

		/* DOCENTI/RELATORI/TUTOR (serie di campi obbligatori)
		 * ripetibile complesso di classe PersonaEvento
		 * minimo 1
		 * devono avere tutti i campi inseriti
		 * */
		if(evento.getDocenti() == null || evento.getDocenti().isEmpty())
			errors.rejectValue(prefix + "docenti", "error.empty");
		else {
			int counter = 0;
			boolean atLeastOneErrorPersonaEvento = false;
			for(PersonaEvento p : evento.getDocenti()) {
				boolean hasError = validatePersonaEvento(p, "docente");
				if(hasError) {
					errors.rejectValue("docenti["+counter+"]", "error.campi_mancanti_persona");
					atLeastOneErrorPersonaEvento = true;
				}
				counter++;
			}
			if(atLeastOneErrorPersonaEvento)
				errors.rejectValue(prefix + "docenti", "error.campi_mancanti_docenti");
		}

		/* RAZIONALE (campo obbligatorio)
		 * campo testuale libero
		 * almeno 1 char
		 * */
		if(evento.getRazionale() == null || evento.getRazionale().isEmpty())
			errors.rejectValue(prefix + "razionale", "error.empty");

		/* RISULTATI ATTESI (campo obbligatorio)
		 * campo testuale libero ripetibile
		 * almeno 1 char
		 * almeno 1 elemento
		 * se non ci sono elementi, la input su cui inserire l'errore punterà al primo elemento della mappa
		 * */
		if(evento.getRisultatiAttesi() == null || evento.getRisultatiAttesi().isEmpty())
			errors.rejectValue("risultatiAttesiMapTemp[1]", "error.empty");

		/* PROGRAMMA RES (serie di campi obbligatori)
		 * ripetibile complesso di classe ProgrammaGiornalieroRES
		 * stesso numero delle date (gestito lato interfaccia)
		 * devono avere tutti i campi inseriti
		 * */
		//TODO aspettare barduz con la modifica alle date

		/* BROCHURE EVENTO (campo obbligatorio se TIPOLOGIA EVENTO == CONVEGNO_CONGRESSO o WORKSHOP_SEMINARIO)
		 * file allegato
		 * */
		if((evento.getTipologiaEvento() == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO
				|| evento.getTipologiaEvento() == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO)
				&& evento.getBrochureEvento() == null)
			errors.rejectValue("brochure", "error.empty");

		/* VERIFICA APPRENDIMENTO (campo obbligatorio)
		 * checkbox
		 * almeno un valore
		 * controllo coerenza se TIPOLOGIA EVENTO == CONVEGNO_CONGRESSO -> selezionabile solo AUTOCERTIFICAZIONE
		 * (anche se da client è tutto gestito)
		 * */
		if(evento.getVerificaApprendimento() == null || evento.getVerificaApprendimento().isEmpty())
			errors.rejectValue(prefix + "verificaApprendimento", "error.empty");
		else if ((evento.getTipologiaEvento() == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO
				&& !evento.getVerificaApprendimento().contains(VerificaApprendimentoRESEnum.AUTOCERTFICAZIONE))
				|| evento.getVerificaApprendimento().size() > 1)
			errors.rejectValue(prefix + "verificaApprendimento", "error.solo_autocertificazione_selezionabile");
		else if ((evento.getTipologiaEvento() != TipologiaEventoRESEnum.CONVEGNO_CONGRESSO)
				&& evento.getVerificaApprendimento().contains(VerificaApprendimentoRESEnum.AUTOCERTFICAZIONE))
			errors.rejectValue(prefix + "verificaApprendimento",  "error.autocertificazione_non_selezionabile");

		/* DURATA COMPLESSIVA (autocompilato)
		 * controllo di sicurezza, la durata totale dell'evento deve essere di più di 3 ore
		 * dato che ogni attività deve essere di minimo 3 ore, va da se che il controllo verrà sempre superato
		 * o segnalato nelle attività
		 * */
		if(evento.getDurata() < ecmProperties.getDurataMinimaEventoRES())
			errors.rejectValue(prefix + "durata", "error.durata_minima_complessiva_non_raggiunta");

		/* CREDITI (campo obbligatorio/autocompilato)
		 * il campo viene autocompilato
		 * a questo punto l'utente può scegliere di accettare il valore
		 * o inserirne uno lui -> se NON accetta il valore il campo crediti che deve inserire è obbligatorio
		 * campo numerico (Float)
		 * */
		if(evento.getCrediti() == null)
			errors.rejectValue(prefix + "crediti", "error.empty");

		/* QUOTA DI PARTECIPAZIONE (campo obbligatorio)
		 * campo numerico (BigDecimal)
		 * */
		if(evento.getQuotaPartecipazione() == null)
			errors.rejectValue(prefix + "quotaPartecipazione", "error.empty");

		/* LINGUA (campo obbligatorio)
		 * radio
		 * */
		if(evento.getSoloLinguaItaliana() == null)
			errors.rejectValue(prefix + "soloLinguaItaliana", "error.empty");

		/* LINGUA STRANIERA UTILIZZATA (campo obbligatorio se soloLinguaItaliana == false)
		 * campo testuale libero
		 * almeno 1 char
		 * */
		if(evento.getSoloLinguaItaliana() != null
				&& evento.getSoloLinguaItaliana() == false
				&& (evento.getLinguaStranieraUtilizzata() == null
				|| evento.getLinguaStranieraUtilizzata().isEmpty()))
			errors.rejectValue(prefix +  "linguaStranieraUtilizzata", "error.empty");

		/* TRADUZIONE SIMULTANEA (campo obbligatorio se soloLinguaItaliana == false)
		 * radio
		 * */
		if(evento.getSoloLinguaItaliana() != null
				&& evento.getSoloLinguaItaliana() == false
				&& evento.getEsisteTraduzioneSimultanea() == null)
			errors.rejectValue(prefix +  "esisteTraduzioneSimultanea", "error.empty");

		/* VERIFICA PRESENZA PARTECIPANTI (campo obbligatorio)
		 * checkbox
		 * almeno 1 selezionato
		 * */
		if(evento.getVerificaPresenzaPartecipanti() == null || evento.getVerificaPresenzaPartecipanti().isEmpty())
			errors.rejectValue(prefix + "verificaPresenzaPartecipanti", "error.empty");

		/* VERIFICA RICADUTE FORMATIVE (campo obbligatorio)
		 * radio
		 * */
		if(evento.getVerificaRicaduteFormative() == null)
			errors.rejectValue(prefix + "verificaRicaduteFormative", "error.empty");

	}

	//validate FSC
	private void validateFSC(EventoFSC evento, Errors errors, String prefix) {

		/* SEDE (tutti campi obbligatori)
		 * provincia da selezione, comune da selezione, almeno 1 char indirizzo, almeno 1 char luogo
		 * */
		if(evento.getSedeEvento() == null) {
			errors.rejectValue(prefix + "sedeEvento.provincia", "error.empty");
			errors.rejectValue(prefix + "sedeEvento.comune", "error.empty");
			errors.rejectValue(prefix + "sedeEvento.indirizzo", "error.empty");
			errors.rejectValue(prefix + "sedeEvento.luogo", "error.empty");
		}
		else {
			if(evento.getSedeEvento().getProvincia() == null || evento.getSedeEvento().getProvincia().isEmpty())
				errors.rejectValue(prefix + "sedeEvento.provincia", "error.empty");
			if(evento.getSedeEvento().getComune() == null || evento.getSedeEvento().getComune().isEmpty())
				errors.rejectValue(prefix + "sedeEvento.comune", "error.empty");
			if(evento.getSedeEvento().getIndirizzo() == null || evento.getSedeEvento().getIndirizzo().isEmpty())
				errors.rejectValue(prefix + "sedeEvento.indirizzo", "error.empty");
			if(evento.getSedeEvento().getLuogo() == null || evento.getSedeEvento().getLuogo().isEmpty())
				errors.rejectValue(prefix + "sedeEvento.luogo", "error.empty");
		}

		/* DATA FINE (campo obbligatorio)
		 * la data di fine deve può essere compresa nello stesso anno solare della data di inizio
		 * e l'evento non può avere durata superiore a 730 giorni
		 * */
		if(evento.getDataFine() == null)
			errors.rejectValue(prefix + "dataFine", "error.empty");
		else if(evento.getDataInizio() != null && (evento.getDataFine().isBefore(evento.getDataInizio())))
			errors.rejectValue(prefix + "dataFine", "error.data_fine_non_valida");
		else if(evento.getDataInizio() != null && evento.getDataFine().getYear() == evento.getDataInizio().getYear())
			errors.rejectValue(prefix + "dataFine", "error.data_fine_fsc_non_valida");
		else if(evento.getDataInizio() != null && evento.getDataFine().isAfter(evento.getDataInizio().plusDays(ecmProperties.getGiorniMaxEventoFSC())))
			errors.rejectValue(prefix + "dataFine", "error.numero_massimo_giorni_evento_fsc730");


		/* TIPOLOGIA EVENTO (campo obbligatorio)
		 * selectpicker (influenza altri campi, ma il controllo su questo campo è banale)
		 * */
		if(evento.getTipologiaEvento() == null)
			errors.rejectValue(prefix + "tipologiaEvento", "error.empty");

		/* TIPOLOGIA GRUPPO (campo obbligatorio se tipologiaEvento == GRUPPI_DI_MIGLIORAMENTO)
		 * selectpicker
		 * */
		if(evento.getTipologiaEvento() != null
				&& evento.getTipologiaEvento() == TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO
				&& evento.getTipologiaGruppo() == null)
			errors.rejectValue(prefix + "tipologiaGruppo", "error.empty");

		/* SPERIMENTAZIONE CLINICA (campo obbligatorio se tipologiaEvento == ATTIVITA_DI_RICERCA)
		 * radio
		 * */
		if(evento.getTipologiaEvento() != null
				&& evento.getTipologiaEvento() == TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA
				&& evento.getSperimentazioneClinica() == null)
			errors.rejectValue(prefix + "sperimentazioneClinica", "error.empty");

		/* OTTENUTO PARERE ETICO (campo obbligatorio se tipologiaEvento == ATTIVITA_DI_RICERCA && sperimentazioneClinica == true)
		 * spunta richiesta
		 * */
		if(evento.getTipologiaEvento() != null
				&& evento.getTipologiaEvento() == TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA
				&& evento.getSperimentazioneClinica() != null
				&& evento.getSperimentazioneClinica() == true
				&& (evento.getOttenutoComitatoEtico() == null
				|| evento.getOttenutoComitatoEtico() == false))
			errors.rejectValue(prefix + "ottenutoComitatoEtico", "error.empty");

		/* DESCRIZIONE DEL PROGETTO E RILEVANZA FORMATIVA (campo obbligatorio)
		 * campo testuale
		 * almeno 1 char
		 * */
		if(evento.getDescrizioneProgetto() == null || evento.getDescrizioneProgetto().isEmpty())
			errors.rejectValue(prefix + "descrizioneProgetto", "error.empty");

		/* FASI/AZIONI/RUOLI FSC
		 *
		 * */
		//TODO

		/* NUMERO PARTECIPANTI (campo obbligatorio)
		 * se tipologiaEvento == TRAINING_INDIVIDUALIZZATO massimo 5 partecipanti per tutor
		 * se tipologiaEvento == GRUPPI_DI_MIGLIORAMENTO massimo 25 partecipanti
		 * se tipologiaEvento == AUDIT_CLINICO_ASSISTENZIALE massimo 25 partecipanti
		 * */
		if(evento.getNumeroPartecipanti() == null)
			errors.rejectValue(prefix + "numeroPartecipanti", "error.empty");
		//TODO trovare modo per contare partecipanti / tutor
		else if(evento.getTipologiaEvento() != null
				&& evento.getTipologiaEvento() == TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO
				&& evento.getNumeroPartecipanti() > ecmProperties.getNumeroMassimoPartecipantiGruppiMiglioramentoFSC())
			errors.rejectValue(prefix + "numeroPartecipanti", "error.troppi_partecipanti25");
		else if(evento.getTipologiaEvento() != null
				&& evento.getTipologiaEvento() == TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE
				&& evento.getNumeroPartecipanti() > ecmProperties.getNumeroMassimoPartecipantiAuditClinicoFSC())
			errors.rejectValue(prefix + "numeroPartecipanti", "error.troppi_partecipanti25");

		/* DURATA COMPLESSIVA (autocompilato)
		 * controlli di sicurezza:
		 * - la durata totale dell'evento deve essere di più di 10 ore per AUDIT_CLINICO_ASSISTENZIALE
		 * - la durata totale dell'evento deve essere di più di 8 ore per GRUPPI_DI_MIGLIORAMENTO
		 * - la durata totale dell'evento deve essere di più di 8 ore per PROGETTI_DI_MIGLIORAMENTO
		 * */
		if(evento.getTipologiaEvento() != null) {
			if(evento.getTipologiaEvento() == TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE
				&& evento.getDurata() < ecmProperties.getDurataMinimaAuditClinicoFSC())
			errors.rejectValue(prefix + "durata", "error.durata_minima_complessiva_non_raggiunta");
			else if(evento.getTipologiaEvento() == TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO
					&& evento.getDurata() < ecmProperties.getDurataMinimaGruppiMiglioramentoFSC())
				errors.rejectValue(prefix + "durata", "error.durata_minima_complessiva_non_raggiunta");
			else if(evento.getTipologiaEvento() == TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO
					&& evento.getDurata() < ecmProperties.getDurataMinimaProgettiMiglioramentoFSC())
				errors.rejectValue(prefix + "durata", "error.durata_minima_complessiva_non_raggiunta");
		}

		/* VERIFICA PRESENZA PARTECIPANTI (campo obbligatorio)
		 * checkbox
		 * almeno 1 selezionato
		 * */
		if(evento.getVerificaPresenzaPartecipanti() == null || evento.getVerificaPresenzaPartecipanti().isEmpty())
			errors.rejectValue(prefix + "verificaPresenzaPartecipanti", "error.empty");

		/* VERIFICA APPRENDIMENTO (campo obbligatorio)
		 * checkbox
		 * */
		if(evento.getVerificaApprendimento() == null || evento.getVerificaApprendimento().isEmpty())
			errors.rejectValue(prefix + "verificaApprendimento", "error.empty");

		/* INDICATORE EFFICACIA FORMATIVA (campo obbligatorio se tipologiaEvento == PROGETTI_DI_MIGLIORAMENTO)
		 * campo testuale
		 * almeno 1 char
		 * */
		if(evento.getTipologiaEvento() != null
				&& evento.getTipologiaEvento() ==  TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO
				&& (evento.getIndicatoreEfficaciaFormativa() == null
				|| evento.getIndicatoreEfficaciaFormativa().isEmpty()))
			errors.rejectValue(prefix + "indicatoreEfficaciaFormativa", "error.empty");

		/* QUOTA DI PARTECIPAZIONE (campo obbligatorio)
		 * campo numerico (BigDecimal)
		 * */
		if(evento.getQuotaPartecipazione() == null)
			errors.rejectValue(prefix + "quotaPartecipazione", "error.empty");

	}

	//validate FAD
	private void validateFAD(EventoFAD evento, Errors errors, String prefix) {

		/* DATA FINE (campo obbligatorio)
		 * la data di fine deve può essere compresa nello stesso anno solare della data di inizio
		 * e l'evento non può avere durata superiore a 365 giorni
		 * */
		if(evento.getDataFine() == null)
			errors.rejectValue(prefix + "dataFine", "error.empty");
		else if(evento.getDataInizio() != null && (evento.getDataFine().isBefore(evento.getDataInizio())))
			errors.rejectValue(prefix + "dataFine", "error.data_fine_non_valida");
		else if(evento.getDataInizio() != null && evento.getDataFine().getYear() == evento.getDataInizio().getYear())
			errors.rejectValue(prefix + "dataFine", "error.data_fine_fad_non_valida");
		else if(evento.getDataInizio() != null && evento.getDataFine().isAfter(evento.getDataInizio().plusDays(ecmProperties.getGiorniMaxEventoFAD())))
			errors.rejectValue(prefix + "dataFine", "error.numero_massimo_giorni_evento_fad365");

		/* PARTECIPANTI (campo obbligatorio)
		 * massimo 5 cifre
		 * */
		if(evento.getNumeroPartecipanti() == null)
			errors.rejectValue(prefix + "numeroPartecipanti", "error.empty");
		else if(evento.getNumeroPartecipanti() < 0 || evento.getNumeroPartecipanti() > 99999)
			errors.rejectValue(prefix + "numeroPartecipanti", "error.numero_partecipanti_non_valido");

		/* TIPOLOGIA EVENTO (campo obbligatorio)
		 * selectpicker
		 * */
		if(evento.getTipologiaEvento() == null)
			errors.rejectValue(prefix + "tipologiaEvento", "error.empty");

		/* DOCENTI/RELATORI/TUTOR (serie di campi obbligatori)
		 * ripetibile complesso di classe PersonaEvento
		 * minimo 1
		 * devono avere tutti i campi inseriti
		 * */
		if(evento.getDocenti() == null || evento.getDocenti().isEmpty())
			errors.rejectValue(prefix + "docenti", "error.empty");
		else {
			int counter = 0;
			boolean atLeastOneErrorPersonaEvento = false;
			for(PersonaEvento p : evento.getDocenti()) {
				boolean hasError = validatePersonaEvento(p, "docente");
				if(hasError) {
					errors.rejectValue("docenti["+counter+"]", "error.campi_mancanti_persona");
					atLeastOneErrorPersonaEvento = true;
				}
				counter++;
			}
			if(atLeastOneErrorPersonaEvento)
				errors.rejectValue(prefix + "docenti", "error.campi_mancanti_docenti");
		}

		/* RAZIONALE (campo obbligatorio)
		 * campo testuale libero
		 * almeno 1 char
		 * */
		if(evento.getRazionale() == null || evento.getRazionale().isEmpty())
			errors.rejectValue(prefix + "razionale", "error.empty");

		/* RISULTATI ATTESI (campo obbligatorio)
		 * campo testuale libero ripetibile
		 * almeno 1 char
		 * almeno 1 elemento
		 * se non ci sono elementi, la input su cui inserire l'errore punterà al primo elemento della mappa
		 * */
		if(evento.getRisultatiAttesi() == null || evento.getRisultatiAttesi().isEmpty())
			errors.rejectValue("risultatiAttesiMapTemp[1]", "error.empty");

		/* VERIFICA APPRENDIMENTO (campo obbligatorio)
		 * campo complesso composto da checkbox e radio
		 * deve avere almeno 1 checkbox selezionata
		 * per ogni checkbox selezionata deve corrispondere un radio selezionato
		 * */
		if(evento.getVerificaApprendimento() == null)
			errors.rejectValue(prefix + "verificaApprendimento", "error.empty");
		else {
			int numCheck = 0;
			boolean noRadio = false;
			for(VerificaApprendimentoFAD vaf : evento.getVerificaApprendimento()) {
				if(vaf.getVerificaApprendimento() != null) {
					numCheck++;
					if(vaf.getVerificaApprendimentoInner() == null)
						noRadio = true;
				}
			}
			if(numCheck == 0)
				errors.rejectValue(prefix + "verificaApprendimento", "error.empty");
			else if(noRadio)
				errors.rejectValue(prefix + "verificaApprendimento", "error.mancano_spunte_radio_verifica_apprendimento_fad");
		}

		/* RADIO SUPPORTO DISCIPLINARE (campo obbligatorio)
		 * radio
		 * */
		if(evento.getSupportoSvoltoDaEsperto() == null)
			errors.rejectValue(prefix + "supportoSvoltoDaEsperto", "error.empty");

		/* CREDITI (campo obbligatorio/autocompilato)
		 * il campo viene autocompilato
		 * a questo punto l'utente può scegliere di accettare il valore
		 * o inserirne uno lui -> se NON accetta il valore il campo crediti che deve inserire è obbligatorio
		 * campo numerico (Float)
		 * */
		if(evento.getCrediti() == null)
			errors.rejectValue(prefix + "crediti", "error.empty");

		/* QUOTA DI PARTECIPAZIONE (campo obbligatorio)
		 * campo numerico (BigDecimal)
		 * */
		if(evento.getQuotaPartecipazione() == null)
			errors.rejectValue(prefix + "quotaPartecipazione", "error.empty");

		/* DOTAZIONE HARDWARE / SOFTWARE (campo obbligatorio)
		 * file allegato
		 * */
		if(evento.getRequisitiHardwareSoftware() == null || evento.getRequisitiHardwareSoftware().isNew())
			errors.rejectValue("requisitiHardwareSoftware", "error.empty");

		/* ACCESSO PIATTAFORMA (serie di campi obbligatori)
		 * 3 campi testuali
		 * */
		if(evento.getUserId() == null || evento.getUserId().isEmpty())
			errors.rejectValue(prefix + "userId", "error.empty");
		if(evento.getPassword() == null || evento.getPassword().isEmpty())
			errors.rejectValue(prefix + "password", "error.empty");
		if(evento.getUrl() == null || evento.getUrl().isEmpty())
			errors.rejectValue(prefix + "url", "error.empty");

	}

	//validate PersonaEvento (tipoPersona serve a distinguere il caso responsabileScientifico da Docente)
	private boolean validatePersonaEvento(PersonaEvento persona, String tipoPersona) {

		//campi comuni
		if(persona.getAnagrafica().getCognome() == null || persona.getAnagrafica().getCognome().isEmpty())
			return true;
		if(persona.getAnagrafica().getNome() == null || persona.getAnagrafica().getNome().isEmpty())
			return true;
		if(persona.getAnagrafica().getCodiceFiscale() == null || persona.getAnagrafica().getCodiceFiscale().isEmpty())
			return true;
		if(persona.getAnagrafica().getCv() == null || persona.getAnagrafica().getCv().isNew())
			return true;

		//campi particolari responsabileScientifico
		if(tipoPersona.equals("responsabile")) {
			if(persona.getQualifica() == null || persona.getQualifica().isEmpty())
				return true;
		}

		//campi particolari docente
		if(tipoPersona.equals("docente")) {
			if(persona.getRuolo() == null)
				return true;
			if(persona.getTitolare() == null)
				return true;
		}

		return false;
	}

	//validate PersonaFullEvento (responsabile segreteria)
	private boolean validatePersonaFullEvento(PersonaFullEvento persona) {

		if(persona.getAnagrafica().getCognome() == null || persona.getAnagrafica().getCognome().isEmpty())
			return true;
		if(persona.getAnagrafica().getNome() == null || persona.getAnagrafica().getNome().isEmpty())
			return true;
		if(persona.getAnagrafica().getCodiceFiscale() == null || persona.getAnagrafica().getCodiceFiscale().isEmpty())
			return true;
		if(persona.getAnagrafica().getEmail() == null || persona.getAnagrafica().getEmail().isEmpty())
			return true;
		if(persona.getAnagrafica().getTelefono() == null || persona.getAnagrafica().getTelefono().isEmpty())
			return true;
		if(persona.getAnagrafica().getCellulare() == null || persona.getAnagrafica().getCellulare().isEmpty())
			return true;

		return false;
	}

	//validate Sponsor
	private boolean validateSponsor(Sponsor sponsor) {

		if(sponsor.getName() == null || sponsor.getName().isEmpty())
			return true;
//		if(sponsor.getSponsorFile() == null || sponsor.getSponsorFile().isNew())
//			return true;

		return false;
	}

	//validate Partner
	private boolean validatePartner(Partner partner) {

		if(partner.getName() == null || partner.getName().isEmpty())
			return true;
		if(partner.getPartnerFile() == null || partner.getPartnerFile().isNew())
			return true;

		return false;
	}
}