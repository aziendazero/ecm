package it.tredi.ecm.web.validator;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.enumlist.EventoVersioneEnum;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoWrapper;

@Component
public class EventoValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventoValidator.class);

	@Autowired private FileValidator fileValidator;
	@Autowired private EventoService eventoService;
	@Autowired private EventoValidatorVersioneUno eventoValidatorVersioneUno;
	@Autowired private EventoValidatorVersioneDue eventoValidatorVersioneDue;

	public void validate(Object target, EventoWrapper wrapper, Errors errors, String prefix) throws Exception{
		Evento evento = (Evento) target;

		EventoVersioneEnum eventoVersione = eventoService.versioneEvento(evento);
		switch (eventoVersione) {
		case UNO_PRIMA_2018:
			eventoValidatorVersioneUno.validate(target, wrapper, errors, prefix);
			break;
		case DUE_DAL_2018:
			eventoValidatorVersioneDue.validate(target, wrapper, errors, prefix);			
			break;
		default:
			throw new Exception("Evento versione: " + eventoVersione + " non gestita");
		}
		
//		validateCommon(evento, errors, prefix);
//
//		if (evento instanceof EventoRES)
//			validateRES(((EventoRES) evento), wrapper, errors, prefix);
//		else if (evento instanceof EventoFSC)
//			validateFSC(((EventoFSC) evento), wrapper, errors, prefix);
//		else if (evento instanceof EventoFAD)
//			validateFAD(((EventoFAD) evento), wrapper, errors, prefix);

		Utils.logDebugErrorFields(LOGGER, errors);

	}

	public Map<String, String> validateContrattoSponsor(File sponsorFile, Long providerId, String prefix) throws Exception {
		Map<String, String> errMap = new HashMap<String, String>();

		if(sponsorFile == null || sponsorFile.isNew())
			errMap.put("file_"+prefix+"_button", "error.empty");
		else if(!fileValidator.validateFirmaCF(sponsorFile, providerId))
			errMap.put("file_"+prefix+"_button", "error.codiceFiscale.firmatario");

		return errMap;
	}

}
