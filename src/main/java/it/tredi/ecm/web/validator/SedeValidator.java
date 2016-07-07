package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.utils.Utils;

@Component
public class SedeValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(SedeValidator.class);
	
	public void validate(Object target, Errors errors, String prefix){
		Utils.logInfo(LOGGER, "Validazione Sede");
		Sede sede = (Sede)target;
		
		if(sede.getProvincia() == null || sede.getProvincia().isEmpty())
			errors.rejectValue(prefix + "provincia", "error.empty");
		if(sede.getComune() == null || sede.getComune().isEmpty())
			errors.rejectValue(prefix + "comune", "error.empty");
		if(sede.getIndirizzo() == null || sede.getIndirizzo().isEmpty())
			errors.rejectValue(prefix + "indirizzo", "error.empty");
		if(sede.getCap() == null || sede.getCap().isEmpty())
			errors.rejectValue(prefix + "cap", "error.empty");
		if(sede.getTelefono() == null || sede.getTelefono().isEmpty())
			errors.rejectValue(prefix + "telefono", "error.empty");
		if(sede.getFax() == null || sede.getFax().isEmpty())
			errors.rejectValue(prefix + "fax", "error.empty");
		if(sede.getEmail() == null || sede.getEmail().isEmpty())
			errors.rejectValue(prefix + "email", "error.empty");
		
		Utils.logDebugErrorFields(LOGGER, errors);
	}
}
