package it.tredi.ecm.web.validator;

import java.time.LocalDate;

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

		validateDelibera(wrapper.getDataDelibera(), wrapper.getNumeroDelibera(), errors, prefix);

		if(wrapper.getAccreditamento().isAccreditatoInAttesaDiFirma() || wrapper.getAccreditamento().isDiniegoInAttesaDiFirma()) {
			fileValidator.validateIsSigned(wrapper.getLetteraAccompagnatoria(), errors, "");
		}

		Utils.logDebugErrorFields(LOGGER, errors);
	}

	public void validateDelibera(LocalDate dataDelibera, String numeroDelibera, Errors errors, String prefix) {
		//errore solo se uno dei due valorizzato
		if((numeroDelibera == null || numeroDelibera.isEmpty()) && dataDelibera != null) {
			errors.rejectValue(prefix + "numeroDelibera", "error.entrambi_valori_per_delibera");
		}
		if(dataDelibera == null && (numeroDelibera != null && !numeroDelibera.isEmpty())) {
			errors.rejectValue(prefix + "dataDelibera", "error.entrambi_valori_per_delibera");
		}
	}

	public void validateAggiungiDatiDelibera(Object target, Errors errors, String prefix) throws Exception {
		LOGGER.info(Utils.getLogMessage("Validazione Aggiunta dati della Delibera"));
		AccreditamentoWrapper wrapper = (AccreditamentoWrapper)target;

		if(wrapper.getIdFileDelibera() == null) {
			throw new Exception("Errore durante l'invio dell'id del file protocollato a cui aggiungere i dati della delibera");
		}

		if(wrapper.getNumeroDelibera() == null || wrapper.getNumeroDelibera().isEmpty()){
			errors.rejectValue(prefix + "numeroDelibera", "error.empty");
		}
		if(wrapper.getDataDelibera() == null) {
			errors.rejectValue(prefix + "dataDelibera", "error.empty");
		}
	}

}
