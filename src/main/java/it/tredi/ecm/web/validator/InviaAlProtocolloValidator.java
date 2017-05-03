package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AccreditamentoWrapper;

@Component
public class InviaAlProtocolloValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(InviaAlProtocolloValidator.class);

	@Autowired private FileValidator fileValidator;

	public void validate(Object target, Errors errors, String prefix) throws Exception{
		LOGGER.info(Utils.getLogMessage("Validazione Invio al Protocollo"));
		AccreditamentoWrapper wrapper = (AccreditamentoWrapper)target;

		fileValidator.validateIsSigned(wrapper.getFileDaFirmare(), errors, "");
		if(wrapper.isCanAccreditatoInAttesaDiFirma() || wrapper.isCanDiniegoInAttesaDiFirma()){
			if(wrapper.getDataDelibera() == null)
				errors.rejectValue(prefix + "dataDelibera", "error.empty");
			if(wrapper.getNumeroDelibera() == null || wrapper.getNumeroDelibera().isEmpty())
				errors.rejectValue(prefix + "numeroDelibera", "error.empty");
		}

		Utils.logDebugErrorFields(LOGGER, errors);
	}

}
