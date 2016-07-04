package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.File;

@Component
public class EngineeringTestValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(EngineeringTestValidator.class);

	@Autowired
	private FileValidator fileValidator;

	public void validate(Object target, Errors errors, String prefix, File file){
		LOGGER.debug("Validazione Test Engineering");
		validateFileDaFirmare(file, errors, "");
	}

	public void validateFileDaFirmare(Object target, Errors errors, String prefix){
		LOGGER.debug("VALIDAZIONE FILE DA FIRMARE");
		File file = null;
		if(target != null)
			file = (File) target;
		File fileDaFirmare = null;

		if(file != null && !file.isNew()){
			if(file.isFILEDAFIRMARE())
					fileDaFirmare = file;
		}

		fileValidator.validate(fileDaFirmare, errors, prefix + "fileDaFirmare");
	}

	//TODO validate mypay
}
