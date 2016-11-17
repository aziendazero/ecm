package it.tredi.ecm.web.validator;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AccreditamentoAllegatiWrapper;

@Component
public class AccreditamentoAllegatiValidator{
private static final Logger LOGGER = LoggerFactory.getLogger(AccreditamentoAllegatiValidator.class);

	@Autowired private FileValidator fileValidator;
	@Autowired private AccreditamentoService accreditamentoService;

	public void validate(Object target, Errors errors, String prefix, Set<File> files, Long providerId) throws Exception{
		LOGGER.info(Utils.getLogMessage("Validazione Allegati Accreditamento"));
		AccreditamentoAllegatiWrapper wrapper = (AccreditamentoAllegatiWrapper) target;
		validateFiles(files, errors, "", wrapper.getAccreditamentoId(), providerId);
		Utils.logDebugErrorFields(LOGGER, errors);
	}

	@SuppressWarnings("unchecked")
	public void validateFiles(Object target, Errors errors, String prefix, Long accreditamentoId, Long providerId) throws Exception{
		Set<File> files = null;
		if(target != null)
			files = (Set<File>) target;
		else
			files = new HashSet<File>();

		File attoCostitutivo = null;
		File esperienzaFormazione = null;
		File utilizzo = null;
		File sistemaInformatico = null;
		File pianoQualita = null;
		File dichiarazioneLegale = null;
		File dichiarazioneEsclusione = null;

		for(File file : files){
			if(file != null && !file.isNew()){
				if(file.isATTOCOSTITUTIVO())
					attoCostitutivo = file;
				else if(file.isESPERIENZAFORMAZIONE())
					esperienzaFormazione = file;
				else if(file.isDICHIARAZIONELEGALE())
					dichiarazioneLegale = file;
				else if(file.isPIANOQUALITA())
					pianoQualita = file;
				else if(file.isUTILIZZO())
					utilizzo = file;
				else if(file.isSISTEMAINFORMATICO())
					sistemaInformatico = file;
				else if(file.isDICHIARAZIONEESCLUSIONE())
					dichiarazioneEsclusione = file;
			}
		}

		Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);

		fileValidator.validate(attoCostitutivo, errors, prefix + "attoCostitutivo", providerId);
		fileValidator.validateWithCondition(esperienzaFormazione, errors, prefix + "esperienzaFormazione", accreditamento.getDatiAccreditamento().getDatiEconomici().hasFatturatoFormazione(), providerId);
		fileValidator.validate(utilizzo, errors, prefix + "utilizzo", providerId);
		fileValidator.validate(sistemaInformatico, errors, prefix + "sistemaInformatico", providerId);
		fileValidator.validate(pianoQualita, errors, prefix + "pianoQualita", providerId);
		fileValidator.validate(dichiarazioneLegale, errors, prefix + "dichiarazioneLegale", providerId);
		
		if(dichiarazioneEsclusione != null && !dichiarazioneEsclusione.getNomeFile().isEmpty())
			fileValidator.validateFirma(dichiarazioneEsclusione, errors, prefix + "dichiarazioneEsclusione", providerId);
	}
}
