package it.tredi.ecm.web.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.utils.Utils;
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
		if (evento.getObiettivoNazionale() == null)
			errors.rejectValue(prefix + "obiettivoNazionale", "error.empty");

		/* OBIETTIVO FORMATIVO REGIONALE (campo obbliagatorio)
		 * selectpicker
		 * */
		if (evento.getObiettivoRegionale() == null)
			errors.rejectValue(prefix + "obiettivoRegionale", "error.empty");

		/* PROFESSIONI/DISCIPLINE (campo obbligatorio)
		 * selectpicker (sono le discipline selezionate a creare le relative professioni)
		 * */
		if (evento.getDiscipline() == null || evento.getDiscipline().isEmpty())
			errors.rejectValue(prefix + "discipline", "error.empty");
		
		/* RESPONSABILI SCIENTIFICI (campo obbligatorio)
		 * ripetibile complesso di classe PersonaEvento
		 * minimo 1 - massimo 3
		 * devono avere tutti i campi inseriti (tranne cv? //TODO chiarire)
		 * */
		if (evento.getResponsabili() == null || evento.getResponsabili().isEmpty())
			errors.rejectValue(prefix + "responsabili", "error.empty");
		else if(evento.getResponsabili().size() > 3)
				errors.rejectValue(prefix + "responsabili", "error.troppi_responsabili");
		else {
			for(PersonaEvento p : evento.getResponsabili())
				validatePersonaEvento(p, errors, prefix + "responsabili");
		}
		
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
		else if(evento.getDataInizio() != null && (evento.getDataFine().getYear() != evento.getDataInizio().getYear()))
			errors.rejectValue(prefix + "dataFine", "error.data_fine_res_non_valida");

		/* DATE INTERMEDIE (campo opzionale)
		 * le date intermedie devono essere comprese tra quella di inizio e quella di fine
		 * */
		if(evento.getDataInizio() != null && evento.getDataFine() != null) {
			for (LocalDate ld : evento.getDateIntermedie()) {
				if(ld.isAfter(evento.getDataFine()) || ld.isBefore(evento.getDataInizio())) {
					//converto a String
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String dataToString = ld.format(dtf);
					//ciclo la mappa alla ricerca di questa data per farmi dare la chiave nella mappa
					Long key = -1L;
					for(Entry<Long, String> entry : wrapper.getDateIntermedieMapTemp().entrySet()) {
						if(entry.getValue().equals(dataToString)) {
							key = entry.getKey();
							break;
						}
					}
					errors.rejectValue("dateIntermedieMapTemp[" + key + "]", "error.data_intermedia_res_non_valida");
				}
			}
		}

		/* TIPOLOGIA EVENTO (campo obbligatorio)
		 * selectpicker (influenza altri campi, ma il controllo su questo campo è banale)
		 * */
		if(evento.getTipologiaEvento() == null)
			errors.rejectValue(prefix + "tipologiaEvento", "error.empty");

		/*	WORKSHOP/SEMINARI (campo obbligatorio se TIPOLOGIA EVENTO == CONVEGNO_CONGRESSO)
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
		 *  se la tipologia dell'evento è CONVEGNO_CONGRESSO -> minimo 200 partecipanti
		 *  se la tipologia dell'evento è WORKSHOP_SEMINARIO -> massimo 100 partecipanti
		 *  se la tipologia dell'evento è CORSO_AGGIORNAMENTO -> massimo 200 partecipanti
		 * */
		if(evento.getNumeroPartecipanti() == null)
			errors.rejectValue(prefix + "numeroPartecipanti", "error.empty");
		else if(evento.getTipologiaEvento() != null 
				&& evento.getTipologiaEvento() == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO
				&& evento.getNumeroPartecipanti() < 200)
			errors.rejectValue(prefix + "numeroPartecipanti", "error.pochi_partecipanti");
		else if(evento.getTipologiaEvento() != null 
				&& evento.getTipologiaEvento() == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO
				&& evento.getNumeroPartecipanti() > 100)
			errors.rejectValue(prefix + "numeroPartecipanti", "error.troppi_partecipanti");
		else if(evento.getTipologiaEvento() != null 
				&& evento.getTipologiaEvento() == TipologiaEventoRESEnum.CORSO_AGGIORNAMENTO
				&& evento.getNumeroPartecipanti() > 200)
			errors.rejectValue(prefix + "numeroPartecipanti", "error.troppi_partecipanti");
		
		/* DOCENTI/RELATORI/TUTOR (campo obbligatorio)
		 * ripetibile complesso di classe PersonaEvento
		 * minimo 1
		 * devono avere tutti i campi inseriti (tranne cv? //TODO chiarire)
		 * */
		if(evento.getDocenti() == null || evento.getDocenti().isEmpty())
			errors.rejectValue(prefix + "docenti", "error.empty");
		else {
			for(PersonaEvento p : evento.getDocenti())
				validatePersonaEvento(p, errors, prefix + "docenti");
		}
		
		/* RAZIONALE (campo obbligatorio)
		 * campo testuale libero
		 * almeno 1 char
		 * */
		if(evento.getRazionale() == null || evento.getRazionale().isEmpty())
			errors.rejectValue(prefix + "razionale", "errors.empty");
		
		/* RISULTATI ATTESI (campo obbligatorio)
		 * campo testuale libero ripetibile
		 * almeno 1 char
		 * almeno 1 elemento
		 * se non ci sono elementi, la input su cui inserire l'errore punterà al primo elemento della mappa
		 * */
		if(evento.getRisultatiAttesi() == null || evento.getRisultatiAttesi().isEmpty())
			errors.rejectValue("risultatiAttesiMapTemp[1]", "errors.empty");
		
	}

	//validate FSC
	private void validateFSC(EventoFSC evento, Errors errors, String prefix) {
		//TODO
	}

	//validate FAD
	private void validateFAD(EventoFAD evento, Errors errors, String prefix) {
		//TODO
	}
	
	//validate PersonaEvento
	private void validatePersonaEvento(PersonaEvento persona, Errors errors, String prefix) {
		//TODO
	}
}
