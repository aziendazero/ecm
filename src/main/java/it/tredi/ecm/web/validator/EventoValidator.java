package it.tredi.ecm.web.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.utils.Utils;

@Component
public class EventoValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventoValidator.class);
	
	public void validate(Object target, Errors errors, String prefix, boolean insertPianoFormativo){
		validateBase(target, errors, prefix);
		if(!insertPianoFormativo)
			validateFull(target, errors, prefix);
		Utils.logDebugErrorFields(LOGGER, errors);
	}
	
	private void validateBase(Object target, Errors errors, String prefix){
		Utils.logInfo(LOGGER, "Validazione Evento Base");
		Evento evento = (Evento) target;
		if(evento.getProceduraFormativa() == null)
			errors.rejectValue(prefix + "proceduraFormativa", "error.empty");
		if(evento.getTitolo() == null || evento.getTitolo().isEmpty())
			errors.rejectValue(prefix + "titolo", "error.empty");
		if(evento.getObiettivoNazionale() == null)
			errors.rejectValue(prefix + "obiettivoNazionale", "error.empty");
		if(evento.getObiettivoRegionale() == null)
			errors.rejectValue(prefix + "obiettivoRegionale", "error.empty");
		if(evento.getProfessioniEvento() == null || evento.getProfessioniEvento().isEmpty())
			errors.rejectValue(prefix + "professioniEvento", "error.empty");
		if(evento.getDiscipline() == null || evento.getDiscipline().isEmpty())
			errors.rejectValue(prefix + "discipline", "error.empty");
	}
	
	private void validateFull(Object target, Errors errors, String prefix){
		Utils.logInfo(LOGGER, "Validazione Evento Full");
		Evento evento = (Evento) target;
		//TODO validateFull in caso di inserimento di un evento nn nel piano formativo
		if(evento.getProceduraFormativa() == null)
			errors.rejectValue(prefix + "proceduraFormativa", "error.empty");
		if(evento.getTitolo() == null || evento.getTitolo().isEmpty())
			errors.rejectValue(prefix + "titolo", "error.empty");
		if(evento.getObiettivoNazionale() == null)
			errors.rejectValue(prefix + "obiettivoNazionale", "error.empty");
		if(evento.getObiettivoRegionale() == null)
			errors.rejectValue(prefix + "obiettivoRegionale", "error.empty");
		if(evento.getProfessioniEvento() == null || evento.getProfessioniEvento().isEmpty())
			errors.rejectValue(prefix + "professioneEvento", "error.empty");
		if(evento.getDiscipline() == null || evento.getDiscipline().isEmpty())
			errors.rejectValue(prefix + "discipline", "error.empty");
	}
	
}
