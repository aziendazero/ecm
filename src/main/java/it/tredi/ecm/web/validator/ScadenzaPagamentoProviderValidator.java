package it.tredi.ecm.web.validator;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.ScadenzaPagamentoProviderWrapper;

@Component
public class ScadenzaPagamentoProviderValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScadenzaPagamentoProviderValidator.class);

	public void validate(Object target, Errors errors, String prefix) throws Exception{
		ScadenzaPagamentoProviderWrapper scadenze = (ScadenzaPagamentoProviderWrapper) target;

		//check inserimento
		if(scadenze.getDataScadenzaPagamento() == null)
			errors.rejectValue(prefix + "dataScadenzaPagamento", "error.empty");
		else if(scadenze.getDataScadenzaPagamento().isBefore(LocalDate.now()))
			errors.rejectValue(prefix + "dataScadenzaPagamento", "error.data_non_valida_verbale");
		
		Utils.logDebugErrorFields(LOGGER, errors);
	}
	
}
