package it.tredi.ecm.web.validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

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
import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;
import it.tredi.ecm.dao.enumlist.NumeroPartecipantiPerCorsoEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiRESEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCBaseEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.RuoloPersonaEventoEnum;
import it.tredi.ecm.dao.enumlist.TematicheInteresseEnum;
import it.tredi.ecm.dao.enumlist.TipoMetodologiaEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFADEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.dao.enumlist.VerificaApprendimentoFSCEnum;
import it.tredi.ecm.dao.enumlist.VerificaApprendimentoRESEnum;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.service.controller.EventoServiceController;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoRESProgrammaGiornalieroWrapper;
import it.tredi.ecm.web.bean.EventoRESTipoDataProgrammaGiornalieroEnum;
import it.tredi.ecm.web.bean.EventoWrapper;
import it.tredi.ecm.web.validator.bean.ValidateEventoFadInfo;
import it.tredi.ecm.web.validator.bean.ValidateEventoResInfo;
import it.tredi.ecm.web.validator.bean.ValidateFasiAzioniRuoliFSCInfo;

// EVENTO_VERSIONE
@Component
public class EventoValidatorVersioneTre {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventoValidatorVersioneTre.class);

	@Autowired private EcmProperties ecmProperties;
	//@Autowired private FileValidator fileValidator; // ERM014045 - no firm
	@Autowired private FileService fileService;
	@Autowired private EventoService eventoService;
	@Autowired private EventoServiceController eventoServiceController;
	@Autowired private EventoValidator eventoValidator;
	@Autowired private PersonaEventoValidator personaEventoValidator;

	//Rimosso perche' il validatore e' un singleton e queste variabili vengono condivise da piu' tread
//	private Set<String> risultatiAttesiUtilizzati;
//	private boolean alertResDocentiPartecipanti = false;

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

		/* TEMATICHE SPECIALI DI INTERESSE NAZIONALE o REGIONALE
		 * selectpicker
		 * */
		if(evento.getContenutiEvento() != null && evento.getContenutiEvento() == ContenutiEventoEnum.ALTRO) {
			if(evento.getTematicaInteresse() == null)
				errors.rejectValue(prefix + "tematicaInteresse", "error.empty");

			/*
			 * A seconda del tipo di TEMATICA SPECIALE DI INTERESSE NAZIONALE o REGIONALE -> SI VINCOLA IL VALORE DELL'OBIETTIVO NAZIONALE
			 * */
			if(evento.getTematicaInteresse() != null && evento.getTematicaInteresse() != TematicheInteresseEnum.NON_RIGUARDA_UNA_TEMATICA_SPECIALE && evento.getObiettivoNazionale() != null) {
				if(!evento.getTematicaInteresse().getObiettiviNazionali().contains(evento.getObiettivoNazionale().getCodiceCogeaps()))
					errors.rejectValue(prefix + "obiettivoRegionale", "error.valore_non_consentito");
			}
		}

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
		 * passati i minGiorni un evento non può essere anticipato e non gli può essere cambiato il numero di date
		 * controllo effettutato solo da VALIDATO a VALIDATO
		 * -------------------
		 * la segreteria gestisce le date come vuole
		 * -------------------
		 * le riedizioni si devono svolgere nell'anno solare di riferimento del padre (anno data fine) e non possono iniziare prima del padre
		 * */
		int minGiorni;
		int giorniPossibilitaPosticipoDaInizioEventoProvider;
		if(evento.getProvider().getTipoOrganizzatore().getGruppo().equals("A")) {
			minGiorni = ecmProperties.getGiorniMinEventoProviderA();
			giorniPossibilitaPosticipoDaInizioEventoProvider = ecmProperties.getGiorniPossibilitaPosticipoDaInizioEventoProviderA();
		} else {
			minGiorni = ecmProperties.getGiorniMinEventoProviderB();
			giorniPossibilitaPosticipoDaInizioEventoProvider = ecmProperties.getGiorniPossibilitaPosticipoDaInizioEventoProviderB();
		}
		if(evento.isRiedizione())
			minGiorni = ecmProperties.getGiorniMinEventoRiedizione();

		if(evento.getStato() == EventoStatoEnum.BOZZA) {
			if(evento.isRiedizione()) {
				if(evento.getDataInizio() == null)
					errors.rejectValue(prefix + "dataInizio", "error.empty");
				else {
					if(!Utils.getAuthenticatedUser().isSegreteria()) {
						if(
							evento.getDataInizio().isBefore(LocalDate.now().plusDays(minGiorni))
							// 08/01/2018
							// La data di inizio di una riedizione deve ricadere nello stesso anno solare della data di fine dell'Evento che viene rieditato.
							// da non applicare ad eventi FSC
							//||
							//evento.getDataInizio().getYear() != evento.getEventoPadre().getDataFine().getYear()
						)
							//Non è possibile inserire una riedizione di un Evento entro 10 giorni dalla data del suo inizio
							errors.rejectValue(prefix + "dataInizio", "error.data_inizio_riedizione_non_valida");
					}
					if(evento.getDataInizio().isBefore(evento.getEventoPadre().getDataInizio())) {
						//La data di inizio di una riedizione non può essere antecedente a quella dell'evento rieditato
						errors.rejectValue(prefix + "dataInizio", "error.data_inizio_antecedente_al_padre");
					}
					if(!(evento instanceof EventoFSC)) {
						if(
								// 08/01/2018
								// La data di inizio di una riedizione deve ricadere nello stesso anno solare della data di fine dell'Evento che viene rieditato.
								// da non applicare ad eventi FSC
								//evento.getDataInizio().isBefore(evento.getEventoPadre().getDataInizio())
								//||
								evento.getDataInizio().getYear() != evento.getEventoPadre().getDataFine().getYear()
						) {
							// La data di inizio di una riedizione deve ricadere nello stesso anno solare della data di fine dell'Evento che viene rieditato.
							errors.rejectValue(prefix + "dataInizio", "error.data_inizio_riedizione_non_valida_per_anno_padre");
						}
						//se l'evento è un riedizione il numero delle date deve coincidere
						if(evento instanceof EventoRES) {
							//numero date da rispettare
							checkDateInizioFine(evento, evento.getEventoPadre(), prefix, errors);
						}
					}
				}
			}
			else {
				if(evento.getDataInizio() == null)
					errors.rejectValue(prefix + "dataInizio", "error.empty");
				else if(evento.getDataInizio().isBefore(LocalDate.now().plusDays(minGiorni)) && !Utils.getAuthenticatedUser().isSegreteria())
					errors.rejectValue(prefix + "dataInizio", "error.data_inizio_non_valida");
			}
		}
		else {
			//impedisce anticipazioni di date di eventi validati fuori dal tempo massimo e un cambio di numero di date confrontando con l'evento
			//sul DB non ancora modificato
			Evento eventoDaDB = eventoService.getEvento(evento.getId());
			if(evento.getDataInizio() == null) {
				errors.rejectValue(prefix + "dataInizio", "error.empty");
			} else {
				//SOLO se ho cambiato la data faccio il controllo sul minGiorni (e non sono segreteria)
				if(!evento.getDataInizio().isEqual(eventoDaDB.getDataInizio()) && !Utils.getAuthenticatedUser().isSegreteria()) {
					if(evento.getDataInizio().isBefore(eventoDaDB.getDataInizio())) {
						//data anticipata
						if(evento.getDataInizio().isBefore(LocalDate.now().plusDays(minGiorni)))
							errors.rejectValue(prefix + "dataInizio", "error.data_inizio_non_valida");
					} else {
						//data posticipata è possibile entro 4 giorni dall'inizio dell'evento, per il provider di tipo A, e entro 10 giorni per il provider di tipo B
						if(eventoDaDB.getDataInizio().isBefore(LocalDate.now().plusDays(giorniPossibilitaPosticipoDaInizioEventoProvider)))
							errors.rejectValue(prefix + "dataInizio", "error.data_inizio_posticipo_non_valida");
					}
				}
			}
			//impedisce di unificare le date di inizio / fine se l'evento ha riedizioni
			if(eventoService.existRiedizioniOfEventoId(evento.getId())) {
				//numero date da rispettare
				checkDateInizioFine(evento, eventoDaDB, prefix, errors);
			}
			/* escludere dal controllo le riedizioni FSC */
			if(evento.isRiedizione()) {
				if(evento.getDataInizio().isBefore(evento.getEventoPadre().getDataInizio())) {
					//La data di inizio di una riedizione non può essere antecedente a quella dell'evento rieditato
					errors.rejectValue(prefix + "dataInizio", "error.data_inizio_antecedente_al_padre");
				}

				if(!(evento instanceof EventoFSC)) {
					if(
							// 08/01/2018
							// La data di inizio di una riedizione deve ricadere nello stesso anno solare della data di fine dell'Evento che viene rieditato.
							// da non applicare ad eventi FSC
							//evento.getDataInizio().isBefore(evento.getEventoPadre().getDataInizio())
							//||
							evento.getDataInizio().getYear() != evento.getEventoPadre().getDataFine().getYear()
					) {
						// La data di inizio di una riedizione deve ricadere nello stesso anno solare della data di fine dell'Evento che viene rieditato.
						errors.rejectValue(prefix + "dataInizio", "error.data_inizio_riedizione_non_valida_per_anno_padre");
					}
					//se l'evento è un riedizione il numero delle date deve coincidere
					if(evento instanceof EventoRES) {
						//numero date da rispettare
						checkDateInizioFine(evento, evento.getEventoPadre(), prefix, errors);
					}
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
			boolean atLeastOneErrorPersonaEventoDuplicata = !personaEventoValidator.validateAnagraficaBaseEventoWithSvolgeAttivitaDiDocenza(evento.getResponsabili(), errors, "responsabiliScientifici");
			if(atLeastOneErrorPersonaEventoDuplicata) {
				errors.rejectValue(prefix + "responsabili", "error.responsabili_scientifici_duplicati");
			} else {
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
		// ERM014977 - getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia va testato solo se sponsorizato
		// e contenutiEvento == ALIMENTAZIONE_PRIMA_INFANZIA
		if(evento.getContenutiEvento() != null
				&& evento.getContenutiEvento() == ContenutiEventoEnum.ALIMENTAZIONE_PRIMA_INFANZIA
				&& evento.getEventoSponsorizzato() != null
				&& evento.getEventoSponsorizzato() == true) {

			if(evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() == null)
				errors.rejectValue(prefix + "eventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia", "error.empty");
			else if(evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() == true
						&& (evento.getEventoSponsorizzato() == null || evento.getEventoSponsorizzato() == false)
					)
				errors.rejectValue(prefix + "eventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia", "error.evento_deve_essere_sponsorizzato");


			/* AUTOCERTIFICAZIONE ASSENZA SPONSOR PRIMA INFANZIA
			 * (campo obbligatorio se contenutiEvento == ALIMENTAZIONE_PRIMA_INFANZIA
			 * e eventoSponsorizzatoDaAziendeAlimentiPrimainfanzia == true)
			 * file allegato
			 * */
			if(evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() != null
					&& evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() == false
					&& evento.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia() == null){
				errors.rejectValue("autocertificazioneAssenzaAziendeAlimentiPrimaInfanzia", "error.empty");
			}

			// ERM014045 - no firma
			/*
			if(evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() != null
					&& evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() == false
					&& evento.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia() != null)
			{
				if(!fileValidator.validateFirmaCF(evento.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia(), evento.getProvider().getId()))
					errors.rejectValue("autocertificazioneAssenzaAziendeAlimentiPrimaInfanzia", "error.codiceFiscale.firmatario");
			}
			*/

			/* AUTOCERTIFICAZIONE DI AUTORIZZAZIONE DEL MINISTERO
			 * (campo obbligatorio se contenutiEvento == ALIMENTAZIONE_PRIMA_INFANZIA
			 * e eventoSponsorizzatoDaAziendeAlimentiPrimainfanzia == false)
			 * file allegato
			 * */
			if(evento.getEventoSponsorizzato() != null
					&& evento.getEventoSponsorizzato() == true
					&& evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() != null
					&& evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() == true
					&& evento.getAutocertificazioneAutorizzazioneMinisteroSalute() == null){
				errors.rejectValue("autocertificazioneAutorizzazioneMinisteroSalute", "error.empty");
			}

			// ERM014045 - no firma
			/*
			if(evento.getEventoSponsorizzato() != null
					&& evento.getEventoSponsorizzato() == true
					&& evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() != null
					&& evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() == true
					&& evento.getAutocertificazioneAutorizzazioneMinisteroSalute() != null)
			{
					if(!fileValidator.validateFirmaCF(evento.getAutocertificazioneAutorizzazioneMinisteroSalute(), evento.getProvider().getId()))
						errors.rejectValue("autocertificazioneAutorizzazioneMinisteroSalute", "error.codiceFiscale.firmatario");
			}
			*/
		}

		// ERM014977-2  getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia va testato solo se sponsorizato
		// e contenutiEvento == ALIMENTAZIONE_PRIMA_INFANZIA e se no sponsorizato
		if(evento.getContenutiEvento() != null
				&& evento.getContenutiEvento() == ContenutiEventoEnum.ALIMENTAZIONE_PRIMA_INFANZIA
				&& evento.getEventoSponsorizzato() != null
				&& evento.getEventoSponsorizzato() == false
				&& evento.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia() == null){
			errors.rejectValue("autocertificazioneAssenzaAziendeAlimentiPrimaInfanzia", "error.empty");
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
		}

		// ERM014045 - no firma
		/*
		if(evento.getAltreFormeFinanziamento() != null
				&& evento.getAltreFormeFinanziamento() == true
				&& evento.getContrattiAccordiConvenzioni() != null)
		{
			if(!fileValidator.validateFirmaCF(evento.getContrattiAccordiConvenzioni(), evento.getProvider().getId()))
				errors.rejectValue("contrattiAccordiConvenzioni", "error.codiceFiscale.firmatario");
		}
		*/

		/* AUTOCERTIFICAZIONE ASSENZA FINANZIAMENTI (facoltativo solo per i provider del gruppo A)
		 * (campo obbligatorio se altreFormeFinanziamento == false)
		 * file allegato
		 * */
		if(!evento.getProvider().isGruppoA()){
			if(evento.getAltreFormeFinanziamento() != null
					&& evento.getAltreFormeFinanziamento() == false
					&& evento.getAutocertificazioneAssenzaFinanziamenti() == null){
				errors.rejectValue("autocertificazioneAssenzaFinanziamenti", "error.empty");
			}

			// ERM014045 - no firma
			/*
			if(evento.getAltreFormeFinanziamento() != null
					&& evento.getAltreFormeFinanziamento() == false
					&& evento.getAutocertificazioneAssenzaFinanziamenti() != null)
			{
				if(!fileValidator.validateFirmaCF(evento.getAutocertificazioneAssenzaFinanziamenti(), evento.getProvider().getId()))
					errors.rejectValue("autocertificazioneAssenzaFinanziamenti", "error.codiceFiscale.firmatario");
			}
			*/
		}

		/* RADIO EVENTO PARTNER (campo obbligatorio)
		 * radio
		 * */
		if(evento.getEventoAvvalePartner() == null) {
			errors.rejectValue(prefix + "eventoAvvalePartner", "error.empty");
		} else {
			/* PARTNERS (campo obbligatorio se eventoAvvalePartner == true)
			 * campo complesso di tipo Partner ripetibile
			 * almeno 1
			 * */
			if(evento.getEventoAvvalePartner() == true) {
				if((evento.getPartners() == null || evento.getPartners().isEmpty())) {
					errors.rejectValue(prefix + "partners", "error.empty");
				} else {
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
			}
		}

		/* DICHIARAZIONE ASSENZA CONFLITTO DI INTERESSE (campo obbligatorio)
		 * file allegato
		 * */

		if(evento.getDichiarazioneAssenzaConflittoInteresse() == null){
			errors.rejectValue("dichiarazioneAssenzaConflittoInteresse", "error.empty");
		}else{
			// ERM014045 - no firma
			/*
			if(!fileValidator.validateFirmaCF(evento.getDichiarazioneAssenzaConflittoInteresse(), evento.getProvider().getId()))
				errors.rejectValue("dichiarazioneAssenzaConflittoInteresse", "error.codiceFiscale.firmatario");
				*/
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

		/* CREDITI (campo obbligatorio/autocompilato)
		 * il campo viene autocompilato
		 * a questo punto l'utente può scegliere di accettare il valore
		 * o inserirne uno lui -> se NON accetta il valore il campo crediti che deve inserire è obbligatorio
		 * campo numerico (Float)
		 * */
		if(evento.getCrediti() == null)
			errors.rejectValue(prefix + "crediti", "error.empty_crediti");

		if(!evento.getConfermatiCrediti().booleanValue() && evento.getCrediti() != null && (evento.getMotivazioneCrediti() == null || evento.getMotivazioneCrediti().isEmpty())) {
			errors.rejectValue(prefix + "motivazioneCrediti", "error.empty");
		}
	}

	private void checkDateInizioFine(Evento evento, Evento evento2, String prefix, Errors errors) {
		//data inizio fine uguali
		if(evento2.getDataFine().isEqual(evento2.getDataInizio())
				&& !evento.getDataFine().isEqual(evento.getDataInizio())) {
			errors.rejectValue(prefix + "dataInizio", "error.date_non_separabili");
			errors.rejectValue(prefix + "dataFine", "error.date_non_separabili");
		}
		//data inizio fine non uguali
		else if (!evento2.getDataFine().isEqual(evento2.getDataInizio())
				&& evento.getDataFine().isEqual(evento.getDataInizio())) {
			errors.rejectValue(prefix + "dataInizio", "error.date_non_unificabili");
			errors.rejectValue(prefix + "dataFine", "error.date_non_unificabili");
		}
	}

	//validate RES
	private void validateRES(EventoRES evento, EventoWrapper wrapper, Errors errors, String prefix) throws Exception {
		ValidateEventoResInfo validateEventoResInfo = new ValidateEventoResInfo();

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
		 * -------------
		 * riedizione stesso anno solare data inizio, che deve coincidere con la data di fine dell'eventoPadre
		 * */
		if(evento.getDataFine() == null)
			errors.rejectValue(prefix + "dataFine", "error.empty");
		else if(evento.getDataInizio() != null && (evento.getDataFine().isBefore(evento.getDataInizio())))
			errors.rejectValue(prefix + "dataFine", "error.data_fine_non_valida");
		else {
			if(evento.isRiedizione() && !Utils.getAuthenticatedUser().isSegreteria()) {
				if(evento.getDataInizio() != null
						&& (evento.getDataFine().getYear() != evento.getEventoPadre().getDataFine().getYear()))
					errors.rejectValue(prefix + "dataFine", "error.data_fine_riedizione_non_valida");
			}
			else {
				if(evento.getDataInizio() != null && (evento.getDataFine().getYear() != evento.getDataInizio().getYear()))
					errors.rejectValue(prefix + "dataFine", "error.data_fine_res_non_valida");
			}
		}

		/* DATE INTERMEDIE (campo opzionale)
		 * le date intermedie devono essere strettamente comprese tra quella di inizio e quella di fine
		 * non possono essere ripetute
		 * -------------
		 * per quanto riguarda la riedizione, il padre e il rieditato devono evere lo stesso numero di date intermedie
		 * */
		LocalDate prevLd = null;
		boolean sorted = true;
		if(evento.getDataInizio() != null && evento.getDataFine() != null) {
			//controllo se la lista delle date è ordinata...
			//altimenti do errore, dovuto al fatto che le riedizioni invertirebbero i programmi
			//se la lista non è perfettamente ordinata
			for (LocalDate ld : evento.getDateIntermedie()) {
				if(prevLd != null) {
					sorted = ld.isAfter(prevLd);
				}
				if(sorted)
					prevLd = ld;
				if(ld.isAfter(evento.getDataFine()) || ld.isEqual(evento.getDataFine()) || ld.isBefore(evento.getDataInizio()) || ld.isEqual(evento.getDataInizio()) || !sorted) {
					//ciclo alla ricerca di questa data per farmi dare le chiavi nella mappa
					Set<Long> keys = new HashSet<Long>();
					for(Entry<Long, EventoRESProgrammaGiornalieroWrapper> entry : wrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().entrySet()) {
						if(entry.getValue().getTipoData() == EventoRESTipoDataProgrammaGiornalieroEnum.INTERMEDIA
								&& entry.getValue().getProgramma().getGiorno() != null && entry.getValue().getProgramma().getGiorno().isEqual(ld)) {
							keys.add(entry.getKey());
						}
					}
					//genero gli errori
					for(Long l : keys) {
						if(!sorted)
							errors.rejectValue("eventoRESDateProgrammiGiornalieriWrapper.sortedProgrammiGiornalieriMap[" + l + "]", "error.ordinare_le_date_intermedie");
						else
							errors.rejectValue("eventoRESDateProgrammiGiornalieriWrapper.sortedProgrammiGiornalieriMap[" + l + "]", "error.data_intermedia_res_non_valida");
					}
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

		/* NUMERO DEI PARTECIPANTI PER CORSO (campo obbligatorio se la tipologia è CORSO_AGGIORNAMENTO) */
		if(evento.getTipologiaEventoRES() != null
				&& evento.getTipologiaEventoRES() == TipologiaEventoRESEnum.CORSO_AGGIORNAMENTO
				&& evento.getNumeroPartecipantiPerCorso() == null) {
			errors.rejectValue(prefix + "numeroPartecipantiPerCorso", "error.empty");
		}

		/* NUMERO DEI PARTECIPANTI (campo obbligatorio)
		 * campo valore numerico
		 * se la tipologia dell'evento è CONVEGNO_CONGRESSO -> minimo 200 partecipanti
		 * se la tipologia dell'evento è WORKSHOP_SEMINARIO -> massimo 100 partecipanti
		 * se la tipologia dell'evento è CORSO_AGGIORNAMENTO -> massimo 200 partecipanti
		 * */
		if(evento.getNumeroPartecipanti() == null)
			errors.rejectValue(prefix + "numeroPartecipanti", "error.empty");
		else if(evento.getNumeroPartecipanti() <= 0)
			errors.rejectValue(prefix + "numeroPartecipanti", "error.numero_positivo");
		else if(evento.getTipologiaEventoRES() != null
				&& evento.getTipologiaEventoRES() == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO
				&& evento.getNumeroPartecipanti() < ecmProperties.getNumeroMinimoPartecipantiConvegnoCongressoRES())
			errors.rejectValue(prefix + "numeroPartecipanti", "error.pochi_partecipanti200");
		else if(evento.getTipologiaEventoRES() != null
				&& evento.getTipologiaEventoRES() == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO
				&& evento.getNumeroPartecipanti() > ecmProperties.getNumeroMassimoPartecipantiWorkshopSeminarioRES())
			errors.rejectValue(prefix + "numeroPartecipanti", "error.troppi_partecipanti100");
		else if(evento.getTipologiaEventoRES() != null
				&& evento.getTipologiaEventoRES() == TipologiaEventoRESEnum.CORSO_AGGIORNAMENTO) {
			if(evento.getNumeroPartecipantiPerCorso() != null) {
				if(evento.getNumeroPartecipantiPerCorso() == NumeroPartecipantiPerCorsoEnum.CORSO_AGGIORNAMENTO_FINO_100_PARTECIPANTI && evento.getNumeroPartecipanti() > 100) {
					errors.rejectValue(prefix + "numeroPartecipanti", "error.partecipanti_corso_aggiornamento_fino_100");
				} else if (evento.getNumeroPartecipantiPerCorso() == NumeroPartecipantiPerCorsoEnum.CORSO_AGGIORNAMENTO_DA_101_A_200_PARTECIPANTI && (evento.getNumeroPartecipanti() < 101 || evento.getNumeroPartecipanti() > 200)) {
					errors.rejectValue(prefix + "numeroPartecipanti", "error.partecipanti_corso_aggiornamento_da_101_fino_200");
				}
			}
		}

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
		 * setto la variabile boolean globale per il controllo delle metodologie interattive per WORKSHOP/SEMINARIO e CORSO DI AGGIORNAMENTO (ratio 1/25)
		 * */
		//risultatiAttesiUtilizzati = new HashSet<String>();
		validateEventoResInfo.setRisultatiAttesiUtilizzati(new HashSet<String>());
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
				validateProgrammaRES(validateEventoResInfo, pgr, errors, "eventoRESDateProgrammiGiornalieriWrapper.sortedProgrammiGiornalieriMap["+ key +"].programma.", evento.getTipologiaEventoRES(), evento.getNumeroPartecipanti(), evento.getDocenti());
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
					//if(!ra.isEmpty() && !risultatiAttesiUtilizzati.contains(ra)) {
					if(!ra.isEmpty() && !validateEventoResInfo.getRisultatiAttesiUtilizzati().contains(ra)) {
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
		// ERM014045 - no firm
		/*
		if(evento.getDocumentoVerificaRicaduteFormative() != null && !evento.getDocumentoVerificaRicaduteFormative().isNew()){
			if(!fileValidator.validateFirmaCF(evento.getDocumentoVerificaRicaduteFormative(), evento.getProvider().getId()))
				errors.rejectValue("documentoVerificaRicaduteFormative", "error.codiceFiscale.firmatario");
		}
		*/
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
		 * -------------
		 * riedizione stesso anno solare data inizio, che deve coincidere con la data di fine dell'eventoPadre
		 * */
		if(evento.getDataFine() == null)
			errors.rejectValue(prefix + "dataFine", "error.empty");
		else if(evento.getDataFine() != null && (evento.getDataFine().isAfter(ecmProperties.getEventoFscDataFineMaxTriennio())))
			errors.rejectValue(prefix + "dataFine", "error.fsc_data_fine_non_appartenente_triennio_attuale");
		else if(evento.getDataInizio() != null && (evento.getDataFine().isBefore(evento.getDataInizio())))
			errors.rejectValue(prefix + "dataFine", "error.data_fine_non_valida");
		else {
			if(evento.isRiedizione() && !Utils.getAuthenticatedUser().isSegreteria()) {
				if(evento.getDataInizio() != null
						&& (evento.getDataFine().getYear() != evento.getEventoPadre().getDataFine().getYear()))
					errors.rejectValue(prefix + "dataFine", "error.data_fine_riedizione_non_valida");
			}
			else {
				//DataInizio e TipologiaEventoFSC sono obbligatori se non compilati non effettuo il controllo sulla DataFine
				if(evento.getDataInizio() != null && evento.getTipologiaEventoFSC() != null)
					if(evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA && evento.getDataFine().isAfter(evento.getDataInizio().plusDays(ecmProperties.getGiorniMaxEventoFscVersione2AttivitaDiRicerca()))) {
						errors.rejectValue(prefix + "dataFine", "error.numero_massimo_giorni_evento_fsc_attivita_di_ricerca");
					} else if(evento.getTipologiaEventoFSC() != TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA && evento.getDataFine().isAfter(evento.getDataInizio().plusDays(ecmProperties.getGiorniMaxEventoFscVersione2()))) {
						errors.rejectValue(prefix + "dataFine", "error.numero_massimo_giorni_evento_fsc_non_attivita_di_ricerca");
					}
			}
		}


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

		/* TIPOLOGIE DI SPERIMENTAZIONE (campo obbligatorio se tipologiaEvento == ATTIVITA_DI_RICERCA)
		 * radio
		 * */
		if(evento.getTipologiaEventoFSC() != null
				&& evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA
				&& evento.getTipologiaSperimentazione() == null)
			errors.rejectValue(prefix + "tipologiaSperimentazione", "error.empty");

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

		//vale per la versione 2 qui stiamo validando solo la versione 2
		if(evento.getEsperti() != null && evento.getEsperti().size() > ecmProperties.getNumeroMassimoEspertiEvento())
			errors.rejectValue(prefix + "esperti", "error.troppi_esperti3");
		if(evento.getCoordinatori() != null && evento.getCoordinatori().size() > ecmProperties.getNumeroMassimoCoordinatoriEvento())
			errors.rejectValue(prefix + "coordinatori", "error.troppi_coordinatori3");

		if(evento.getTipologiaEventoFSC() != null && evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA) {
			if(evento.getInvestigatori() == null || evento.getInvestigatori().isEmpty())
				errors.rejectValue(prefix + "investigatori", "error.empty");
		}

		//È prevista la redazione di un documento conclusivo quale ad es. linee guida, procedure, protocolli, indicazioni operative?
		if(evento.getPrevistaRedazioneDocumentoConclusivo() == null)
			errors.rejectValue(prefix + "previstaRedazioneDocumentoConclusivo", "error.empty");
		//È presente un Tutor esperto esterno che validi le attività del gruppo?
		if(evento.getPresenteTutorEspertoEsternoValidatoreAttivita() == null)
			errors.rejectValue(prefix + "presenteTutorEspertoEsternoValidatoreAttivita", "error.empty");

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
			EventoVersioneEnum versioneEvento = eventoServiceController.versioneEvento(evento);
			List<RuoloFSCEnum> listRuoloFSCEnumPerResponsabiliScientifici = eventoService.getListRuoloFSCEnumPerResponsabiliScientifici(evento);
			List<RuoloFSCEnum> listRuoloFSCEnumPerCoordinatori = eventoService.getListRuoloFSCEnumPerCoordinatori(evento);
			List<RuoloFSCEnum> listRuoloFSCEnumPerEsperti = eventoService.getListRuoloFSCEnumPerEsperti(evento);
			//gestione particolare tipologiaEvento == PROGETTI_DI_MIGLIORAMENTO
			//eseguo i controlli solo alle fasiDaInserire specificate da fasiDaInserire
			if(evento.getTipologiaEventoFSC() != null
					&& evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO
					&& evento.getFasiDaInserire() != null) {
				for(FaseAzioniRuoliEventoFSCTypeA far : evento.getFasiAzioniRuoli()) {
					//validazione solo se la fase è abilitata
					if(evento.getFasiDaInserire().getFasiAbilitate().contains(far.getFaseDiLavoro())) {
						//return new boolean[] {atLeastOnePartecipante, atLeastOneTutor};
						ValidateFasiAzioniRuoliFSCInfo validationResults = validateFasiAzioniRuoliFSC(far, errors, "programmaEventoFSC["+counter+"].", evento.getTipologiaEventoFSC(), versioneEvento, listRuoloFSCEnumPerResponsabiliScientifici, listRuoloFSCEnumPerCoordinatori, listRuoloFSCEnumPerEsperti);
						if(validationResults.isAtLeastOnePartecipante())
							atLeastOnePartecipante = true;
						if(validationResults.isAtLeastOneTutor())
							atLeastOneTutor = true;
					}
					counter++;
				}
			}
			//gestione di default
			else {
				for(FaseAzioniRuoliEventoFSCTypeA far : evento.getFasiAzioniRuoli()) {
					//return new boolean[] {atLeastOnePartecipante, atLeastOneTutor};
					ValidateFasiAzioniRuoliFSCInfo validationResults = validateFasiAzioniRuoliFSC(far, errors, "programmaEventoFSC["+counter+"].", evento.getTipologiaEventoFSC(), versioneEvento, listRuoloFSCEnumPerResponsabiliScientifici, listRuoloFSCEnumPerCoordinatori, listRuoloFSCEnumPerEsperti);
					if(validationResults.isAtLeastOnePartecipante())
						atLeastOnePartecipante = true;
					if(validationResults.isAtLeastOneTutor())
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
				errors.rejectValue(prefix + "riepilogoRuoli", "error.errore_tabella_fsc_versione2_"+evento.getTipologiaEventoFSC());
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
			errors.rejectValue(prefix + "riepilogoRuoli", "error.errore_tabella_fsc"+evento.getTipologiaEventoFSC().name());
		else if(evento.getTipologiaEventoFSC() != null
				&& evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE
				&& evento.getNumeroPartecipanti() > ecmProperties.getNumeroMassimoPartecipantiAuditClinicoFSC())
			errors.rejectValue(prefix + "riepilogoRuoli", "error.errore_tabella_fsc"+evento.getTipologiaEventoFSC().name());
//		else if(evento.getTipologiaEventoFSC() != null
//				&& evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO
//				&& evento.getNumeroPartecipanti() > (evento.getNumeroTutor() * 5))
//			errors.rejectValue(prefix + "riepilogoRuoli", "error.errore_tabella_fsc"+evento.getTipologiaEventoFSC().name());

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
		if(evento.getVerificaApprendimento() == null || evento.getVerificaApprendimento().isEmpty()) {
			errors.rejectValue(prefix + "verificaApprendimento", "error.empty");
		} else {

			if (evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO
					&& !evento.getVerificaApprendimento().contains(VerificaApprendimentoFSCEnum.RAPPORTO_CONCLUSIVO)) {
				errors.rejectValue(prefix + "verificaApprendimento",
						"error.evento_tipo_NON_TRAINING_INDIVIDUALIZZATO_manca_RAPPORTO_CONCLUSIVO");
			}

			if (evento.getTipologiaEventoFSC() != TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO
					&& !evento.getVerificaApprendimento().contains(VerificaApprendimentoFSCEnum.RELAZIONE_FIRMATA)) {
				errors.rejectValue(prefix + "verificaApprendimento",
						"error.evento_tipo_TRAINING_INDIVIDUALIZZATO_manca_RELAZIONE_FIRMATA");
			}
		}

		/*
		 * INDICATORE EFFICACIA FORMATIVA (campo obbligatorio se tipologiaEvento ==
		 * PROGETTI_DI_MIGLIORAMENTO) campo testuale almeno 1 char
		 */
		if (evento.getTipologiaEventoFSC() != null
				&& evento.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO
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
		ValidateEventoFadInfo validateEventoFadInfo = new ValidateEventoFadInfo();
		/* DATA FINE (campo obbligatorio)
		 * e l'evento non può avere durata superiore a 365 giorni
		 * -------------
		 * riedizione stesso anno solare data inizio, che deve coincidere con la data di fine dell'eventoPadre
		 * */
		if(evento.getDataFine() == null)
			errors.rejectValue(prefix + "dataFine", "error.empty");
		else if(evento.getDataFine() != null && (evento.getDataFine().isAfter(ecmProperties.getEventoFadDataFineMaxTriennio()))) {
			//Questo non funziona perche' non è stato usato nella view il #fields.errors ma una mappa personalizzata
//			int year = ecmProperties.getEventoFadDataFineMaxTriennio().getYear();
//			String[] values = new String[]{String.valueOf(year-2), String.valueOf(year)};
//			errors.rejectValue(prefix + "dataFine", "error.data_fine_non_appartenente_triennio_attuale_with_args", values, null);
			//Quindi utilizzo un messaggio senza parametri
			errors.rejectValue(prefix + "dataFine", "error.fad_data_fine_non_appartenente_triennio_attuale");
		} else if(evento.getDataInizio() != null && (evento.getDataFine().isBefore(evento.getDataInizio())))
			errors.rejectValue(prefix + "dataFine", "error.data_fine_non_valida");
		else {
			if(evento.isRiedizione() && !Utils.getAuthenticatedUser().isSegreteria()) {
				if(evento.getDataInizio() != null
						&& (evento.getDataFine().getYear() != evento.getEventoPadre().getDataFine().getYear()))
					errors.rejectValue(prefix + "dataFine", "error.data_fine_riedizione_non_valida");
			}
			else {
				if(evento.getDataInizio() != null && evento.getDataFine().isAfter(evento.getDataInizio().plusDays(ecmProperties.getGiorniMaxEventoFAD())))
					errors.rejectValue(prefix + "dataFine", "error.numero_massimo_giorni_evento_fad365");
			}
		}
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
		//risultatiAttesiUtilizzati = new HashSet<String>();
		validateEventoFadInfo.setRisultatiAttesiUtilizzati(new HashSet<String>());
		if(evento.getProgrammaFAD() == null || evento.getProgrammaFAD().isEmpty())
			errors.rejectValue(prefix + "programmaFAD", "error.empty");
		else {
			int counter = 0;
			boolean atLeastOneErrorAttivita = false;
			for(DettaglioAttivitaFAD daf : evento.getProgrammaFAD()) {
				boolean hasError = validateProgrammaFAD(validateEventoFadInfo, daf);
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
				//if(!ra.isEmpty() && !risultatiAttesiUtilizzati.contains(ra)) {
				if(!ra.isEmpty() && !validateEventoFadInfo.getRisultatiAttesiUtilizzati().contains(ra)) {
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
			// ERM014045 - no firm
			/*
			if(!fileValidator.validateFirmaCF(evento.getRequisitiHardwareSoftware(), evento.getProvider().getId()))
				errors.rejectValue("requisitiHardwareSoftware", "error.codiceFiscale.firmatario");
				*/
		}

		/* ACCESSO PIATTAFORMA (serie di campi obbligatori)
		 * 3 campi testuali
		 * */
		if(evento.getTipologiaEventoFAD() == null || evento.getTipologiaEventoFAD() != TipologiaEventoFADEnum.APPRENDIMENTO_INDIVIDUALE_NO_ONLINE) {
			if(evento.getUserId() == null || evento.getUserId().isEmpty())
				errors.rejectValue(prefix + "userId", "error.empty");
			if(evento.getPassword() == null || evento.getPassword().isEmpty())
				errors.rejectValue(prefix + "password", "error.empty");
			if(evento.getUrl() == null || evento.getUrl().isEmpty())
				errors.rejectValue(prefix + "url", "error.empty");
		}

	}

	//validate PersonaEvento (tipoPersona serve a distinguere il caso responsabileScientifico da Docente)
	private boolean validatePersonaEvento(PersonaEvento persona, String tipoPersona) {

		//campi comuni
		if(persona.getAnagrafica().getCognome() == null || persona.getAnagrafica().getCognome().isEmpty())
			return true;
		if(persona.getAnagrafica().getNome() == null || persona.getAnagrafica().getNome().isEmpty())
			return true;
		// @since ERM014009
		//if(persona.getAnagrafica().getCodiceFiscale() == null || persona.getAnagrafica().getCodiceFiscale().isEmpty())
		if(Utils.rejectIfCodFiscIncorrect(persona.getAnagrafica().getCodiceFiscale(), persona.getAnagrafica().getStraniero()))
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
		// @since ERM014009
		//if(persona.getAnagrafica().getCodiceFiscale() == null || persona.getAnagrafica().getCodiceFiscale().isEmpty())
		if(Utils.rejectIfCodFiscIncorrect(persona.getAnagrafica().getCodiceFiscale()))
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
		// ERM014045 - no firm
		/*
		if(!fileValidator.validateFirmaCF(partner.getPartnerFile(), providerId))
			return true;
		*/

		return false;
	}

	//validate ProgrammaRES
	private void validateProgrammaRES(ValidateEventoResInfo validateEventoResInfo, ProgrammaGiornalieroRES programma, Errors errors, String prefix, TipologiaEventoRESEnum tipologiaEvento, Integer numeroPartecipanti, List<PersonaEvento> docentiEvento){

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
				boolean hasError = validateDettaglioAttivitaRES(validateEventoResInfo, dar, tipologiaEvento, numeroPartecipanti, docentiEvento);
				if(hasError) {
					errors.rejectValue(prefix + "programma["+counter+"]", "");
					atLeastOneErrorDettaglioAttivita = true;
				}
				if(!dar.isExtraType())
					atLeastOneAttivita = true;
				counter++;
			}
			//if(alertResDocentiPartecipanti) {
			if(validateEventoResInfo.isAlertResDocentiPartecipanti()) {
				errors.rejectValue(prefix + "programma", "error.ratio_metodologie_interattive_res");
				//alertResDocentiPartecipanti = false;
				validateEventoResInfo.setAlertResDocentiPartecipanti(false);
			}
			else if(validateEventoResInfo.isAlertResDocentiNonPresenti()) {
				errors.rejectValue(prefix + "programma", "error.docente_non_presente_res");
				validateEventoResInfo.setAlertResDocentiNonPresenti(false);
			}
			else if(atLeastOneErrorDettaglioAttivita)
				errors.rejectValue(prefix + "programma", "error.campi_mancanti_dettaglio_attivita");
			else if(!atLeastOneAttivita)
				errors.rejectValue(prefix + "programma", "error.solo_pause_programma_res");
		}
	}

	//validate DettaglioAttivita del ProgrammaRES
	private boolean validateDettaglioAttivitaRES(ValidateEventoResInfo validateEventoResInfo, DettaglioAttivitaRES dettaglio, TipologiaEventoRESEnum tipologiaEvento, Integer numeroPartecipanti, List<PersonaEvento> docentiEvento){

		//per prima cose se ho un risultato atteso lo aggiungo al set
		//risultatiAttesiUtilizzati.add(dettaglio.getRisultatoAtteso());
		validateEventoResInfo.getRisultatiAttesiUtilizzati().add(dettaglio.getRisultatoAtteso());

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
			if(dettaglio.getDocenti() == null || dettaglio.getDocenti().isEmpty())
				return true;
			Set<PersonaEvento> docentiTitolariDettaglio = new HashSet<PersonaEvento>();
			for(PersonaEvento docente : dettaglio.getDocenti()) {
				//il "Moderatore" NON deve essere considerato come un docente, anche se titolare, nel controllo del rapporto 1/25 tra docenti/discenti.
				if("titolare".equalsIgnoreCase(docente.getTitolare()) && docente.getRuolo() != RuoloPersonaEventoEnum.MODERATORE)
					docentiTitolariDettaglio.add(docente);
				//Controllo che il docente sia veramente presente fra i docenti
				if(!docentiEvento.contains(docente))
					validateEventoResInfo.setAlertResDocentiNonPresenti(true);
			}
			if(dettaglio.getObiettivoFormativo() == null)
				return true;
			if(dettaglio.getMetodologiaDidattica() == null)
				return true;
			//tiommi 2017-06-12 : modifica controllo res
			//controllare che sia rispettato il vincolo 1 / 25 dei docenti TITOLARI / partecipanti
			// N.B solo per gli eventi di tipo WORKSHOP/SEMINARIO o CORSO DI AGGIORNAMENTO
			if(dettaglio.getMetodologiaDidattica().getMetodologia() == TipoMetodologiaEnum.INTERATTIVA &&
					(tipologiaEvento == TipologiaEventoRESEnum.CORSO_AGGIORNAMENTO ||
					tipologiaEvento == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO) &&
					numeroPartecipanti != null &&
					(numeroPartecipanti.intValue() > (docentiTitolariDettaglio.size() * 25))) {
				//alertResDocentiPartecipanti = true;
				validateEventoResInfo.setAlertResDocentiPartecipanti(true);
			}

			if(validateEventoResInfo.isAlertResDocentiNonPresenti() || validateEventoResInfo.isAlertResDocentiPartecipanti())
				return true;

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
	private ValidateFasiAzioniRuoliFSCInfo validateFasiAzioniRuoliFSC(FaseAzioniRuoliEventoFSCTypeA faseAzioniRuoli, Errors errors, String prefix, TipologiaEventoFSCEnum tipologiaEvento,
			EventoVersioneEnum versione, List<RuoloFSCEnum> listRuoloFSCEnumPerResponsabiliScientifici,
			List<RuoloFSCEnum> listRuoloFSCEnumPerCoordinatori, List<RuoloFSCEnum> listRuoloFSCEnumPerEsperti) {

		ValidateFasiAzioniRuoliFSCInfo validateFasiAzioniRuoliFSCInfo = new ValidateFasiAzioniRuoliFSCInfo();

		//fase di lavoro (gratis, non viene inserita dall'utente, ma generata
		//automaticamente dal sistema

		//azioniRuoli (almeno 1 azione per fase)
		if(faseAzioniRuoli.getAzioniRuoli() == null || faseAzioniRuoli.getAzioniRuoli().isEmpty()) {
			errors.rejectValue(prefix + "azioniRuoli", "error.empty");
			//return new boolean[] {false, false};
			return validateFasiAzioniRuoliFSCInfo;
		}
		else {
			int counter = 0;
			boolean atLeastOneErrorAzione = false;
			boolean errorePartecipanteAudit = false;
			boolean atLeastOnePartecipante = false;
			boolean atLeastOneTutor = false;
			boolean atLeastOneRuoloRipetuto = false;

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

				//versione 2 controllo che i ruoli delle azioni siano validi in quanto potrebbero essere stati inseriti correttamente
				//ma poi potrebbero essere stati modificati i responsabili scientifici o la data inizio passando da un evento della versione 2 alla versione 1
				//rendendo alcuni o tutti i ruoli "Responsabile scientifico X" (X = A o B o C) non piu' accettabili
				for(RuoloOreFSC ruoloOre : aref.getRuoli()) {
					eventoValidator.validateRuoloDinamicoDaSezione1(validateFasiAzioniRuoliFSCInfo, ruoloOre, tipologiaEvento, versione, listRuoloFSCEnumPerResponsabiliScientifici, listRuoloFSCEnumPerCoordinatori, listRuoloFSCEnumPerEsperti);
				}

				//hasErrors
				if(validationResults[0] || validationResults[3] || validateFasiAzioniRuoliFSCInfo.isInvalidResponsabileScientifico()
						|| validateFasiAzioniRuoliFSCInfo.isInvalidCoordinatore()
						|| validateFasiAzioniRuoliFSCInfo.isInvalidEsperto()) {
					//Evidenzio la riga della AzioneRuoliEventoFSC
					errors.rejectValue(prefix + "azioniRuoli["+counter+"]", "");
					if(validationResults[0])
						atLeastOneErrorAzione = true;
					if(validationResults[3])
						atLeastOneRuoloRipetuto = true;
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
						errorePartecipanteAudit = true;
					else if((pairs.getValue() % 2) != 0f)
						errorePartecipanteAudit = true;
				 }
			}

			if(atLeastOneErrorAzione || atLeastOneRuoloRipetuto)
				errors.rejectValue(prefix + "azioniRuoli", "error.campi_con_errori_azione_ruoli"+tipologiaEvento);
			else if(errorePartecipanteAudit) {
				errors.rejectValue(prefix + "azioniRuoli", "error.partecipanti_AUDIT_ore");
			}

			//mostro i messaggi di non validita' dei ruoli dinamici non piu' presenti in sezione 1
			if(validateFasiAzioniRuoliFSCInfo.isInvalidResponsabileScientifico())
				errors.rejectValue(prefix + "azioniRuoli", "error.ruolo_responsabile_scientifico_x_non_valido");
			if(validateFasiAzioniRuoliFSCInfo.isInvalidCoordinatore())
				errors.rejectValue(prefix + "azioniRuoli", "error.ruolo_coordinatore_x_non_valido");
			if(validateFasiAzioniRuoliFSCInfo.isInvalidEsperto())
				errors.rejectValue(prefix + "azioniRuoli", "error.ruolo_esperto_x_non_valido");

			//return new boolean[] {atLeastOnePartecipante, atLeastOneTutor};
			validateFasiAzioniRuoliFSCInfo.setAtLeastOnePartecipante(atLeastOnePartecipante);
			validateFasiAzioniRuoliFSCInfo.setAtLeastOneTutor(atLeastOneTutor);
			return validateFasiAzioniRuoliFSCInfo;
		}
	}

	//validate azioniRuoli delle FasiAzioniRuoliFSC
	//ritorna un array di boolean -> boolean[] {hasError, hasPartecipante, hasTutor, ruoloRipetuto}
	private boolean[] validateAzioneRuoliFSC(AzioneRuoliEventoFSC azioneRuoli, TipologiaEventoFSCEnum tipologiaEvento) {

		//parte in comune
		if(azioneRuoli.getAzione() == null || azioneRuoli.getAzione().isEmpty())
			return new boolean[] {true, false, false, false};
		if(azioneRuoli.getObiettivoFormativo() == null)
			return new boolean[] {true, false, false, false};
		if(azioneRuoli.getRisultatiAttesi() == null || azioneRuoli.getRisultatiAttesi().isEmpty())
			return new boolean[] {true, false, false, false};
		if(azioneRuoli.getMetodiDiLavoro() == null || azioneRuoli.getMetodiDiLavoro().isEmpty())
			return new boolean[] {true, false, false, false};
		if(azioneRuoli.getRuoli() == null || azioneRuoli.getRuoli().isEmpty())
			return new boolean[] {true, false, false, false};

		//parti specifiche
		int numCoordinatoriA = 0;
		int numCoordinatoriB = 0;
		int numCoordinatoriC = 0;
		int numResponsabili = 0;
		boolean hasPartecipante = false;
		boolean hasTutor = false;
		boolean ruoloRipetuto = false;
		if(tipologiaEvento != null) switch(tipologiaEvento) {

			//caso 1) tipologiaEvento == TRAINING_INDIVIDUALIZZATO
			// - almeno 1 ruolo partecipante, tutor per azione
			// - almeno 1 ora non frazionabile per il partecipante
			// - massimo 1 coordinatore per azione
			// - tutti campi obbligatori
			case TRAINING_INDIVIDUALIZZATO:

				for(RuoloOreFSC r : azioneRuoli.getRuoli()) {
					if(r.getTempoDedicato() == null)
						return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};
					if(r.getRuolo() == null)
						return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};
					else {
						if(r.getRuolo() == RuoloFSCEnum.COORDINATORE_A)
							numCoordinatoriA++;
						else if(r.getRuolo() == RuoloFSCEnum.COORDINATORE_B)
							numCoordinatoriB++;
						else if(r.getRuolo() == RuoloFSCEnum.COORDINATORE_C)
							numCoordinatoriC++;
						else if(r.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE) {
							if(r.getTempoDedicato() < 1)
								return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};
							else
								hasPartecipante = true;
						}
						else if(r.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.TUTOR)
							hasTutor = true;
					}
				}
				if(numCoordinatoriA > 1 || numCoordinatoriB > 1 || numCoordinatoriC > 1)
					return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};

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
						return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};
					if(r.getRuolo() == null)
						return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};
					else {
						if(r.getRuolo() == RuoloFSCEnum.COORDINATORE_A)
							numCoordinatoriA++;
						else if(r.getRuolo() == RuoloFSCEnum.COORDINATORE_B)
							numCoordinatoriB++;
						else if(r.getRuolo() == RuoloFSCEnum.COORDINATORE_C)
							numCoordinatoriC++;
						else if(r.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE) {
							if(r.getTempoDedicato() < 2)
								return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};
							else if((r.getTempoDedicato() % 2) != 0f)
								return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};
							hasPartecipante = true;
						}
					}
				}
				//caso particolare (qua le fasi sono come le azioni per le altre tipologie,
				//quindi controllo che nella riga ci sia almeno 1 ruolo Partecipante)
				if(numCoordinatoriA > 1 || numCoordinatoriB > 0 || numCoordinatoriC > 0)
					return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};

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
						return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};
					if(r.getRuolo() == null)
						return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};
					else {
						if(r.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE) {
							hasPartecipante = true;
						}
						else if(r.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.RESPONSABILE)
							numResponsabili++;
					}
				}
				if(numResponsabili > 1)
					return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};

			break;

			//caso4) tipologiaEvento == ATTIVITA_DI_RICERCA
			// - almeno 1 azione per fase
			// - almeno 1 ruolo partecipante per azione
			// - tutti i campi obbligatori
			case ATTIVITA_DI_RICERCA:

				for(RuoloOreFSC r : azioneRuoli.getRuoli()) {
					if(r.getTempoDedicato() == null)
						return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};
					if(r.getRuolo() == null)
						return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};
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
						return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};
					if(r.getRuolo() == null)
						return new boolean[] {true, hasPartecipante, hasTutor, ruoloRipetuto};
					else {
						if(r.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE) {
//							if(r.getTempoDedicato() < 2)
//								return new boolean[] {true, hasPartecipante, hasTutor};
//							else if((r.getTempoDedicato() % 2) != 0f)
//								return new boolean[] {true, hasPartecipante, hasTutor};
							hasPartecipante = true;
						}
					}
				}

			break;

		}

		//controllo che non vi siano Ruoli ripetuti nella stessa azione
		//TODO per ottimizzare si potrebbe mettere tutto lo switch in un unico ciclo for dei ruoli (non si ciclerebbe 2 volte)
		List<RuoloFSCEnum> ruoliList = new ArrayList<RuoloFSCEnum>();
		Set<RuoloFSCEnum> ruoliSet = new HashSet<RuoloFSCEnum>();
		for(RuoloOreFSC r : azioneRuoli.getRuoli()) {
			ruoliList.add(r.getRuolo());
		}
		ruoliSet.addAll(ruoliList);
		if(ruoliList.size() > ruoliSet.size())
			ruoloRipetuto = true;

		return new boolean[] {false, hasPartecipante, hasTutor, ruoloRipetuto};
	}

	//validate tabella ruoli FSC
	private boolean validateTabellaRuoliFSC(RiepilogoRuoliFSC riepilogoRuoli, TipologiaEventoFSCEnum tipologiaEvento) {

		//campi in comuni obbligatori (partecipanti > 0)
		if(riepilogoRuoli.getNumeroPartecipanti() <= 0)
			return true;

		//tipologiaEvento == TRAINING_INDIVIDUALIZZATO || ATTIVITA_DI_RICERCA nessun controllo

		if(tipologiaEvento != null) {
			switch(tipologiaEvento) {

				//tipologiaEvento == GRUPPI_DI_MIGLIORAMENTO
				// - massimo 25 partecipanti per ruolo
				// - impegno complessivo minimo 8 ore totali per ruolo PARTECIPANTE
				// - massimo un coordinatore
				case GRUPPI_DI_MIGLIORAMENTO:

					if(riepilogoRuoli.getNumeroPartecipanti() > 25)
						return true;
					if(riepilogoRuoli.getRuolo() != null
							&& riepilogoRuoli.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE
							&& riepilogoRuoli.getTempoDedicato() < 8f)
						return true;
					if(riepilogoRuoli.getRuolo() != null
							&& riepilogoRuoli.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.COORDINATORE
							&& riepilogoRuoli.getNumeroPartecipanti() > 1)
						return true;

				break;

				//tipologiaEvento == PROGETTI DI MIGLIORAMENTO
				// - impegno complessivo minimo 8 ore totali per ruolo PARTECIPANTE
				// - massimo un responsabile
				case PROGETTI_DI_MIGLIORAMENTO:

					if(riepilogoRuoli.getRuolo() != null
							&& riepilogoRuoli.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE
							&& riepilogoRuoli.getTempoDedicato() < 8f)
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
		}
		//Specifici Versione 2
		//per "Responsabile scientifico A B C", "Coordinatore A B C", per "Esperto A B C" deve essere inserito solo 1 partecipante
		if(riepilogoRuoli.getRuolo() != null
				&& riepilogoRuoli.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.RESPONSABILE_SCIENTIFICO
				&& riepilogoRuoli.getNumeroPartecipanti() > 1)
			return true;
		if(riepilogoRuoli.getRuolo() != null
				&& riepilogoRuoli.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.COORDINATORE_X
				&& riepilogoRuoli.getNumeroPartecipanti() > 1)
			return true;
		if(riepilogoRuoli.getRuolo() != null
				&& riepilogoRuoli.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.ESPERTO
				&& riepilogoRuoli.getNumeroPartecipanti() > 1)
			return true;


		return false;
	}

	//validate ProgrammaFAD
	private boolean validateProgrammaFAD(ValidateEventoFadInfo validateEventoFadInfo, DettaglioAttivitaFAD attivita) {

		//per prima cose se ho un risultato atteso lo aggiungo al set
		//risultatiAttesiUtilizzati.add(attivita.getRisultatoAtteso());
		validateEventoFadInfo.getRisultatiAttesiUtilizzati().add(attivita.getRisultatoAtteso());

		//tutti i campi obbligatori
		if(attivita.getArgomento() == null || attivita.getArgomento().isEmpty()) {
			return true;
		}
		if(attivita.getDocenti() == null || attivita.getDocenti().isEmpty()) {
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
