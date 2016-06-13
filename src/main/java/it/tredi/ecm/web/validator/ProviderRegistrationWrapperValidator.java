package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.service.bean.ProviderRegistrationWrapper;

@Component
public class ProviderRegistrationWrapperValidator{
	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderRegistrationWrapperValidator.class);
	
	@Autowired
	private AccountValidator accountValidator;
	@Autowired
	private FileValidator fileValidator;
	@Autowired
	private ProviderValidator providerValidator;
	
	public void validate(Object target, Errors errors) {
		LOGGER.debug("Validating ProviderRegistrationWrapper");
		ProviderRegistrationWrapper providerForm = (ProviderRegistrationWrapper)target;
		accountValidator.validate(providerForm.getProvider().getAccount(), errors, "provider.account.");
		
		providerValidator.validateForRegistrazione(providerForm.getProvider(), errors, "provider.");
		//TODO Richiedente e allegato solo per alcuni tipi di provider
		validateRichiedente(providerForm.getRichiedente(), errors);
		validateLegale(providerForm.getLegale(), errors);
		fileValidator.validate(providerForm.getDelegaRichiedenteFile(), errors, "delegaRichiedenteFile");
	}
	
	private void validateRichiedente(Persona richiedente, Errors errors){
		if(richiedente.getAnagrafica().getCognome().isEmpty())
			errors.rejectValue("richiedente.anagrafica.cognome", "error.empty");
		if(richiedente.getAnagrafica().getNome().isEmpty())
			errors.rejectValue("richiedente.anagrafica.nome", "error.empty");
		if(richiedente.getAnagrafica().getCodiceFiscale().isEmpty())
			errors.rejectValue("richiedente.anagrafica.codiceFiscale", "error.empty");
		if(richiedente.getIncarico().isEmpty())
			errors.rejectValue("richiedente.incarico", "error.empty");
		if(richiedente.getAnagrafica().getTelefono().isEmpty())
			errors.rejectValue("richiedente.anagrafica.telefono", "error.empty");
	}
	
	private void validateLegale(Persona legale, Errors errors){
		if(legale.getAnagrafica().getCognome().isEmpty())
			errors.rejectValue("legale.anagrafica.cognome", "error.empty");
		if(legale.getAnagrafica().getNome().isEmpty())
			errors.rejectValue("legale.anagrafica.nome", "error.empty");
		if(legale.getAnagrafica().getCodiceFiscale().isEmpty())
			errors.rejectValue("legale.anagrafica.codiceFiscale", "error.empty");
		if(legale.getAnagrafica().getPec().isEmpty())
			errors.rejectValue("legale.anagrafica.pec", "error.empty");
		if(legale.getAnagrafica().getEmail().isEmpty())
			errors.rejectValue("legale.anagrafica.email", "error.empty");
	}

}
