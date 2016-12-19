package it.tredi.ecm.web.validator;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.utils.Utils;

@Component
public class DatiAccreditamentoValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatiAccreditamentoValidator.class);

	@Autowired private FileValidator fileValidator;

	public void validate(Object target, Errors errors, String prefix, Set<File> files, Long providerId) throws Exception{
		LOGGER.info(Utils.getLogMessage("Validazione DatiAccreditamento"));
		DatiAccreditamento dati = (DatiAccreditamento) target;
		validateDatiAccreditamento(target, errors, prefix);
		validateFilesConCondizione(files, errors, "", dati, providerId);
		validateFilesObbligatori(files, errors, "", providerId);
		Utils.logDebugErrorFields(LOGGER, errors);
	}

	private void validateDatiAccreditamento(Object target, Errors errors, String prefix){
		DatiAccreditamento datiAccreditamento = (DatiAccreditamento)target;
		if(datiAccreditamento.getTipologiaAccreditamento() == null || datiAccreditamento.getTipologiaAccreditamento().isEmpty())
			errors.rejectValue(prefix + "tipologiaAccreditamento", "error.empty");
		if(datiAccreditamento.getProcedureFormative() == null || datiAccreditamento.getProcedureFormative().isEmpty())
			errors.rejectValue(prefix + "procedureFormative", "error.empty");
		if(datiAccreditamento.getProfessioniAccreditamento() == null || datiAccreditamento.getProfessioniAccreditamento().isEmpty())
			errors.rejectValue(prefix + "professioniAccreditamento", "error.empty");
		if(datiAccreditamento.getDiscipline() == null || datiAccreditamento.getDiscipline().isEmpty())
			errors.rejectValue(prefix + "discipline", "error.empty");
		if(datiAccreditamento.getNumeroDipendentiFormazioneTempoIndeterminato() == null)
			errors.rejectValue(prefix + "numeroDipendentiFormazioneTempoIndeterminato", "error.empty");
		if(datiAccreditamento.getNumeroDipendentiFormazioneAltro() == null)
			errors.rejectValue(prefix + "numeroDipendentiFormazioneAltro", "error.empty");

		if(datiAccreditamento.getAccreditamento().isProvvisorio()) {
			//controllo di tipo cronologico, se inserisco un anno, gli altri più recenti diventano obbligatori
			if(datiAccreditamento.getDatiEconomici().getFatturatoComplessivoValoreTre() != null) {
				if(datiAccreditamento.getDatiEconomici().getFatturatoComplessivoValoreDue() == null)
					errors.rejectValue(prefix + "datiEconomici.fatturatoComplessivoValoreDue", "error.empty");
				if(datiAccreditamento.getDatiEconomici().getFatturatoComplessivoValoreUno() == null)
					errors.rejectValue(prefix + "datiEconomici.fatturatoComplessivoValoreUno", "error.empty");
			}
			if(datiAccreditamento.getDatiEconomici().getFatturatoComplessivoValoreDue() != null) {
				if(datiAccreditamento.getDatiEconomici().getFatturatoComplessivoValoreUno() == null)
					errors.rejectValue(prefix + "datiEconomici.fatturatoComplessivoValoreUno", "error.empty");
			}
			if(datiAccreditamento.getDatiEconomici().getFatturatoFormazioneValoreTre() != null) {
				if(datiAccreditamento.getDatiEconomici().getFatturatoFormazioneValoreDue() == null)
					errors.rejectValue(prefix + "datiEconomici.fatturatoFormazioneValoreDue", "error.empty");
				if(datiAccreditamento.getDatiEconomici().getFatturatoFormazioneValoreUno() == null)
					errors.rejectValue(prefix + "datiEconomici.fatturatoFormazioneValoreUno", "error.empty");
			}
			if(datiAccreditamento.getDatiEconomici().getFatturatoFormazioneValoreDue() != null) {
				if(datiAccreditamento.getDatiEconomici().getFatturatoFormazioneValoreUno() == null)
					errors.rejectValue(prefix + "datiEconomici.fatturatoFormazioneValoreUno", "error.empty");
			}
		}
		if(datiAccreditamento.getAccreditamento().isStandard()) {
			if(datiAccreditamento.getDatiEconomici().getFatturatoComplessivoValoreDue() == null)
				errors.rejectValue(prefix + "datiEconomici.fatturatoComplessivoValoreDue", "error.empty");
			if(datiAccreditamento.getDatiEconomici().getFatturatoComplessivoValoreUno() == null)
				errors.rejectValue(prefix + "datiEconomici.fatturatoComplessivoValoreUno", "error.empty");
			if(datiAccreditamento.getDatiEconomici().getFatturatoFormazioneValoreDue() == null)
				errors.rejectValue(prefix + "datiEconomici.fatturatoFormazioneValoreDue", "error.empty");
			if(datiAccreditamento.getDatiEconomici().getFatturatoFormazioneValoreUno() == null)
				errors.rejectValue(prefix + "datiEconomici.fatturatoFormazioneValoreUno", "error.empty");
		}
	}

	@SuppressWarnings("unchecked")
	private void validateFilesConCondizione(Object target, Errors errors, String prefix, DatiAccreditamento dati, Long providerId) throws Exception{
		LOGGER.debug("VALIDAZIONE ALLEGATI CON CONDIZIONE");
		Set<File> files = null;
		if(target != null)
			files = (Set<File>) target;
		else
			files = new HashSet<File>();
		File estrattoBilancioComplessivo = null;

		for(File file : files){
			if(file != null){
				if(file.isESTRATTOBILANCIOCOMPLESSIVO())
					estrattoBilancioComplessivo = file;
			}
		}
		fileValidator.validateWithCondition(estrattoBilancioComplessivo, errors, prefix + "estrattoBilancioComplessivo", dati.getDatiEconomici().hasFatturatoComplessivo(), providerId);
	}

	@SuppressWarnings("unchecked")
	private void validateFilesObbligatori(Object target, Errors errors, String prefix, Long providerId) throws Exception{
		LOGGER.debug("VALIDAZIONE ALLEGATI OBBLIGATORI");
		Set<File> files = null;
		if(target != null)
			files = (Set<File>) target;
		else
			files = new HashSet<File>();
		File funzionigramma = null;
		File organigramma = null;
		File estrattoBilancioFormazione = null;

		for(File file : files){
			if(file != null && !file.isNew()){
				if(file.isFUNZIONIGRAMMA())
					funzionigramma = file;
				else if(file.isORGANIGRAMMA())
					organigramma = file;
				else if(file.isESTRATTOBILANCIOFORMAZIONE())
					estrattoBilancioFormazione = file;
			}
		}
		fileValidator.validate(funzionigramma, errors, prefix + "funzionigramma",providerId);
		fileValidator.validate(organigramma, errors, prefix + "organigramma", providerId);
		fileValidator.validate(estrattoBilancioFormazione, errors, prefix + "estrattoBilancioFormazione", providerId);
	}
}
