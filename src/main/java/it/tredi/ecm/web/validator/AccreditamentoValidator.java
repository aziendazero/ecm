package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;

@Component
public class AccreditamentoValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccreditamentoValidator.class);

	public void validate(Object target, Errors errors) {
		LOGGER.debug("VALIDATING ACCREDITAMENTO");
		validateProvider((Provider)target, errors);
		validatePersona((Persona)target, errors);
		validateAccreditamento((Accreditamento)target, errors);
	}

	private void validateProvider(Provider provider, Errors errors){

	}

	private void validatePersona(Persona persona, Errors errors){

	}

	private void validateAccreditamento(Accreditamento accreditamento, Errors errors){

	}
}
