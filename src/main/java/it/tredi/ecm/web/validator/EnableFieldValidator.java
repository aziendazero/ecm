package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.RichiestaIntegrazioneWrapper;

@Component
public class EnableFieldValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(EnableFieldValidator.class);

	@Autowired private AccreditamentoService accreditamentoService;

	public String validate(Object target){
		LOGGER.info(Utils.getLogMessage("Validazione EnableField"));
		RichiestaIntegrazioneWrapper richiestaIntegrazioneWrapper = (RichiestaIntegrazioneWrapper)target;

		String errorMsg = null;

		Accreditamento accreditamento = accreditamentoService.getAccreditamento(richiestaIntegrazioneWrapper.getAccreditamentoId());

		if(richiestaIntegrazioneWrapper.getSelected() != null && !accreditamento.isVariazioneDati()){
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
