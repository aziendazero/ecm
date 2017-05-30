package it.tredi.ecm.web.validator;

import java.nio.file.Files;

import org.glassfish.jersey.server.internal.scanning.FilesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.RelazioneAnnuale;
import it.tredi.ecm.utils.Utils;

@Component
public class RelazioneAnnualeValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(RelazioneAnnualeValidator.class);

	@Autowired private FileValidator fileValidator;

	public void validate(Object target, Object allegato, Errors errors, String prefix, Long providerId) throws Exception {
		LOGGER.info(Utils.getLogMessage("Validazione Relazione Annuale"));
		RelazioneAnnuale relazioneAnnuale = (RelazioneAnnuale)target;
		File relazioneFinale = (File) allegato;
		validateRelazioneAnnuale(relazioneAnnuale, relazioneFinale, errors, prefix, providerId);
		Utils.logDebugErrorFields(LOGGER, errors);
	}

	private void validateRelazioneAnnuale(RelazioneAnnuale relazioneAnnuale, File relazioneFinale, Errors errors, String prefix, Long providerId) throws Exception{
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
			errors.rejectValue(prefix + "altriFinanziamenti", "error.empty");
		}

		if(relazioneAnnuale.getQuoteDiPartecipazione() == null){
			errors.rejectValue(prefix + "quoteDiPartecipazione", "error.empty");
		}

		fileValidator.validate(relazioneFinale, errors, prefix + "relazioneFinale", providerId);
	}
}
