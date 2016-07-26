package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;

@Component
public class ProviderValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderValidator.class);

	@Autowired private ProviderService providerService;

	private void validateProviderBase(Object target, Errors errors, String prefix) {
		Provider providerForm = (Provider)target;
		if(providerForm.getDenominazioneLegale().isEmpty())
			errors.rejectValue(prefix + "denominazioneLegale", "error.empty");
		if(providerForm.getTipoOrganizzatore() == null || providerForm.getTipoOrganizzatore().getNome().isEmpty())
			errors.rejectValue(prefix + "tipoOrganizzatore", "error.empty");

	}

	public void validateForRegistrazione(Object target, Errors errors, String prefix) {
		LOGGER.info(Utils.getLogMessage("Validazione Provider per Registrazione"));
		Provider providerForm = (Provider)target;
		validateProviderBase(providerForm, errors, prefix);

		//Presenza e univocità di codiceFiscale e/o partitaIva
//		if(providerForm.getPartitaIva().isEmpty() && providerForm.getCodiceFiscale().isEmpty())
//			errors.rejectValue(prefix + "partitaIva", "error.empty");
//		else{
//			Provider provider = providerService.getProviderByPartitaIva((providerForm.getPartitaIva()));
//			if(provider != null){
//				if(providerForm.isNew()){
//					errors.rejectValue(prefix + "partitaIva", "error.partitaIva.duplicated");
//				}else{
//					if(!provider.getId().equals(providerForm.getId())){
//						errors.rejectValue(prefix + "partitaIva", "error.partitaIva.duplicated");
//					}
//				}
//			}
//		}
//
//		if(providerForm.getPartitaIva().isEmpty() && providerForm.getCodiceFiscale().isEmpty())
//			errors.rejectValue(prefix + "codiceFiscale", "error.empty");
//		else{
//			Provider provider = providerService.getProviderByCodiceFiscale((providerForm.getCodiceFiscale()));
//			if(provider != null){
//				if(providerForm.isNew()){
//					errors.rejectValue(prefix + "codiceFiscale", "error.codiceFiscale.duplicated");
//				}else{
//					if(!provider.getId().equals(providerForm.getId())){
//						errors.rejectValue(prefix + "codiceFiscale", "error.codiceFiscale.duplicated");
//					}
//				}
//			}
//		}

		// se il flag hasPartitaIVA è true valido, altrimenti no
		if(providerForm.getHasPartitaIVA()) {
			//partita IVA obbligatoria

			//contollo su partita IVA esistente
			if(!providerForm.getPartitaIva().isEmpty()){
				//inserita partitaIva

				//cerco se esiste già un provider con quella partitaIva
				Provider provider = providerService.getProviderByPartitaIva((providerForm.getPartitaIva()));
				if(provider != null){
					//ho trovato un provider con la partitaIva..
					//se sono in registrazione di nuovo provider do errore
					if(providerForm.isNew()){
						errors.rejectValue(prefix + "partitaIva", "error.partitaIva.duplicated");
					}else{
						//se sono in modifica.. controllo che effettivamente sono in modifica del provider che ho trovato
						if(!provider.getId().equals(providerForm.getId())){
							errors.rejectValue(prefix + "partitaIva", "error.partitaIva.duplicated");
						}
					}
				}

				//cerco se esiste già un provider con quella partitaIva registrata nel codiceFiscale
				Provider providerCF = providerService.getProviderByCodiceFiscale((providerForm.getPartitaIva()));
				if(providerCF != null){
					//ho trovato un provider con la partitaIva..registrata nel campo cf
					//se sono in registrazione di nuovo provider do errore
					if(providerForm.isNew()){
						errors.rejectValue(prefix + "partitaIva", "error.partitaIva.duplicated");
					}else{
						//se sono in modifica.. controllo che effettivamente sono in modifica del provider che ho trovato
						if(!providerCF.getId().equals(providerForm.getId())){
							errors.rejectValue(prefix + "partitaIva", "error.partitaIva.duplicated");
						}
					}
				}
			}
			// se non ho la partitaIVA errore
			else {
				errors.rejectValue(prefix +  "partitaIva", "error.empty");
			}
		}


		//se entrambi vuoti..errore perchè almeno uno è obbligatorio
		if(providerForm.getCodiceFiscale().isEmpty()){
			errors.rejectValue(prefix + "codiceFiscale", "error.empty");
		}else{
			//inserito codiceFiscale

			//cerco se esiste già un provider con quella CodiceFiscale
			Provider provider = providerService.getProviderByCodiceFiscale((providerForm.getCodiceFiscale()));
			if(provider != null){
				//ho trovato un provider con il CodiceFiscale..
				//se sono in registrazione di nuovo provider do errore
				if(providerForm.isNew()){
					errors.rejectValue(prefix + "codiceFiscale", "error.codiceFiscale.duplicated");
				}else{
					//se sono in modifica.. controllo che effettivamente sono in modifica del provider che ho trovato
					if(!provider.getId().equals(providerForm.getId())){
						errors.rejectValue(prefix + "codiceFiscale", "error.codiceFiscale.duplicated");
					}
				}
			}

			//cerco se esiste già un provider con quel CodiceFiscale registrato nella partitaIva
			Provider providerPIVA = providerService.getProviderByPartitaIva((providerForm.getCodiceFiscale()));
			if(providerPIVA != null){
				//ho trovato un provider con il CodiceFiscale..registrata nel campo partitaIva
				//se sono in registrazione di nuovo provider do errore
				if(providerForm.isNew()){
					errors.rejectValue(prefix + "codiceFiscale", "error.codiceFiscale.duplicated");
				}else{
					//se sono in modifica.. controllo che effettivamente sono in modifica del provider che ho trovato
					if(!providerPIVA.getId().equals(providerForm.getId())){
						errors.rejectValue(prefix + "codiceFiscale", "error.codiceFiscale.duplicated");
					}
				}
			}
		}

		Utils.logDebugErrorFields(LOGGER, errors);
	}

	public void validateForAccreditamento(Object target, Errors errors, String prefix) {
		LOGGER.info(Utils.getLogMessage("Validazione Provider Accreditamento"));
		Provider providerForm = (Provider)target;
		validateProviderBase(providerForm, errors, prefix);

		if(providerForm.getRagioneSociale() == null){
			errors.rejectValue(prefix + "ragioneSociale", "error.empty");
		}
		if(providerForm.getNaturaOrganizzazione().isEmpty()){
			errors.rejectValue(prefix + "naturaOrganizzazione", "error.empty");
		}
		// se il flag hasPartitaIVA è true valido, altrimenti no
		if(providerForm.getHasPartitaIVA()) {
			//partita IVA obbligatoria

			//contollo su partita IVA esistente
			if(!providerForm.getPartitaIva().isEmpty()){
				//inserita partitaIva

				//cerco se esiste già un provider con quella partitaIva
				Provider provider = providerService.getProviderByPartitaIva((providerForm.getPartitaIva()));
				if(provider != null){
					//ho trovato un provider con la partitaIva..
					//se sono in registrazione di nuovo provider do errore
					if(providerForm.isNew()){
						errors.rejectValue(prefix + "partitaIva", "error.partitaIva.duplicated");
					}else{
						//se sono in modifica.. controllo che effettivamente sono in modifica del provider che ho trovato
						if(!provider.getId().equals(providerForm.getId())){
							errors.rejectValue(prefix + "partitaIva", "error.partitaIva.duplicated");
						}
					}
				}

				//cerco se esiste già un provider con quella partitaIva registrata nel codiceFiscale
				Provider providerCF = providerService.getProviderByCodiceFiscale((providerForm.getPartitaIva()));
				if(providerCF != null){
					//ho trovato un provider con la partitaIva..registrata nel campo cf
					//se sono in registrazione di nuovo provider do errore
					if(providerForm.isNew()){
						errors.rejectValue(prefix + "partitaIva", "error.partitaIva.duplicated");
					}else{
						//se sono in modifica.. controllo che effettivamente sono in modifica del provider che ho trovato
						if(!providerCF.getId().equals(providerForm.getId())){
							errors.rejectValue(prefix + "partitaIva", "error.partitaIva.duplicated");
						}
					}
				}
			}
			// se non ho la partitaIVA errore
			else {
				errors.rejectValue(prefix +  "partitaIva", "error.empty");
			}
		}
		Utils.logDebugErrorFields(LOGGER, errors);
	}

}
