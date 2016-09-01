package it.tredi.ecm.web.validator;

import java.time.LocalDate;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.utils.Utils;

@Component
public class SedutaValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(SedutaValidator.class);

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
			errors.rejectValue(prefix + "ora", "error.ora_non_valida");
		Utils.logDebugErrorFields(LOGGER, errors);
	}
}
