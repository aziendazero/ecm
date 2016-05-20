package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Sede;

@Component
public class SedeValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(SedeValidator.class);
	
	public void validate(Object traget, Errors error, String prefix){
		LOGGER.debug("Validazione della sede");
		Sede sede = (Sede)traget;
		
		if(sede.getProvincia() == null || sede.getProvincia().isEmpty())
			error.rejectValue(prefix + "provincia", "error.empty");
		if(sede.getComune() == null || sede.getComune().isEmpty())
			error.rejectValue(prefix + "comune", "error.empty");
		if(sede.getIndirizzo() == null || sede.getIndirizzo().isEmpty())
			error.rejectValue(prefix + "indirizzo", "error.empty");
		if(sede.getCap() == null || sede.getCap().isEmpty())
			error.rejectValue(prefix + "cap", "error.empty");
		if(sede.getTelefono() == null || sede.getTelefono().isEmpty())
			error.rejectValue(prefix + "telefono", "error.empty");
		if(sede.getAltroTelefono() == null || sede.getAltroTelefono().isEmpty())
			error.rejectValue(prefix + "altroTelefono", "error.empty");
		if(sede.getFax() == null || sede.getFax().isEmpty())
			error.rejectValue(prefix + "fax", "error.empty");
		if(sede.getEmail() == null || sede.getEmail().isEmpty())
			error.rejectValue(prefix + "email", "error.empty");
	}
}
