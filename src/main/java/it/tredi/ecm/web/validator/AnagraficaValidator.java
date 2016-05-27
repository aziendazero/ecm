package it.tredi.ecm.web.validator;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Anagrafica;

@Component
public class AnagraficaValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(AnagraficaValidator.class);
	private static final String PATTERN_CODICE_FISCALE = "[a-zA-Z]{6}[0-9]{2}[a-zA-Z][0-9]{2}[a-zA-Z][0-9]{3}[a-zA-Z]";
	
	public void validateBase(Object target, Errors errors, String prefix){
		Anagrafica anagrafica = (Anagrafica)target;
		if(anagrafica.getCognome().isEmpty())
			errors.rejectValue(prefix + "cognome", "error.empty");
		if(anagrafica.getNome().isEmpty())
			errors.rejectValue(prefix + "nome", "error.empty");
		if(anagrafica.getTelefono().isEmpty())
			errors.rejectValue(prefix + "telefono", "error.empty");
		if(anagrafica.getEmail().isEmpty())
			errors.rejectValue(prefix + "email", "error.empty");
		
		if(anagrafica.getCodiceFiscale().isEmpty()){
			errors.rejectValue(prefix + "codiceFiscale", "error.empty");
		}else{
			if(!anagrafica.isStraniero()){
				if(!Pattern.matches(PATTERN_CODICE_FISCALE, anagrafica.getCodiceFiscale()))
					errors.rejectValue(prefix + "codiceFiscale", "error.invalid");
			}
		}
		
	}
	
	public void validateCellulare(Object target, Errors errors, String prefix){
		Anagrafica anagrafica = (Anagrafica)target;
		if(anagrafica.getCellulare().isEmpty())
			errors.rejectValue(prefix + "cellulare", "error.empty");
	}
	
	public void validatePEC(Object target, Errors errors, String prefix){
		Anagrafica anagrafica = (Anagrafica)target;
		if(anagrafica.getPec().isEmpty())
			errors.rejectValue(prefix + "pec", "error.empty");
	}
}
