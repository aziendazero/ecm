package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.ImpostazioniProviderWrapper;


@Component
public class ImpostazioniProviderValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImpostazioniProviderValidator.class);

	public void validate(Object target, Errors errors, String prefix) throws Exception{
		ImpostazioniProviderWrapper impostazioni = (ImpostazioniProviderWrapper) target;

		//check inserimento
		if(impostazioni.getCanInsertPianoFormativo() == null)
			errors.rejectValue(prefix + "canInsertPianoFormativo", "error.empty");
		if(impostazioni.getCanInsertEventi() == null)
			errors.rejectValue(prefix + "canInsertEventi", "error.empty");
		if(impostazioni.getCanInsertDomandaStandard() == null)
			errors.rejectValue(prefix + "canInsertDomandaStandard", "error.empty");
		if(impostazioni.getCanInsertDomandaProvvisoria() == null)
			errors.rejectValue(prefix + "canInsertDomandaProvvisoria", "error.empty");
		if(impostazioni.getCanInsertRelazioneAnnuale() == null)
			errors.rejectValue(prefix + "canInsertRelazioneAnnuale", "error.empty");

		//date
		if(impostazioni.getCanInsertPianoFormativo() != null
				&& impostazioni.getCanInsertPianoFormativo() == true
				&& impostazioni.getDataScadenzaInsertPianoFormativo() == null)
			errors.rejectValue(prefix + "dataScadenzaInsertPianoFormativo", "error.empty");
		if(impostazioni.getCanInsertDomandaStandard() != null
				&& impostazioni.getCanInsertDomandaStandard() == true
				&& impostazioni.getDataScadenzaInsertDomandaStandard() == null)
			errors.rejectValue(prefix + "dataScadenzaInsertDomandaStandard", "error.empty");
		if(impostazioni.getCanInsertDomandaProvvisoria() != null
				&& impostazioni.getCanInsertDomandaProvvisoria() == true
				&& impostazioni.getDataRinnovoInsertDomandaProvvisoria() == null)
			errors.rejectValue(prefix + "dataRinnovoInsertDomandaProvvisoria", "error.empty");
		if(impostazioni.getCanInsertRelazioneAnnuale() != null
				&& impostazioni.getCanInsertRelazioneAnnuale() == true
				&& impostazioni.getDataScadenzaInsertRelazioneAnnuale() == null)
			errors.rejectValue(prefix + "dataScadenzaInsertRelazioneAnnuale", "error.empty");

		//select stato
		if(impostazioni.getStato() == null)
			errors.rejectValue(prefix + "stato", "error.empty");

		Utils.logDebugErrorFields(LOGGER, errors);
	}

}
