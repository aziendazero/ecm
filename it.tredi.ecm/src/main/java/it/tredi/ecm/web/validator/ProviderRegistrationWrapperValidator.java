package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.bean.ProviderRegistrationWrapper;

@Component
public class ProviderRegistrationWrapperValidator{
	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderRegistrationWrapperValidator.class);
	
	@Autowired
	private ProviderService providerService;
	@Autowired
	private AccountValidator accountValidator;
	
	public void validate(Object target, Errors errors,boolean saveMinimal) {
		LOGGER.debug("Validating ProviderRegistrationWrapper");
		ProviderRegistrationWrapper providerForm = (ProviderRegistrationWrapper)target;
		accountValidator.validate(providerForm.getProvider().getAccount(), errors, "provider.account.");
		validateProvider(providerForm.getProvider(), errors);
		if(!saveMinimal){
			validateRichiedente(providerForm.getRichiedente(), errors);
			validateLegale(providerForm.getLegale(), errors);
		}
	}
	
	private void validateProvider(Provider providerForm, Errors errors){
		if(providerForm.getDenominazioneLegale().isEmpty())
			errors.rejectValue("provider.denominazioneLegale", "error.empty");
		if(providerForm.getTipoOrganizzatore().isEmpty())
			errors.rejectValue("provider.tipoOrganizzatore", "error.empty");
		
		//Presenza e univocità di cfPiva
		if(providerForm.getCfPiva().isEmpty())
			errors.rejectValue("provider.cfPiva", "error.empty");
		else{
			Provider provider = providerService.getProviderByCfPiva(providerForm.getCfPiva());
			if(provider != null){
				if(providerForm.isNew()){
					errors.rejectValue("provider.cfPiva", "error.cfPiva.duplicated");
				}else{
					if(provider.getId() != providerForm.getId()){
						errors.rejectValue("provider.cfPiva", "error.cfPiva.duplicated");
					}
				}
			}
		}
	}
	
	private void validateRichiedente(Persona richiedente, Errors errors){
		if(richiedente.getAnagrafica().getCognome().isEmpty())
			errors.rejectValue("richiedente.anagrafica.cognome", "error.empty");
		if(richiedente.getAnagrafica().getNome().isEmpty())
			errors.rejectValue("richiedente.anagrafica.nome", "error.empty");
		if(richiedente.getAnagrafica().getCodiceFiscale().isEmpty())
			errors.rejectValue("richiedente.anagrafica.codiceFiscale", "error.empty");
		if(richiedente.getAnagrafica().getPec().isEmpty())
			errors.rejectValue("richiedente.anagrafica.pec", "error.empty");
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
