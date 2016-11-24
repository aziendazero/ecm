package it.tredi.ecm.web.validator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.omg.CORBA.TRANSACTION_MODE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.AzioneRuoliEventoFSC;
import it.tredi.ecm.dao.entity.DettaglioAttivitaFAD;
import it.tredi.ecm.dao.entity.DettaglioAttivitaRES;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.FaseAzioniRuoliEventoFSCTypeA;
import it.tredi.ecm.dao.entity.Partner;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.entity.PersonaFullEvento;
import it.tredi.ecm.dao.entity.ProgrammaGiornalieroRES;
import it.tredi.ecm.dao.entity.RiepilogoRuoliFSC;
import it.tredi.ecm.dao.entity.RuoloOreFSC;
import it.tredi.ecm.dao.entity.Sponsor;
import it.tredi.ecm.dao.entity.VerificaApprendimentoFAD;
import it.tredi.ecm.dao.enumlist.ContenutiEventoEnum;
import it.tredi.ecm.dao.enumlist.EventoStatoEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiRESEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCBaseEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.dao.enumlist.VerificaApprendimentoRESEnum;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoRESProgrammaGiornalieroWrapper;
import it.tredi.ecm.web.bean.EventoRESTipoDataProgrammaGiornalieroEnum;
import it.tredi.ecm.web.bean.EventoWrapper;

@Component
public class EventoValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventoValidator.class);

	@Autowired private EcmProperties ecmProperties;
	@Autowired private FileValidator fileValidator;
	@Autowired private FileService fileService;
	@Autowired private EventoService eventoService;

	private Set<String> risultatiAttesiUtilizzati;

	public void validate(Object target, EventoWrapper wrapper, Errors errors, String prefix) throws Exception{
		Evento evento = (Evento) target;
		validateCommon(evento, errors, prefix);

		if (evento instanceof EventoRES)
			validateRES(((EventoRES) evento), wrapper, errors, prefix);
		else if (evento instanceof EventoFSC)
			validateFSC(((EventoFSC) evento), wrapper, errors, prefix);
		else if (evento instanceof EventoFAD)
			validateFAD(((EventoFAD) evento), wrapper, errors, prefix);

		Utils.logDebugErrorFields(LOGGER, errors);

	}

	//validate delle parti in comune
	private void validateCommon(Evento evento, Errors errors, String prefix) throws Exception{

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
		 * un evento può essere inserito fino a 15 giorni dalla data di inizio per il gruppo A, 30 per il gruppo B, 10 se riedizione
		 * controllo effettutato solo da BOZZA a VALIDATO
		 * -------------------
		 * un evento non può essere anticipato e non gli può essere cambiato il numero di date
		 * controllo effettutato solo da VALIDATO a VALIDATO
		 * */
		if(evento.getStato() == EventoStatoEnum.BOZZA) {
			int minGiorni;
			if(evento.getProvider().getTipoOrganizzatore().getGruppo().equals("A"))
				minGiorni = ecmProperties.getGiorniMinEventoProviderA();
			else minGiorni = ecmProperties.getGiorniMinEventoProviderB();
			if(evento.isRiedizione())
				minGiorni = ecmProperties.getGiorniMinEventoRiedizione();

			if(evento.getDataInizio() == null)
				errors.rejectValue(prefix + "dataInizio", "error.empty");
			else if(evento.getDataInizio().isBefore(LocalDate.now().plusDays(minGiorni)))
				errors.rejectValue(prefix + "dataInizio", "error.data_inizio_non_valida");
		}
		else {
			//impedisce anticipazioni di date di eventi validati e un cambio di numero di date confrontando con l'evento
			//sul DB non ancora modificato
			Evento eventoDaDB = eventoService.getEvento(evento.getId());
			//la segreteria solo può anticipare la data di un evento
			if(evento.getDataInizio().isBefore(eventoDaDB.getDataInizio()) && !Utils.getAuthenticatedUser().isSegreteria())
				errors.rejectValue(prefix + "dataInizio", "error.anticipazione_data_non_possibile");
			else if(evento instanceof EventoRES) {
				//numero date da rispettare
				//data inizio fine uguali
				if(eventoDaDB.getDataFine().isEqual(eventoDaDB.getDataInizio())
						&& !evento.getDataFine().isEqual(evento.getDataInizio())) {
					errors.rejectValue(prefix + "dataInizio", "error.date_non_separabili");
					errors.rejectValue(prefix + "dataFine", "error.date_non_separabili");
				}
				//data inizio fine non uguali
				else if (!eventoDaDB.getDataFine().isEqual(eventoDaDB.getDataInizio())
						&& evento.getDataFine().isEqual(evento.getDataInizio())) {
					errors.rejectValue(prefix + "dataInizio", "error.date_non_unificabili");
					errors.rejectValue(prefix + "dataFine", "error.date_non_unificabili");
				}
			}
		}

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
					errors.rejectValue("responsabiliScientifici["+counter+"]", "");
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

		/* CHECK INFO SPONSOR (campo obbligatorio)
		 * spunta richiesta
		 * */
		if(evento.getEventoSponsorizzato() != null && evento.getEventoSponsorizzato().booleanValue()){
			if(evento.getLetteInfoAllegatoSponsor() == null || evento.getLetteInfoAllegatoSponsor() == false)
				errors.rejectValue(prefix + "letteInfoAllegatoSponsor", "error.empty");
		}

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
					errors.rejectValue("sponsors["+counter+"]", "");
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
				&& evento.getEventoSponsorizzato() != null
				&& evento.getEventoSponsorizzato() == true
				&& evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() != null
				&& evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() == false
				&& evento.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia() == null){
			errors.rejectValue("autocertificazioneAssenzaAziendeAlimentiPrimaInfanzia", "error.empty");
		}
		else{
			if(!fileValidator.validateFirmaCF(evento.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia(), evento.getProvider().getId()))
				errors.rejectValue("autocertificazioneAssenzaAziendeAlimentiPrimaInfanzia", "error.codiceFiscale.firmatario");
		}

		/* AUTOCERTIFICAZIONE DI AUTORIZZAZIONE DEL MINISTERO
		 * (campo obbligatorio se contenutiEvento == ALIMENTAZIONE_PRIMA_INFANZIA
		 * e eventoSponsorizzatoDaAziendeAlimentiPrimainfanzia == false)
		 * file allegato
		 * */
		if(evento.getContenutiEvento() != null
				&& evento.getContenutiEvento() == ContenutiEventoEnum.ALIMENTAZIONE_PRIMA_INFANZIA
				&& evento.getEventoSponsorizzato() != null
				&& evento.getEventoSponsorizzato() == true
				&& evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() != null
				&& evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() == true
				&& evento.getAutocertificazioneAutorizzazioneMinisteroSalute() == null){
			errors.rejectValue("autocertificazioneAutorizzazioneMinisteroSalute", "error.empty");
		}
		else{
			if(!fileValidator.validateFirmaCF(evento.getAutocertificazioneAutorizzazioneMinisteroSalute(), evento.getProvider().getId()))
				errors.rejectValue("autocertificazioneAutorizzazioneMinisteroSalute", "error.codiceFiscale.firmatario");
		}

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
				&& evento.getContrattiAccordiConvenzioni() == null){
			errors.rejectValue("contrattiAccordiConvenzioni", "error.empty");
		}else{
			if(!fileValidator.validateFirmaCF(evento.getContrattiAccordiConvenzioni(), evento.getProvider().getId()))
				errors.rejectValue("contrattiAccordiConvenzioni", "error.codiceFiscale.firmatario");
		}

		/* AUTOCERTIFICAZIONE ASSENZA FINANZIAMENTI (facoltativo solo per i provider del gruppo A)
		 * (campo obbligatorio se altreFormeFinanziamento == false)
		 * file allegato
		 * */
		if(!evento.getProvider().isGruppoA()){
			if(evento.getAltreFormeFinanziamento() != null
					&& evento.getAltreFormeFinanziamento() == false
					&& evento.getAutocertificazioneAssenzaFinanziamenti() == null){
				errors.rejectValue("autocertificazioneAssenzaFinanziamenti", "error.empty");
			}else{
				if(!fileValidator.validateFirmaCF(evento.getAutocertificazioneAssenzaFinanziamenti(), evento.getProvider().getId()))
					errors.rejectValue("autocertificazioneAssenzaFinanziamenti", "error.codiceFiscale.firmatario");
			}
		}

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
				boolean hasError = validatePartner(p, evento.getProvider().getId());
				if(hasError) {
					errors.rejectValue("partners["+counter+"]", "");
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
		if(evento.getDichiarazioneAssenzaConflittoInteresse() == null){
			errors.rejectValue("dichiarazioneAssenzaConflittoInteresse", "error.empty");
		}else{
			if(!fileValidator.validateFirmaCF(evento.getDichiarazioneAssenzaConflittoInteresse(), evento.getProvider().getId()))
				errors.rejectValue("dichiarazioneAssenzaConflittoInteresse", "error.codiceFiscale.firmatario");
		}

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
	private void validateRES(EventoRES evento, EventoWrapper wrapper, Errors errors, String prefix) throws Exception {

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
		 * le date intermedie devono essere strettamente comprese tra quella di inizio e quella di fine
		 * non possono essere ripetute
		 * -------------
		 * per quanto riguarda la riedizione, il padre e il rieditato devono evere lo stesso numero di date intermedie
		 * */
		if(evento.getDataInizio() != null && evento.getDataFine() != null) {
			for (LocalDate ld : evento.getDateIntermedie()) {
				if(ld.isAfter(evento.getDataFine()) || ld.isEqual(evento.getDataFine()) || ld.isBefore(evento.getDataInizio()) || ld.isEqual(evento.getDataInizio())) {
					//ciclo alla ricerca di questa data per farmi dare le chiavi nella mappa
					Set<Long> keys = new HashSet<Long>();
					for(Entry<Long, EventoRESProgrammaGiornalieroWrapper> entry : wrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().entrySet()) {
						if(entry.getValue().getTipoData() == EventoRESTipoDataProgrammaGiornalieroEnum.INTERMEDIA
								&& entry.getValue().getProgramma().getGiorno() != null && entry.getValue().getProgramma().getGiorno().isEqual(ld)) {
							keys.add(entry.getKey());
						}
					}
					//genero gli errori
					for(Long l : keys)
						errors.rejectValue("eventoRESDateProgrammiGiornalieriWrapper.sortedProgrammiGiornalieriMap[" + l + "]", "error.data_intermedia_res_non_valida");
				}
			}
		}
		//controllo ripetizioni
		if(evento.getDateIntermedie() != null && !evento.getDateIntermedie().isEmpty()) {
			Set<LocalDate> duplicates = new HashSet<LocalDate>();
			duplicates = evento.getDateIntermedie().stream().filter(ld -> Collections.frequency(evento.getDateIntermedie(), ld) > 1).collect(Collectors.toSet());
			for(LocalDate ld : duplicates) {
				//ciclo alla ricerca di questa data per farmi dare le chiavi nella mappa
				Set<Long> keys = new HashSet<Long>();
				for(Entry<Long, EventoRESProgrammaGiornalieroWrapper> entry : wrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().entrySet()) {
					if(entry.getValue().getTipoData() == EventoRESTipoDataProgrammaGiornalieroEnum.INTERMEDIA
							&& entry.getValue().getProgramma().getGiorno() != null && entry.getValue().getProgramma().getGiorno().isEqual(ld)) {
						keys.add(entry.getKey());
					}
				}
				//genero gli errori
				for(Long l : keys)
					errors.rejectValue("eventoRESDateProgrammiGiornalieriWrapper.sortedProgrammiGiornalieriMap[" + l + "]", "error.data_intermedia_ripetuta");
			}
		}
		//controllo riedizione con evento padre
		if(evento.isRiedizione()) {
			Evento eventoPadre = eventoService.getEvento(evento.getEventoPadre().getId());
			if(evento.getDateIntermedie().size() != ((EventoRES) eventoPadre).getDateIntermedie().size()) {
				//cerco le date che sono state annullate
				Set<Long> keys = new HashSet<Long>();
				for(Entry<Long, EventoRESProgrammaGiornalieroWrapper> entry : wrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().entrySet()) {
					if(entry.getValue().getTipoData() == EventoRESTipoDataProgrammaGiornalieroEnum.INTERMEDIA
							&& entry.getValue().getProgramma().getGiorno() == null) {
						keys.add(entry.getKey());
					}
				}
				//genero gli errori
				for(Long l : keys)
					errors.rejectValue("eventoRESDateProgrammiGiornalieriWrapper.sortedProgrammiGiornalieriMap[" + l + "]", "error.data_intermedia_annullata");
			}
		}


		/* TIPOLOGIA EVENTO (campo obbligatorio)
		 * selectpicker (influenza altri campi, ma il controllo su questo campo è banale)
		 * */
		if(evento.getTipologiaEventoRES() == null)
			errors.rejectValue(prefix + "tipologiaEventoRES", "error.empty");

		/* WORKSHOP/SEMINARI (campo obbligatorio se TIPOLOGIA EVENTO == CONVEGNO_CONGRESSO)
		 * radio
		 * */
		if(evento.getTipologiaEventoRES() != null
				&& evento.getTipologiaEventoRES() == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO
				&& evento.getWorkshopSeminariEcm() == null)
			errors.rejectValue(prefix + "workshopSeminariEcm", "error.empty");

		/* TITOLO CONVEGNO (campo obbligatorio se TIPOLOGIA EVENTO == WORKSHOP_SEMINARIO)
		 * campo testuale libero
		 * almeno 1 char
		 * */
		if(evento.getTipologiaEventoRES() != null
				&& evento.getTipologiaEventoRES() == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO
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
		else if(evento.getTipologiaEventoRES() != null
				&& evento.getTipologiaEventoRES() == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO
				&& evento.getNumeroPartecipanti() < ecmProperties.getNumeroMinimoPartecipantiConvegnoCongressoRES())
			errors.rejectValue(prefix + "numeroPartecipanti", "error.pochi_partecipanti200");
		else if(evento.getTipologiaEventoRES() != null
				&& evento.getTipologiaEventoRES() == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO
				&& evento.getNumeroPartecipanti() > ecmProperties.getNumeroMassimoPartecipantiWorkshopSeminarioRES())
			errors.rejectValue(prefix + "numeroPartecipanti", "error.troppi_partecipanti100");
		else if(evento.getTipologiaEventoRES() != null
				&& evento.getTipologiaEventoRES() == TipologiaEventoRESEnum.CORSO_AGGIORNAMENTO
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
					errors.rejectValue("docenti["+counter+"]", "");
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

		/* PROGRAMMA RES (serie di campi obbligatori)
		 * ripetibile complesso di classe ProgrammaGiornalieroRES
		 * stesso numero delle date (gestito lato interfaccia)
		 * devono avere tutti i campi inseriti
		 * N.B. creo un set di risultati attesi per verificare dopo il ciclo del programma se sono stati utilizzati tutti
		 * */
		risultatiAttesiUtilizzati = new HashSet<String>();
		if(evento.getProgramma() == null || evento.getProgramma().isEmpty())
			errors.rejectValue(prefix + "programma", "error.empty");
		else {
			for(ProgrammaGiornalieroRES pgr : evento.getProgramma()) {
				//ciclo alla ricerca di questo Programma per farmi dare la chiave nella mappa
				Long key = -1L;
				for(Entry<Long, EventoRESProgrammaGiornalieroWrapper> entry : wrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().entrySet()) {
					if(entry.getValue().getProgramma().equals(pgr)) {
						key = entry.getKey();
						break;
					}
				}
				validateProgrammaRES(pgr, errors, "eventoRESDateProgrammiGiornalieriWrapper.sortedProgrammiGiornalieriMap["+ key +"].programma.", evento.getTipologiaEventoRES());
			}
		}

		/* RISULTATI ATTESI (campo obbligatorio)
		 * campo testuale libero ripetibile
		 * almeno 1 char
		 * almeno 1 elemento
		 * se non ci sono elementi, la input su cui inserire l'errore punterà al primo elemento della mappa
		 * controllo che effettivamente tutti i risultati attesi siano stati utilizzati
		 * devono essere presenti nel set precedentemente settato durante il ciclo del programma
		 * controllo solo se tipologiaEvento != CONVEGNO_CONGRESSO
		 * */
		if(evento.getTipologiaEventoRES() != null && evento.getTipologiaEventoRES() != TipologiaEventoRESEnum.CONVEGNO_CONGRESSO) {
			if(evento.getRisultatiAttesi() == null || evento.getRisultatiAttesi().isEmpty())
				errors.rejectValue(prefix + "risultatiAttesi", "error.empty");
			else{
				for (String ra : evento.getRisultatiAttesi()) {
					if(!ra.isEmpty() && !risultatiAttesiUtilizzati.contains(ra)) {
						//ciclo alla ricerca di questa data per farmi dare la chiave nella mappa
						Long key = -1L;
						for(Entry<Long, String> entry : wrapper.getRisultatiAttesiMapTemp().entrySet()) {
							if(entry.getValue().equals(ra)) {
								key = entry.getKey();
								break;
							}
						}
						errors.rejectValue("risultatiAttesiMapTemp[" + key + "]", "error.risultato_atteso_non_utilizzato");
					}
				}
			}
		}

		/* BROCHURE EVENTO (campo obbligatorio se TIPOLOGIA EVENTO == CONVEGNO_CONGRESSO o WORKSHOP_SEMINARIO)
		 * file allegato
		 * */
		if((evento.getTipologiaEventoRES() == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO
				|| evento.getTipologiaEventoRES() == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO)
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
		else if (evento.getTipologiaEventoRES() == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO
				&& (!evento.getVerificaApprendimento().contains(VerificaApprendimentoRESEnum.AUTOCERTFICAZIONE)
				|| evento.getVerificaApprendimento().size() > 1))
			errors.rejectValue(prefix + "verificaApprendimento", "error.solo_autocertificazione_selezionabile");
		else if ((evento.getTipologiaEventoRES() != TipologiaEventoRESEnum.CONVEGNO_CONGRESSO)
				&& evento.getVerificaApprendimento().contains(VerificaApprendimentoRESEnum.AUTOCERTFICAZIONE))
			errors.rejectValue(prefix + "verificaApprendimento",  "error.autocertificazione_non_selezionabile");

		/* DURATA COMPLESSIVA (autocompilato)
		 * controllo di sicurezza, la durata totale dell'evento deve essere di minimo 3 ore
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

		/* DOCUMENTO VERIICA RICADUTE FORMATIVE (campo FACOLTATIVO, MA SE C'E' CONTROLLO SULLA FIRMA DIGITALE)
		 * file allegato
		 * */
		if(evento.getDocumentoVerificaRicaduteFormative() != null && !evento.getDocumentoVerificaRicaduteFormative().isNew()){
			if(!fileValidator.validateFirmaCF(evento.getDocumentoVerificaRicaduteFormative(), evento.getProvider().getId()))
				errors.rejectValue("documentoVerificaRicaduteFormative", "error.codiceFiscale.firmatario");
		}

	}

	//validate FSC
	private void validateFSC(EventoFSC evento, EventoWrapper wrapper, Errors errors, String prefix) {

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
		 * l'evento non può avere durata superiore a 730 giorni
		 * */
		if(evento.getDataFine() == null)
			errors.rejectValue(prefix + "dataFine", "error.empty");
		else if(evento.getDataInizio() != null && (evento.getDataFine().isBefore(evento.getDataInizio())))
			errors.rejectValue(prefix + "dataFine", "error.data_fine_non_valida");
		else if(evento.getDataInizio() != null && evento.getDataFine().isAfter(evento.getDataInizio().plusDays(ecmProperties.getGiorniMaxEventoFSC())))
			errors.rejectValue(prefix + "dataFine", "error.numero_massimo_giorni_evento_fsc730");

		/* TIPOLOGIA EVENTO (campo obbligatorio)
		 * selectpicker (influenza altri campi, ma il controllo su questo campo è banale)
		 * */
		if(evento.getTipologiaEventoFSC() == null)
			errors.rejectValue(prefix + "tipologiaEventoFSC", "error.empty");

		/* TIPOLOGIA GRUPPO (campo obbligatorio se tipologiaEvento == GRUPPI_DI_MIGLIORAMENTO)
		 * selectpicker
		 * */
		if(evento.getTipologiaEventoFSC() != null
				&& evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO
				&& evento.getTipologiaGruppo() == null)
			errors.rejectValue(prefix + "tipologiaGruppo", "error.empty");

		/* SPERIMENTAZIONE CLINICA (campo obbligatorio se tipologiaEvento == ATTIVITA_DI_RICERCA)
		 * radio
		 * */
		if(evento.getTipologiaEventoFSC() != null
				&& evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA
				&& evento.getSperimentazioneClinica() == null)
			errors.rejectValue(prefix + "sperimentazioneClinica", "error.empty");

		/* OTTENUTO PARERE ETICO (campo obbligatorio se tipologiaEvento == ATTIVITA_DI_RICERCA && sperimentazioneClinica == true)
		 * spunta richiesta
		 * */
		if(evento.getTipologiaEventoFSC() != null
				&& evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA
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

		/* FASI DAINSERIRE (campo obbligatorio se tipologiaEvento == PROGETTI_DI_MIGLIORAMENTO)
		 * radio
		 * gestione perticolare per tipologiaEvento == PROGETTI_DI_MIGLIORAMENTO
		 * */
		if(evento.getTipologiaEventoFSC() != null
				&& evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO
				&& evento.getFasiDaInserire() == null)
			errors.rejectValue(prefix + "fasiDaInserire", "error.empty");

		/* FASI/AZIONI/RUOLI FSC
		 * (serie di campi obbligatori)
		 * ripetibile complesso di classe fasiAzioniRuoli
		 * cambia a seconda della tipologia di evento FSC
		 * devono avere tutti i campi inseriti
		 * */
		if(evento.getFasiAzioniRuoli() == null || evento.getFasiAzioniRuoli().isEmpty())
			errors.rejectValue(prefix + "fasiAzioniRuoli", "error.selezionare_tipologia_inserimento_programma");
		else {
			int counter = 0;
			boolean atLeastOnePartecipante = false;
			boolean atLeastOneTutor = false;
			//gestione particolare tipologiaEvento == PROGETTI_DI_MIGLIORAMENTO
			//eseguo i controlli solo alle fasiDaInserire specificate da fasiDaInserire
			if(evento.getTipologiaEventoFSC() != null
					&& evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO
					&& evento.getFasiDaInserire() != null) {
				for(FaseAzioniRuoliEventoFSCTypeA far : evento.getFasiAzioniRuoli()) {
					//validazione solo se la fase è abilitata
					if(evento.getFasiDaInserire().getFasiAbilitate().contains(far.getFaseDiLavoro())) {
						boolean[] validationResults = validateFasiAzioniRuoliFSC(far, errors, "programmaEventoFSC["+counter+"].", evento.getTipologiaEventoFSC());
						if(validationResults[0])
							atLeastOnePartecipante = true;
						if(validationResults[1])
							atLeastOneTutor = true;
					}
					counter++;
				}
			}
			//gestione di default
			else {
				for(FaseAzioniRuoliEventoFSCTypeA far : evento.getFasiAzioniRuoli()) {
					boolean[] validationResults = validateFasiAzioniRuoliFSC(far, errors, "programmaEventoFSC["+counter+"].", evento.getTipologiaEventoFSC());
					if(validationResults[0])
						atLeastOnePartecipante = true;
					if(validationResults[1])
						atLeastOneTutor = true;
					counter++;
				}
			}
			if(!atLeastOnePartecipante || (!atLeastOneTutor && evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO))
				errors.rejectValue(prefix + "fasiAzioniRuoli", "error.vincolo_partecipanti_tutor" + evento.getTipologiaEventoFSC());
		}

		/* TABELLA RIEPILOGO FSC
		 * - numero partecipanti (campo obbligatorio)
		 * - controllo sulle ore per ruolo a seconda della tipologiaEvento
		 * */
		if(evento.getRiepilogoRuoli() == null || evento.getRiepilogoRuoli().isEmpty())
			errors.rejectValue(prefix + "riepilogoRuoli", "error.errori_calcolo_tabella_riepilogo");
		else {
			boolean atLeastOneErrorTabella = false;
			for(RiepilogoRuoliFSC rrf : evento.getRiepilogoRuoli()) {
				if(rrf.getRuolo() != null) {
					boolean hasError = validateTabellaRuoliFSC(rrf, evento.getTipologiaEventoFSC());
					if(hasError) {
						errors.rejectValue("riepilogoRuoliFSC["+rrf.getRuolo()+"]", "");
						atLeastOneErrorTabella = true;
					}
				}
			}
			if(atLeastOneErrorTabella)
				errors.rejectValue(prefix + "riepilogoRuoli", "error.errore_tabella_fsc"+evento.getTipologiaEventoFSC());
		}

		/* NUMERO PARTECIPANTI (campo obbligatorio)
		 * se tipologiaEvento == TRAINING_INDIVIDUALIZZATO massimo 5 partecipanti per tutor
		 * se tipologiaEvento == GRUPPI_DI_MIGLIORAMENTO massimo 25 partecipanti
		 * se tipologiaEvento == AUDIT_CLINICO_ASSISTENZIALE massimo 25 partecipanti
		 * N.B. devio gli errori sulla tebella di riepilogo, dove vengono effettivamente inseriti i partecipanti/tutor
		 * */
		if(evento.getNumeroPartecipanti() == null)
			errors.rejectValue(prefix + "riepilogoRuoli", "error.empty");
		else if(evento.getTipologiaEventoFSC() != null
				&& evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO
				&& evento.getNumeroPartecipanti() > ecmProperties.getNumeroMassimoPartecipantiGruppiMiglioramentoFSC())
			errors.rejectValue(prefix + "riepilogoRuoli", "error.errore_tabella_fsc"+evento.getTipologiaEventoFSC());
		else if(evento.getTipologiaEventoFSC() != null
				&& evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE
				&& evento.getNumeroPartecipanti() > ecmProperties.getNumeroMassimoPartecipantiAuditClinicoFSC())
			errors.rejectValue(prefix + "riepilogoRuoli", "error.errore_tabella_fsc"+evento.getTipologiaEventoFSC());
		else if(evento.getTipologiaEventoFSC() != null
				&& evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO
				&& evento.getNumeroPartecipanti() > (evento.getNumeroTutor() * 5))
			errors.rejectValue(prefix + "riepilogoRuoli", "error.errore_tabella_fsc"+evento.getTipologiaEventoFSC());

		/* DURATA COMPLESSIVA (autocompilato)
		 * controlli di sicurezza:
		 * - la durata totale dell'evento deve essere di più di 10 ore per AUDIT_CLINICO_ASSISTENZIALE
		 * - la durata totale dell'evento deve essere di più di 8 ore per GRUPPI_DI_MIGLIORAMENTO
		 * - la durata totale dell'evento deve essere di più di 8 ore per PROGETTI_DI_MIGLIORAMENTO
		 * */
		if(evento.getTipologiaEventoFSC() != null) {
			if(evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE
				&& evento.getDurata() < ecmProperties.getDurataMinimaAuditClinicoFSC())
			errors.rejectValue(prefix + "durata", "error.durata_minima_complessiva_non_raggiunta");
			else if(evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO
					&& evento.getDurata() < ecmProperties.getDurataMinimaGruppiMiglioramentoFSC())
				errors.rejectValue(prefix + "durata", "error.durata_minima_complessiva_non_raggiunta");
			else if(evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO
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
		if(evento.getTipologiaEventoFSC() != null
				&& evento.getTipologiaEventoFSC() ==  TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO
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
	private void validateFAD(EventoFAD evento, EventoWrapper wrapper, Errors errors, String prefix) throws Exception{

		/* DATA FINE (campo obbligatorio)
		 * la data di fine deve può essere compresa nello stesso anno solare della data di inizio
		 * e l'evento non può avere durata superiore a 365 giorni
		 * */
		if(evento.getDataFine() == null)
			errors.rejectValue(prefix + "dataFine", "error.empty");
		else if(evento.getDataInizio() != null && (evento.getDataFine().isBefore(evento.getDataInizio())))
			errors.rejectValue(prefix + "dataFine", "error.data_fine_non_valida");
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
		if(evento.getTipologiaEventoFAD() == null)
			errors.rejectValue(prefix + "tipologiaEventoFAD", "error.empty");

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
					errors.rejectValue("docenti["+counter+"]", "");
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

		/* PROGRAMMA FAD
		 * (serie di campi obbligatori)
		 * ripetibile complesso di classe DettaglioAttivitàFAD
		 * devono avere tutti i campi inseriti
		 * N.B. mentre ciclo il programma FAD mi segno tutti i risultati attesi utilizzati
		 * */
		risultatiAttesiUtilizzati = new HashSet<String>();
		if(evento.getProgrammaFAD() == null || evento.getProgrammaFAD().isEmpty())
			errors.rejectValue(prefix + "programmaFAD", "error.empty");
		else {
			int counter = 0;
			boolean atLeastOneErrorAttivita = false;
			for(DettaglioAttivitaFAD daf : evento.getProgrammaFAD()) {
				boolean hasError = validateProgrammaFAD(daf);
				if(hasError) {
					errors.rejectValue("programmaEventoFAD["+counter+"]", "error.campi_con_errori_programma_fad");
					atLeastOneErrorAttivita = true;
				}
				counter++;
			}
			if(atLeastOneErrorAttivita)
				errors.rejectValue(prefix + "programmaFAD", "error.campi_con_errori_programma_fad");
		}

		/* RISULTATI ATTESI (campo obbligatorio)
		 * campo testuale libero ripetibile
		 * almeno 1 char
		 * almeno 1 elemento
		 * se non ci sono elementi, la input su cui inserire l'errore punterà al primo elemento della mappa
		 * controllo che effettivamente tutti i risultati attesi siano stati utilizzati
		 * devono essere presenti nel set precedentemente settato durante il ciclo del programma
		 * controllo solo se tipologiaEvento != CONVEGNO_CONGRESSO
		 * */
		if(evento.getRisultatiAttesi() == null || evento.getRisultatiAttesi().isEmpty())
			errors.rejectValue(prefix + "risultatiAttesi", "error.empty");
		else{
			for (String ra : evento.getRisultatiAttesi()) {
				if(!ra.isEmpty() && !risultatiAttesiUtilizzati.contains(ra)) {
					//ciclo alla ricerca di questa data per farmi dare la chiave nella mappa
					Long key = -1L;
					for(Entry<Long, String> entry : wrapper.getRisultatiAttesiMapTemp().entrySet()) {
						if(entry.getValue().equals(ra)) {
							key = entry.getKey();
							break;
						}
					}
					errors.rejectValue("risultatiAttesiMapTemp[" + key + "]", "error.risultato_atteso_non_utilizzato");
				}
			}
		}

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
		if(evento.getRequisitiHardwareSoftware() == null || evento.getRequisitiHardwareSoftware().isNew()){
			errors.rejectValue("requisitiHardwareSoftware", "error.empty");
		}else{
			if(!fileValidator.validateFirmaCF(evento.getRequisitiHardwareSoftware(), evento.getProvider().getId()))
				errors.rejectValue("requisitiHardwareSoftware", "error.codiceFiscale.firmatario");
		}

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
	private boolean validatePartner(Partner partner, Long providerId) throws Exception {
		if(partner.getPartnerFile() != null && !partner.getPartnerFile().isNew())
			partner.setPartnerFile(fileService.getFile(partner.getPartnerFile().getId()));

		if(partner.getName() == null || partner.getName().isEmpty())
			return true;
		if(partner.getPartnerFile() == null || partner.getPartnerFile().isNew())
			return true;
		if(!fileValidator.validateFirmaCF(partner.getPartnerFile(), providerId))
			return true;

		return false;
	}

	//validate ProgrammaRES
	private void validateProgrammaRES(ProgrammaGiornalieroRES programma, Errors errors, String prefix, TipologiaEventoRESEnum tipologiaEvento){

		//data (gratis, non è un dato che può inseririre l'utente, ma viene generata
		//all'inserimento delle date di inzio, fine e intermedie

		//sede
		if(programma.getSede() == null){
			errors.rejectValue(prefix + "sede", "error.sede_evento_null");
			errors.rejectValue(prefix + "sede.provincia", "error.empty");
			errors.rejectValue(prefix + "sede.comune", "error.empty");
			errors.rejectValue(prefix + "sede.luogo", "error.empty");
			errors.rejectValue(prefix + "sede.indirizzo", "error.empty");
		}
		else {
			if(programma.getSede().getProvincia() == null || programma.getSede().getProvincia().isEmpty())
				errors.rejectValue(prefix + "sede.provincia", "error.empty");
			if(programma.getSede().getComune() == null || programma.getSede().getComune().isEmpty())
				errors.rejectValue(prefix + "sede.comune", "error.empty");
			if(programma.getSede().getLuogo() == null || programma.getSede().getLuogo().isEmpty())
				errors.rejectValue(prefix + "sede.luogo", "error.empty");
			if(programma.getSede().getIndirizzo() == null || programma.getSede().getIndirizzo().isEmpty())
				errors.rejectValue(prefix + "sede.indirizzo", "error.empty");
		}

		//lista attività
		if(programma.getProgramma() == null || programma.getProgramma().isEmpty())
			errors.rejectValue(prefix + "programma", "error.empty");
		else {
			int counter = 0;
			boolean atLeastOneAttivita = false; //controllo che non siano state inserite solo pause
			boolean atLeastOneErrorDettaglioAttivita = false;
			for(DettaglioAttivitaRES dar : programma.getProgramma()) {
				boolean hasError = validateDettaglioAttivitaRES(dar, tipologiaEvento);
				if(hasError) {
					errors.rejectValue(prefix + "programma["+counter+"]", "");
					atLeastOneErrorDettaglioAttivita = true;
				}
				if(!dar.isExtraType())
					atLeastOneAttivita = true;
				counter++;
			}
			if(atLeastOneErrorDettaglioAttivita)
				errors.rejectValue(prefix + "programma", "error.campi_mancanti_dettaglio_attivita");
			else if(!atLeastOneAttivita)
				errors.rejectValue(prefix + "programma", "error.solo_pause_programma_res");
		}
	}

	//validate DettaglioAttivita del ProgrammaRES
	private boolean validateDettaglioAttivitaRES(DettaglioAttivitaRES dettaglio, TipologiaEventoRESEnum tipologiaEvento){

		//per prima cose se ho un risultato atteso lo aggiungo al set
		risultatiAttesiUtilizzati.add(dettaglio.getRisultatoAtteso());

		//tutti i campi devono essere inseriti, con eccezione di:
		// 1) se tipologiaEvento è CONVEGNI_CONGRESSI, bisogna inserire un programma semplificato
		//con nessun risultato atteso, obiettivo formativo OBV1 e le relative metodologie
		// 2) se il dettaglio attività è una pausa necessario solo orari
		if(dettaglio.getOrarioInizio() == null)
			return true;
		if(dettaglio.getOrarioFine() == null)
			return true;
		else if(dettaglio.getOrarioFine().isBefore(dettaglio.getOrarioInizio()))
			return true;

		//controlli per non pausa [ 2) ]
		if(!dettaglio.isExtraType()) {
			if(dettaglio.getArgomento() == null || dettaglio.getArgomento().isEmpty())
				return true;
			if(dettaglio.getDocente() == null)
				return true;
			if(dettaglio.getObiettivoFormativo() == null)
				return true;
			if(dettaglio.getMetodologiaDidattica() == null)
				return true;

			System.out.println(dettaglio.getRisultatoAtteso());

			//controllo per eventi non di tipolgia CONVEGNO_CONGRESSO [ 1) ]
			if(tipologiaEvento != TipologiaEventoRESEnum.CONVEGNO_CONGRESSO) {
				if(dettaglio.getRisultatoAtteso() == null || dettaglio.getRisultatoAtteso().isEmpty())
					return true;
			}
			//controllo specifico per tipologia di eventi CONVEGNO_CONGRESSO [ 1) ]
			else {
				if(dettaglio.getRisultatoAtteso() != null && !dettaglio.getRisultatoAtteso().isEmpty())
					return true;
				if(dettaglio.getObiettivoFormativo() != ObiettiviFormativiRESEnum.OBV1)
					return true;
				//io lascerei così, la probabilita che le enum cambino e spacchino tutto è troppo alta.. se proprio richiesto abilitare il seguente
	//			if(dettaglio.getMetodologiaDidattica() != MetodologiaDidatticaRESEnum._1
	//					|| dettaglio.getMetodologiaDidattica() != MetodologiaDidatticaRESEnum._2
	//					|| dettaglio.getMetodologiaDidattica() != MetodologiaDidatticaRESEnum._3
	//					|| dettaglio.getMetodologiaDidattica() != MetodologiaDidatticaRESEnum._4
	//					|| dettaglio.getMetodologiaDidattica() != MetodologiaDidatticaRESEnum._5)
	//				return true;
			}
		}
		return false;
	}

	//validate FasiAzioniRuoliFSC
	//ritorna se ha trovato almeno 1 partecipante e almeno 1 tutor per fase
	private boolean[] validateFasiAzioniRuoliFSC(FaseAzioniRuoliEventoFSCTypeA faseAzioniRuoli, Errors errors, String prefix, TipologiaEventoFSCEnum tipologiaEvento) {

		//fase di lavoro (gratis, non viene inserita dall'utente, ma generata
		//automaticamente dal sistema

		//azioniRuoli (almeno 1 azione per fase)
		if(faseAzioniRuoli.getAzioniRuoli() == null || faseAzioniRuoli.getAzioniRuoli().isEmpty()) {
			errors.rejectValue(prefix + "azioniRuoli", "error.empty");
			return new boolean[] {false, false};
		}
		else {
			int counter = 0;
			boolean atLeastOneErrorAzione = false;
			boolean atLeastOnePartecipante = false;
			boolean atLeastOneTutor = false;

			Map<RuoloFSCEnum, Float> checkOrePartecipante = new HashMap<RuoloFSCEnum, Float>();

			for(AzioneRuoliEventoFSC aref : faseAzioniRuoli.getAzioniRuoli()) {
				boolean[] validationResults = validateAzioneRuoliFSC(aref, tipologiaEvento);

				//sommo tutte le ore dei partecipanti per verificare che abbia almeno 2 ore non frazionabili
				if(tipologiaEvento == TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE){
					for(RuoloOreFSC r : aref.getRuoli()) {
						if(r.getTempoDedicato() != null && r.getRuolo() != null && r.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE){
							if(checkOrePartecipante.containsKey(r.getRuolo())){
								float v = checkOrePartecipante.get(r.getRuolo());
								checkOrePartecipante.put(r.getRuolo(),v + r.getTempoDedicato());
							}else{
								checkOrePartecipante.put(r.getRuolo(),r.getTempoDedicato());
							}
						}
					}
				}

				//hasErrors
				if(validationResults[0]) {
					errors.rejectValue(prefix + "azioniRuoli["+counter+"]", "");
					atLeastOneErrorAzione = true;
				}
				if(validationResults[1]) {
					atLeastOnePartecipante = true;
				}
				if(validationResults[2]) {
					atLeastOneTutor = true;
				}
				counter++;
			}

			//cerco se ci sono partecipanti con meno di 2 ore
			if(checkOrePartecipante != null){
				Iterator<Entry<RuoloFSCEnum,Float>> iterator = checkOrePartecipante.entrySet().iterator();

				while (iterator.hasNext()) {
					Map.Entry<RuoloFSCEnum,Float> pairs = iterator.next();
					if(pairs.getValue() < 2)
						atLeastOneErrorAzione = true;
				 }
			}

			if(atLeastOneErrorAzione)
				errors.rejectValue(prefix + "azioniRuoli", "error.campi_con_errori_azione_ruoli"+tipologiaEvento);
			return new boolean[] {atLeastOnePartecipante, atLeastOneTutor};
		}
	}

	//validate azioniRuoli delle FasiAzioniRuoliFSC
	//ritorna un array di boolean -> boolean[] {hasError, hasPartecipante, hasTutor}
	private boolean[] validateAzioneRuoliFSC(AzioneRuoliEventoFSC azioneRuoli, TipologiaEventoFSCEnum tipologiaEvento) {

		//parte in comune
		if(azioneRuoli.getAzione() == null || azioneRuoli.getAzione().isEmpty())
			return new boolean[] {true, false, false};
		if(azioneRuoli.getObiettivoFormativo() == null)
			return new boolean[] {true, false, false};
		if(azioneRuoli.getRisultatiAttesi() == null || azioneRuoli.getRisultatiAttesi().isEmpty())
			return new boolean[] {true, false, false};
		if(azioneRuoli.getMetodiDiLavoro() == null || azioneRuoli.getMetodiDiLavoro().isEmpty())
			return new boolean[] {true, false, false};
		if(azioneRuoli.getRuoli() == null || azioneRuoli.getRuoli().isEmpty())
			return new boolean[] {true, false, false};

		//parti specifiche
		int numCoordinatori = 0;
		int numResponsabili = 0;
		boolean hasPartecipante = false;
		boolean hasTutor = false;
		if(tipologiaEvento != null) switch(tipologiaEvento) {

			//caso 1) tipologiaEvento == TRAINING_INDIVIDUALIZZATO
			// - almeno 1 ruolo partecipante, tutor per azione
			// - almeno 1 ora non frazionabile per il partecipante
			// - massimo 1 coordinatore per azione
			// - tutti campi obbligatori
			case TRAINING_INDIVIDUALIZZATO:

				for(RuoloOreFSC r : azioneRuoli.getRuoli()) {
					if(r.getTempoDedicato() == null)
						return new boolean[] {true, hasPartecipante, hasTutor};
					if(r.getRuolo() == null)
						return new boolean[] {true, hasPartecipante, hasTutor};
					else {
						if(r.getRuolo() == RuoloFSCEnum.COORDINATORE)
							numCoordinatori++;
						else if(r.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE) {
							if(r.getTempoDedicato() < 1)
								return new boolean[] {true, hasPartecipante, hasTutor};
							else
								hasPartecipante = true;
						}
						else if(r.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.TUTOR)
							hasTutor = true;
					}
				}
				if(numCoordinatori > 1)
					return new boolean[] {true, hasPartecipante, hasTutor};

			break;

			//caso 2) tipologiaEvento == GRUPPI_DI_MIGLIORAMENTO
			// - almeno una fase (azione per semplicità)
			// - almeno un ruolo partecipante per fase
			// - almeno 2 ore non frazionabili per partecipante, campo valorizzabile solo con multipli di 2
			// - massimo un coordinatore per fase
			// - tutti i campi obbligatori
			case GRUPPI_DI_MIGLIORAMENTO:

				for(RuoloOreFSC r : azioneRuoli.getRuoli()) {
					if(r.getTempoDedicato() == null)
						return new boolean[] {true, hasPartecipante, hasTutor};
					if(r.getRuolo() == null)
						return new boolean[] {true, hasPartecipante, hasTutor};
					else {
						if(r.getRuolo() == RuoloFSCEnum.COORDINATORE)
							numCoordinatori++;
						else if(r.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE) {
							if(r.getTempoDedicato() < 2)
								return new boolean[] {true, hasPartecipante, hasTutor};
							else if((r.getTempoDedicato() % 2) != 0f)
								return new boolean[] {true, hasPartecipante, hasTutor};
							hasPartecipante = true;
						}
					}
				}
				//caso particolare (qua le fasi sono come le azioni per le altre tipologie,
				//quindi controllo che nella riga ci sia almeno 1 ruolo Partecipante)
				if(numCoordinatori > 1)
					return new boolean[] {true, hasPartecipante, hasTutor};

			break;

			//caso3) tipologiaEvento == PROGETTI_DI_MIGLIORAMENTO
			// - controllo a seconda delle fasi inserite
			// - almeno 1 azione per fase
			// - almeno 1 ruolo partecipante per azione
			// - massimo un responsabile per fase
			// - tutti i campi obbligatori
			case PROGETTI_DI_MIGLIORAMENTO:

				for(RuoloOreFSC r : azioneRuoli.getRuoli()) {
					if(r.getTempoDedicato() == null)
						return new boolean[] {true, hasPartecipante, hasTutor};
					if(r.getRuolo() == null)
						return new boolean[] {true, hasPartecipante, hasTutor};
					else {
						if(r.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE) {
							hasPartecipante = true;
						}
						else if(r.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.RESPONSABILE)
							numResponsabili++;
					}
				}
				if(numResponsabili > 1)
					return new boolean[] {true, hasPartecipante, hasTutor};

			break;

			//caso4) tipologiaEvento == ATTIVITA_DI_RICERCA
			// - almeno 1 azione per fase
			// - almeno 1 ruolo partecipante per azione
			// - tutti i campi obbligatori
			case ATTIVITA_DI_RICERCA:

				for(RuoloOreFSC r : azioneRuoli.getRuoli()) {
					if(r.getTempoDedicato() == null)
						return new boolean[] {true, hasPartecipante, hasTutor};
					if(r.getRuolo() == null)
						return new boolean[] {true, hasPartecipante, hasTutor};
					else {
						if(r.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE) {
							hasPartecipante = true;
						}
					}
				}

			break;

			//caso5) tipologiaEvento == AUDIT_CLINICO_ASSISTENZIALE
			// - almeno 1 azione per fase
			// - almeno 1 ruolo partecipante per azione
			// - tutti i campi obbligatori
			case AUDIT_CLINICO_ASSISTENZIALE:

				for(RuoloOreFSC r : azioneRuoli.getRuoli()) {
					if(r.getTempoDedicato() == null)
						return new boolean[] {true, hasPartecipante, hasTutor};
					if(r.getRuolo() == null)
						return new boolean[] {true, hasPartecipante, hasTutor};
					else {
						if(r.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE) {
//							if(r.getTempoDedicato() < 2)
//								return new boolean[] {true, hasPartecipante, hasTutor};
							hasPartecipante = true;
						}
					}
				}

			break;

		}

		return new boolean[] {false, hasPartecipante, hasTutor};
	}

	//validate tabella ruoli FSC
	private boolean validateTabellaRuoliFSC(RiepilogoRuoliFSC riepilogoRuoli, TipologiaEventoFSCEnum tipologiaEvento) {

		//campi in comuni obbligatori (partecipanti > 0)
		if(riepilogoRuoli.getNumeroPartecipanti() <= 0)
			return true;

		//tipologiaEvento == TRAINING_INDIVIDUALIZZATO || ATTIVITA_DI_RICERCA nessun controllo

		if(tipologiaEvento != null)
			switch(tipologiaEvento) {

				//tipologiaEvento == GRUPPI_DI_MIGLIORAMENTO
				// - massimo 25 partecipanti per ruolo
				// - impegno complessivo minimo 8 ore totali per tutti i ruoli
				// - massimo un coordinatore
				case GRUPPI_DI_MIGLIORAMENTO:

					if(riepilogoRuoli.getNumeroPartecipanti() > 25)
						return true;
					if(riepilogoRuoli.getTempoDedicato() < 8f)
						return true;
					if(riepilogoRuoli.getRuolo() != null
							&& riepilogoRuoli.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.COORDINATORE
							&& riepilogoRuoli.getNumeroPartecipanti() > 1)
						return true;

				break;

				//tipologiaEvento == PROGETTI DI MIGLIORAMENTO
				// - impegno complessivo minimo 8 ore totali per ruolo PARTECIPANTE
				// - massimo un coordinatore
				case PROGETTI_DI_MIGLIORAMENTO:

					if(riepilogoRuoli.getRuolo() != null
							&& riepilogoRuoli.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE
							&& riepilogoRuoli.getTempoDedicato() < 8f)
						return true;
					if(riepilogoRuoli.getRuolo() != null
							&& riepilogoRuoli.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.COORDINATORE
							&& riepilogoRuoli.getNumeroPartecipanti() > 1)
						return true;
					if(riepilogoRuoli.getRuolo() != null
							&& riepilogoRuoli.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.RESPONSABILE
							&& riepilogoRuoli.getNumeroPartecipanti() > 1)
						return true;

				break;

				//tipologiaEvento == AUDIT_CLINICO_ASSISTENZIALE
				// - impegno complessivo minimo di 10 ore totali per ruolo PARTECIPANTE
				// - massimo un coordinatore
				case AUDIT_CLINICO_ASSISTENZIALE:

					if(riepilogoRuoli.getRuolo() != null
							&& riepilogoRuoli.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE
							&& riepilogoRuoli.getTempoDedicato() < 10f)
						return true;
					if(riepilogoRuoli.getRuolo() != null
							&& riepilogoRuoli.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.COORDINATORE
							&& riepilogoRuoli.getNumeroPartecipanti() > 1)
						return true;

				break;

				//tipologiaEvento == TRAINING_INDIVIDUALIZZATO
				// - massimo un coordinatore
				case TRAINING_INDIVIDUALIZZATO:

					if(riepilogoRuoli.getRuolo() != null
							&& riepilogoRuoli.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.COORDINATORE
							&& riepilogoRuoli.getNumeroPartecipanti() > 1)
						return true;

				break;

				//tipologiaEvento == ATTIVITA_DI_RICERCA
				// - massimo un coordinatore
				case ATTIVITA_DI_RICERCA:

					if(riepilogoRuoli.getRuolo() != null
							&& riepilogoRuoli.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.COORDINATORE
							&& riepilogoRuoli.getNumeroPartecipanti() > 1)
						return true;

				break;

			}

		return false;
	}

	//validate ProgrammaFAD
	private boolean validateProgrammaFAD(DettaglioAttivitaFAD attivita) {

		//per prima cose se ho un risultato atteso lo aggiungo al set
		risultatiAttesiUtilizzati.add(attivita.getRisultatoAtteso());

		//tutti i campi obbligatori
		if(attivita.getArgomento() == null || attivita.getArgomento().isEmpty()) {
			return true;
		}
		if(attivita.getDocente() == null) {
			return true;
		}
		if(attivita.getRisultatoAtteso() == null || attivita.getRisultatoAtteso().isEmpty()) {
			return true;
		}
		if(attivita.getObiettivoFormativo() == null) {
			return true;
		}
		if(attivita.getMetodologiaDidattica() == null) {
			return true;
		}
		if(attivita.getOreAttivita() <= 0f) {
			return true;
		}

		return false;
	}

}
