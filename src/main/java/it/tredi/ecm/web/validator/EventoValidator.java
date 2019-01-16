package it.tredi.ecm.web.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.RuoloOreFSC;
import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCBaseEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoWrapper;
import it.tredi.ecm.web.validator.bean.ValidateFasiAzioniRuoliFSCInfo;

@Component
public class EventoValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventoValidator.class);

	@Autowired private FileValidator fileValidator;
	@Autowired private EventoService eventoService;
	@Autowired private EventoValidatorVersioneUno eventoValidatorVersioneUno;
	@Autowired private EventoValidatorVersioneDue eventoValidatorVersioneDue;

	// EVENTO_VERSIONE
	public void validate(Object target, EventoWrapper wrapper, Errors errors, String prefix) throws Exception{
		Evento evento = (Evento) target;

		EventoVersioneEnum eventoVersione = eventoService.versioneEvento(evento);
		switch (eventoVersione) {
		case UNO_PRIMA_2018:
			eventoValidatorVersioneUno.validate(target, wrapper, errors, prefix);
			break;
		case DUE_DAL_2018:
			eventoValidatorVersioneDue.validate(target, wrapper, errors, prefix);
			break;
		default:
			throw new Exception("Evento versione: " + eventoVersione + " non gestita");
		}

//		validateCommon(evento, errors, prefix);
//
//		if (evento instanceof EventoRES)
//			validateRES(((EventoRES) evento), wrapper, errors, prefix);
//		else if (evento instanceof EventoFSC)
//			validateFSC(((EventoFSC) evento), wrapper, errors, prefix);
//		else if (evento instanceof EventoFAD)
//			validateFAD(((EventoFAD) evento), wrapper, errors, prefix);

		Utils.logDebugErrorFields(LOGGER, errors);

	}

	public Map<String, String> validateContrattoSponsor(File sponsorFile, Long providerId, String prefix) throws Exception {
		Map<String, String> errMap = new HashMap<String, String>();

		if(sponsorFile == null || sponsorFile.isNew())
			errMap.put("file_"+prefix+"_button", "error.empty");
//		else if(!fileValidator.validateFirmaCF(sponsorFile, providerId))
//			errMap.put("file_"+prefix+"_button", "error.codiceFiscale.firmatario");

		return errMap;
	}

	// EVENTO_VERSIONE
	//versione 2 controllo che i ruoli delle azioni siano validi in quanto potrebbero essere stati inseriti correttamente
	//ma poi potrebbero essere stati modificati i responsabili scientifici o la data inizio passando da un evento della versione 2 alla versione 1
	//o viceversa rendendo alcuni o tutti i ruoli "Responsabile scientifico X" (X = A o B o C) non piu' accettabili
	public void validateRuoloDinamicoDaSezione1(ValidateFasiAzioniRuoliFSCInfo validateFasiAzioniRuoliFSCInfo, RuoloOreFSC ruoloOre
			, TipologiaEventoFSCEnum tipologiaEvento, EventoVersioneEnum versione
			, List<RuoloFSCEnum> listRuoloFSCEnumPerResponsabiliScientifici, List<RuoloFSCEnum> listRuoloFSCEnumPerCoordinatori, List<RuoloFSCEnum> listRuoloFSCEnumPerEsperti) {
		if(ruoloOre.getRuolo() != null && ruoloOre.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.RESPONSABILE_SCIENTIFICO) {
			//potrebbe non essere valido
			if(versione == EventoVersioneEnum.UNO_PRIMA_2018) {
				// vengono settati tutti a null perche' nella versione 1 non esistevano
				validateFasiAzioniRuoliFSCInfo.setInvalidResponsabileScientifico(true);
			} else {
				if(!listRuoloFSCEnumPerResponsabiliScientifici.contains(ruoloOre.getRuolo()))
					validateFasiAzioniRuoliFSCInfo.setInvalidResponsabileScientifico(true);
			}
		} else if(ruoloOre.getRuolo() != null && ruoloOre.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.COORDINATORE_X) {
			//potrebbe non essere valido
			if(versione == EventoVersioneEnum.UNO_PRIMA_2018) {
				// vengono settati tutti a null perche' nella versione 1 non esistevano
				validateFasiAzioniRuoliFSCInfo.setInvalidCoordinatore(true);
			} else {
				if(!listRuoloFSCEnumPerCoordinatori.contains(ruoloOre.getRuolo()))
					validateFasiAzioniRuoliFSCInfo.setInvalidCoordinatore(true);
			}
		} else if(ruoloOre.getRuolo() != null && ruoloOre.getRuolo().getRuoloBase() == RuoloFSCBaseEnum.ESPERTO) {
			//potrebbe non essere valido
			if(versione == EventoVersioneEnum.UNO_PRIMA_2018) {
				//vengono accettati solo quelli validi per la tipologia corrente
				if(tipologiaEvento != null && !tipologiaEvento.getRuoliCoinvolti().contains(ruoloOre.getRuolo())) {
					validateFasiAzioniRuoliFSCInfo.setInvalidEsperto(true);
				}
			} else {
				if(!listRuoloFSCEnumPerEsperti.contains(ruoloOre.getRuolo()))
					validateFasiAzioniRuoliFSCInfo.setInvalidEsperto(true);
			}
		}
	}

}
