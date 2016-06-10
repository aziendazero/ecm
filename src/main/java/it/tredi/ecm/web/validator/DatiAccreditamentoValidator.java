package it.tredi.ecm.web.validator;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.DatiEconomici;
import it.tredi.ecm.dao.entity.File;

@Component
public class DatiAccreditamentoValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatiAccreditamentoValidator.class);
	
	@Autowired
	private FileValidator fileValidator;
	
	public void validate(Object target, Errors errors, String prefix, Set<File> files){
		LOGGER.debug("Validazione Dati Accreditamento");
		validateDatiAccreditamento(target, errors, prefix);
		validateFilesEconomici(files, errors, "", ((DatiAccreditamento) target).getDatiEconomici());
		validateFilesStrutturaPersonale(files, errors, "");
	}
	
	private void validateDatiAccreditamento(Object target, Errors errors, String prefix){
		DatiAccreditamento datiAccreditamento = (DatiAccreditamento)target;
		if(datiAccreditamento.getTipologiaAccreditamento() == null || datiAccreditamento.getTipologiaAccreditamento().isEmpty())
			errors.rejectValue(prefix + "tipologiaAccreditamento", "error.empty");
		if(datiAccreditamento.getProcedureFormative() == null || datiAccreditamento.getProcedureFormative().isEmpty())
			errors.rejectValue(prefix + "procedureFormative", "error.empty");
		if(datiAccreditamento.getProfessioniAccreditamento() == null || datiAccreditamento.getProfessioniAccreditamento().isEmpty())
			errors.rejectValue(prefix + "professioniAccreditamento", "error.empty");
	}
	
	@SuppressWarnings("unchecked")
	public void validateFilesEconomici(Object target, Errors errors, String prefix, DatiEconomici datiEconomici){
		LOGGER.debug("VALIDAZIONE ALLEGATI ECONOMICI");
		if(!datiEconomici.isEmpty()){
			Set<File> files = null;
			if(target != null)
				files = (Set<File>) target;
			else
				files = new HashSet<File>();
			File estrattoBilancioFormazione = null;
			File budgetPrevisionale = null;
			
			for(File file : files){
				if(file != null){
					if(file.isESTRATTOBILANCIOFORMAZIONE())
						estrattoBilancioFormazione = file;
					else if(file.isBUDGETPREVISIONALE())
						budgetPrevisionale = file;
				}
			}
			fileValidator.validate(estrattoBilancioFormazione, errors, prefix + "estrattoBilancioFormazione");
			fileValidator.validate(budgetPrevisionale, errors, prefix + "budgetPrevisionale");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void validateFilesStrutturaPersonale(Object target, Errors errors, String prefix){
		LOGGER.debug("VALIDAZIONE ALLEGATI STRUTTARA PERSONALE");
		Set<File> files = null;
		if(target != null)
			files = (Set<File>) target;
		else
			files = new HashSet<File>();
		File funzionigramma = null;
		File organigramma = null;
		
		for(File file : files){
			if(file != null){
				if(file.isFUNZIONIGRAMMA())
					funzionigramma = file;
				else if(file.isORGANIGRAMMA())
					organigramma = file;
			}
		}
		fileValidator.validate(funzionigramma, errors, prefix + "funzionigramma");
		fileValidator.validate(organigramma, errors, prefix + "organigramma");
	}
}
