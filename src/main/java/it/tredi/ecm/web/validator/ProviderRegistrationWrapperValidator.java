package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.service.bean.ProviderRegistrationWrapper;
import it.tredi.ecm.utils.Utils;

@Component
public class ProviderRegistrationWrapperValidator{
	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderRegistrationWrapperValidator.class);

	@Autowired private AccountValidator accountValidator;
	@Autowired private FileValidator fileValidator;
	@Autowired private ProviderValidator providerValidator;

	public void validate(Object target, Errors errors) {
		LOGGER.info(Utils.getLogMessage("Validazione ProviderRegistrationWrapper"));
		ProviderRegistrationWrapper providerForm = (ProviderRegistrationWrapper)target;
		accountValidator.validate(providerForm.getProvider().getAccount(), errors, "provider.account.");

		providerValidator.validateForRegistrazione(providerForm.getProvider(), errors, "provider.");
		validateRichiedente(providerForm.getRichiedente(), errors);
		validateLegale(providerForm.getLegale(), errors);

		//TODO Delegato consentito solo per alcuni tipi di Provider
		//allegato obbligatorio solo se e' stato selezionato il flag delegato
		if(providerForm.getDelegato() == null)
			errors.rejectValue("delegato", "error.empty");

		if(providerForm.getDelegato() != null && providerForm.getDelegato() == true){
			fileValidator.validate(providerForm.getDelega(), errors, "delega");
		}

		//check che il legale rappresentante e il delegato del legale rappresentante non abbiano lo stesso cv
		if(providerForm.getDelegato() != null && providerForm.getDelegato() == true) {
			String cvLegale = providerForm.getLegale().getAnagrafica().getCodiceFiscale();
			String cvDelegato = providerForm.getRichiedente().getAnagrafica().getCodiceFiscale();
			// evito il controllo se una delle due è vuota
			if (!cvLegale.isEmpty() && cvLegale.equals(cvDelegato))
				errors.rejectValue("richiedente.anagrafica.codiceFiscale", "error.stessoCf");
		}

		Utils.logDebugErrorFields(LOGGER, errors);

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
