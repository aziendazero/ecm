package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.ScadenzeEventoWrapper;

@Component
public class ScadenzeEventoValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScadenzeEventoValidator.class);

	public void validate(Object target, Errors errors, String prefix) throws Exception{
		ScadenzeEventoWrapper scadenze = (ScadenzeEventoWrapper) target;

		//check inserimento
		if(scadenze.getDataScadenzaPagamento() == null)
			errors.rejectValue(prefix + "dataScadenzaPagamento", "error.empty");
		if(scadenze.getDataScadenzaRendicontazione() == null)
			errors.rejectValue(prefix + "dataScadenzaRendicontazione", "error.empty");

		Utils.logDebugErrorFields(LOGGER, errors);
	}

}