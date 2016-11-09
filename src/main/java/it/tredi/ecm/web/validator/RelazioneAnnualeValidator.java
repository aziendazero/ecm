package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.RelazioneAnnuale;
import it.tredi.ecm.utils.Utils;

@Component
public class RelazioneAnnualeValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(RelazioneAnnualeValidator.class);
	
	public void validate(Object target, Errors errors, String prefix) {
		LOGGER.info(Utils.getLogMessage("Validazione Relazione Annuale"));
		RelazioneAnnuale relazioneAnnuale = (RelazioneAnnuale)target;
		validateRelazioneAnnuale(relazioneAnnuale, errors, prefix);
		Utils.logDebugErrorFields(LOGGER, errors);
	}
	
	private void validateRelazioneAnnuale(RelazioneAnnuale relazioneAnnuale, Errors errors, String prefix){
		//Presenza e univocità del name
		if(relazioneAnnuale.getNumeroPartecipantiNoCrediti() == null){
			errors.rejectValue(prefix + "numeroPartecipantiNoCrediti", "error.empty");
		}
		
		if(relazioneAnnuale.getCostiTotaliEventi() == null){
			errors.rejectValue(prefix + "costiTotaliEventi", "error.empty");
		}
		
		if(relazioneAnnuale.getRicaviDaSponsor() == null){
			errors.rejectValue(prefix + "ricaviDaSponsor", "error.empty");
		}
		
		if(relazioneAnnuale.getAltriFinanziamenti() == null){
			errors.rejectValue(prefix + "altriFinaziamenti", "error.empty");
		}
		
		if(relazioneAnnuale.getQuoteDiPartecipazione() == null){
			errors.rejectValue(prefix + "quotePartecipazione", "error.empty");
		}
		
		if(relazioneAnnuale.getRelazioneFinale() == null || relazioneAnnuale.getRelazioneFinale().getData().length == 0){
			errors.rejectValue(prefix + "relazioneFinale", "error.empty");
		}
	}
}
