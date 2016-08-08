package it.tredi.ecm.web.validator;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;

@Component
public class ValutazioneValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(ValutazioneValidator.class);

	public void validateValutazione(Object target, Errors errors, String prefix) {
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = (Map<IdFieldEnum, FieldValutazioneAccreditamento>) target;
		for (Map.Entry<IdFieldEnum, FieldValutazioneAccreditamento> entry : mappa.entrySet()) {
			if(entry.getValue().getEsito() == null)
				errors.rejectValue(prefix + entry.getKey().getNameRef(), "error.atleast_one_empty");
			else
				if(entry.getValue().getEsito() == false && (entry.getValue().getNote() == null
				|| entry.getValue().getNote().isEmpty()))
					errors.rejectValue(prefix + entry.getKey().getNameRef(), "error.note_obbligatorie");
		}
	}
}
