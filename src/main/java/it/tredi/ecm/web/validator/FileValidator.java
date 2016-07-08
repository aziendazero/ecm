package it.tredi.ecm.web.validator;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.utils.Utils;

@Component
public class FileValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileValidator.class);
	
	@Autowired private EcmProperties ecmProperties;
	@Autowired private MessageSource messageSource;
	
	public void validate(Object target, Errors errors, String prefix) {
		LOGGER.info(Utils.getLogMessage("Validazione File"));
		File file = (File)target;
		if(file == null || file.getNomeFile().isEmpty() || file.getData().length == 0){
			errors.rejectValue(prefix, "error.empty");
		}else{
			if(file.getData().length > ecmProperties.getMultipartMaxFileSize()){
				errors.rejectValue(prefix, "error.maxFileSize", new Object[]{String.valueOf(ecmProperties.getMultipartMaxFileSize()/(1024*1024) )},"");
			}
		}
	}
	
	public String validate(Object target) {
		LOGGER.info(Utils.getLogMessage("Validazione File AJAX Upload"));
		File file = (File)target;
		String error = "";
		if(file == null || file.getNomeFile().isEmpty() || file.getData().length == 0){
			error = messageSource.getMessage("error.empty", null, Locale.getDefault());
		}else{
			if(file.getData().length > ecmProperties.getMultipartMaxFileSize()){
				error = messageSource.getMessage("error.maxFileSize", new Object[]{String.valueOf(ecmProperties.getMultipartMaxFileSize()/(1024*1024) )}, Locale.getDefault());
			}
		}
		return error;
	}
}
