package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.RichiestaIntegrazioneWrapper;

@Component
public class EnableFieldValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(EnableFieldValidator.class);

	public String validate(Object target){
		LOGGER.info(Utils.getLogMessage("Validazione EnableField"));
		RichiestaIntegrazioneWrapper richiestaIntegrazioneWrapper = (RichiestaIntegrazioneWrapper)target;

		String errorMsg = null;

		if(richiestaIntegrazioneWrapper.getSelected() != null){
			for(IdFieldEnum field : richiestaIntegrazioneWrapper.getSelected()){
				if(richiestaIntegrazioneWrapper.getMappaNoteFieldEditabileAccreditamento().get(field) == null || richiestaIntegrazioneWrapper.getMappaNoteFieldEditabileAccreditamento().get(field).isEmpty()){
					errorMsg = "message.motivazione_enablefield_required";
					LOGGER.info("Validazione enableField errore su " + field);
				}
			}
		}

		return errorMsg;
	}
}
