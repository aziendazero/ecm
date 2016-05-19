package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.service.ProviderService;

@Component
public class ProviderValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderValidator.class);
	
	@Autowired
	private ProviderService providerService;
	
	private void validateProviderBase(Object target, Errors errors, String prefix) {
		LOGGER.debug("VALIDAZIONE PROVIDER DATI COMUNI");
		Provider providerForm = (Provider)target;
		if(providerForm.getDenominazioneLegale().isEmpty())
			errors.rejectValue(prefix + "denominazioneLegale", "error.empty");
		if(providerForm.getTipoOrganizzatore() == null || providerForm.getTipoOrganizzatore().getNome().isEmpty())
			errors.rejectValue(prefix + "tipoOrganizzatore", "error.empty");
		
	}
	
	public void validateForRegistrazione(Object target, Errors errors, String prefix) {
		LOGGER.debug("VALIDAZIONE PROVIDER PER REGISTRAZIONE");
		Provider providerForm = (Provider)target;
		validateProviderBase(providerForm, errors, prefix);
		
		//Presenza e univocità di codiceFiscale e/o partitaIva
		if(providerForm.getPartitaIva().isEmpty())
			errors.rejectValue(prefix + "partitaIva", "error.empty");
		else{
			Provider provider = providerService.getProviderByPartitaIva((providerForm.getPartitaIva()));
			if(provider != null){
				if(providerForm.isNew()){
					errors.rejectValue(prefix + "partitaIva", "error.partitaIva.duplicated");
				}else{
					if(provider.getId() != providerForm.getId()){
						errors.rejectValue(prefix + "partitaIva", "error.partitaIva.duplicated");
					}
				}
			}
		}
	}
	
	public void validateForAccreditamento(Object target, Errors errors, String prefix) {
		LOGGER.debug("VALIDAZIONE PROVIDER PER ACCREDITAMENTO");
		Provider providerForm = (Provider)target;
		validateProviderBase(providerForm, errors, prefix);
		
		if(providerForm.getRagioneSociale().isEmpty()){
			errors.rejectValue(prefix + "ragioneSociale", "error.empty");
		}
		if(providerForm.getNaturaOrganizzazione().isEmpty()){
			errors.rejectValue(prefix + "naturaOrganizzazione", "error.empty");
		}
	}
	
}
