package it.tredi.ecm.web.validator;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.File;

@Component
public class DatiAccreditamentoValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatiAccreditamentoValidator.class);
	
	@Autowired
	private FileValidator fileValidator;
	
	public void validate(Object target, Errors errors, String prefix, Set<File> files){
		LOGGER.debug("Validazione Dati Accreditamento");
		validateDatiAccreditamento(target, errors, prefix);
		//TODO file validators
		//validateFiles(files, errors, "", ((Persona)target).getRuolo());
	}
	
	private void validateDatiAccreditamento(Object target, Errors errors, String prefix){
		DatiAccreditamento datiAccreditamento = (DatiAccreditamento)target;
		if(datiAccreditamento.getTipologiaAccreditamento() == null || datiAccreditamento.getTipologiaAccreditamento().isEmpty())
			errors.rejectValue(prefix + "tipologiaAccreditamento", "error.empty");
		if(datiAccreditamento.getProcedureFormative() == null || datiAccreditamento.getProcedureFormative().isEmpty())
			errors.rejectValue(prefix + "procedureFormative", "error.empty");
		if(datiAccreditamento.getProfessioniAccreditamento() == null || datiAccreditamento.getProfessioniAccreditamento().isEmpty())
			errors.rejectValue(prefix + "professioniAccreditamento", "error.empty");
	}
}
