package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;

@Component
public class SedeValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(SedeValidator.class);

	@Autowired private ProviderService providerService;

	public void validate(Object target, Object providerTarget, Errors errors, String prefix){
		LOGGER.info(Utils.getLogMessage("Validazione Sede"));
		Sede sede = (Sede)target;
		Provider provider = (Provider)providerTarget;

		if(sede.isSedeLegale() && providerService.hasAlreadySedeLegaleProvider(provider, sede))
			errors.rejectValue(prefix + "sedeLegale", "error.sede_legale_gia_inserita");
		if(sede.getProvincia() == null || sede.getProvincia().isEmpty())
			errors.rejectValue(prefix + "provincia", "error.empty");
		if(sede.getComune() == null || sede.getComune().isEmpty())
			errors.rejectValue(prefix + "comune", "error.empty");
		if(sede.getIndirizzo() == null || sede.getIndirizzo().isEmpty())
			errors.rejectValue(prefix + "indirizzo", "error.empty");
		if(sede.getCap() == null || sede.getCap().isEmpty())
			errors.rejectValue(prefix + "cap", "error.empty");
		if(sede.getTelefono() == null || sede.getTelefono().isEmpty())
			errors.rejectValue(prefix + "telefono", "error.empty");
		if(sede.getFax() == null || sede.getFax().isEmpty())
			errors.rejectValue(prefix + "fax", "error.empty");
		if(sede.getEmail() == null || sede.getEmail().isEmpty())
			errors.rejectValue(prefix + "email", "error.empty");

		Utils.logDebugErrorFields(LOGGER, errors);
	}
}
