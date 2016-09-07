package it.tredi.ecm.web.validator;

import java.time.LocalDate;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.dao.entity.ValutazioneCommissione;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.SedutaWrapper;

@Component
public class SedutaValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(SedutaValidator.class);

	@Autowired private EcmProperties ecmProperties;

	public void validate(Object target, Errors errors, String prefix){
		LOGGER.info(Utils.getLogMessage("Validazione Seduta"));
		Seduta seduta = (Seduta)target;

		if(seduta.getData() == null)
			errors.rejectValue(prefix + "data", "error.empty");
		else if(seduta.getData().isBefore(LocalDate.now()))
			errors.rejectValue(prefix + "data", "error.data_non_valida");
		if(seduta.getOra() == null)
			errors.rejectValue(prefix + "ora", "error.empty");
		else if(seduta.getData().isEqual(LocalDate.now()) && seduta.getOra().isBefore(LocalTime.now()))
			errors.rejectValue(prefix + "ora", "error.ora_non_valida1");
		else if(seduta.getData().isEqual(LocalDate.now()) && seduta.getOra().isBefore(LocalTime.now().plusMinutes(ecmProperties.getSedutaValidationMinutes())))
			errors.rejectValue(prefix + "ora", "error.ora_non_valida2");
		Utils.logDebugErrorFields(LOGGER, errors);
	}

	public void validateValutazioneCommissione(Object target, Errors errors, String prefix) {
		LOGGER.info(Utils.getLogMessage("Validazione Creazione Valutazione Commissione"));
		SedutaWrapper wrapper = (SedutaWrapper) target;
		if(wrapper.getIdAccreditamentoDaInserire() == null)
			errors.rejectValue(prefix + "idAccreditamentoDaInserire", "error.empty");
		if(wrapper.getMotivazioneDaInserire() == null || wrapper.getMotivazioneDaInserire().isEmpty()) {
			errors.rejectValue(prefix + "motivazioneDaInserire", "error.empty");
		}
		Utils.logDebugErrorFields(LOGGER, errors);
	}

	public void validateSpostamentoValutazioneCommissione(Object target, Errors errors, String prefix) {
		LOGGER.info(Utils.getLogMessage("Validazione Spostamento Valutazione Commissione"));
		SedutaWrapper wrapper = (SedutaWrapper) target;
		if(wrapper.getSedutaTarget() == null)
			errors.rejectValue(prefix + "sedutaTarget", "error.empty");
		Utils.logDebugErrorFields(LOGGER, errors);
	}

	public void validateCompletamentoValutazioneCommissione(Object target, Errors errors, String prefix) {
		LOGGER.info(Utils.getLogMessage("Validazione Completamento Valutazione Commissione"));
		ValutazioneCommissione val = (ValutazioneCommissione) target;
		if(val.getValutazioneCommissione() == null || val.getValutazioneCommissione().isEmpty())
			errors.rejectValue(prefix + "valutazioneCommissione", "error.empty");
		if(val.getStato() == null)
			errors.rejectValue(prefix + "stato", "error.empty");
		Utils.logDebugErrorFields(LOGGER, errors);
	}
}
