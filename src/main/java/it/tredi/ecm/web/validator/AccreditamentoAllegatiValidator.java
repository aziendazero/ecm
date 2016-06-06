package it.tredi.ecm.web.validator;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.File;

@Component
public class AccreditamentoAllegatiValidator {
private static final Logger LOGGER = LoggerFactory.getLogger(AccreditamentoAllegatiValidator.class);
	
	@Autowired
	private FileValidator fileValidator;
	
	public void validate(Object target, Errors errors, String prefix, Set<File> files){
		LOGGER.debug("Validazione Allegati Accreditamento");
		validateFiles(files, errors, "");
	}
	
	public void validateFiles(Object target, Errors errors, String prefix){
		LOGGER.debug("VALIDAZIONE ALLEGATI ACCREDITAMENTO");
		Set<File> files = null;
		if(target != null)
			files = (Set<File>) target;
		else
			files = new HashSet<File>();
		
		File attoCostitutivo = null;
		File esperienzaFormazione = null;
		File utilizzo = null;
		File sistemaInformatico = null;
		File pianoQualita = null;
		File dichiarazioneLegale = null;
		
		for(File file : files){
			if(file != null){
				if(file.isATTOCOSTITUTIVO())
					attoCostitutivo = file;
				else if(file.isESPERIENZAFORMAZIONE())
					esperienzaFormazione = file;
				else if(file.isDICHIARAZIONELEGALE())
					dichiarazioneLegale = file;
				else if(file.isPIANOQUALITA())
					pianoQualita = file;
				else if(file.isUTILIZZO())
					utilizzo = file;
				else if(file.isSISTEMAINFORMATICO())
					sistemaInformatico = file;
			}
		}
		
		fileValidator.validate(attoCostitutivo, errors, prefix + "attoCostitutivo");
		fileValidator.validate(esperienzaFormazione, errors, prefix + "esperienzaFormazione");
		fileValidator.validate(utilizzo, errors, prefix + "utilizzo");
		fileValidator.validate(sistemaInformatico, errors, prefix + "sistemaInformatico");
		fileValidator.validate(pianoQualita, errors, prefix + "pianoQualita");
		fileValidator.validate(dichiarazioneLegale, errors, prefix + "dichiarazioneLegale");
	}
}
