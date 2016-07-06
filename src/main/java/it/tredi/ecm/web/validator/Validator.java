package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.springframework.validation.Errors;

import it.tredi.ecm.utils.Utils;

public class Validator {
	public void logDebugErrorFields(Logger LOGGER, Errors errors){
		if(LOGGER.isDebugEnabled())
			errors.getFieldErrors().forEach( fieldError -> Utils.logDebug(LOGGER, fieldError.getField() + ": '" + fieldError.getRejectedValue() + "' [" + fieldError.getCode()+ "]"));
	}
}
