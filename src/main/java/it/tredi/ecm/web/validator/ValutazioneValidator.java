package it.tredi.ecm.web.validator;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;

@Component
public class ValutazioneValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(ValutazioneValidator.class);

	public void validateValutazione(Object target, Errors errors) {
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = (Map<IdFieldEnum, FieldValutazioneAccreditamento>) target;
		for (Map.Entry<IdFieldEnum, FieldValutazioneAccreditamento> entry : mappa.entrySet()) {
			String key = gestisciEccezioniKey(entry.getKey().getKey());
			if(entry.getValue().getEsito() == null) {
				errors.rejectValue(key, "error.atleast_one_empty");
			}
			else
				if(entry.getValue().getEsito() == false && (entry.getValue().getNote() == null
				|| entry.getValue().getNote().isEmpty()))
					errors.rejectValue(key, "error.note_obbligatorie");
		}
	}

	public void validateValutazioneComplessiva(Object target, Errors errors) {
		Set<Account> refereeGroup = (Set<Account>)target;
		if(refereeGroup == null || refereeGroup.size() != 3) {
			errors.rejectValue("refereeGroup", "error.numero_referee");
		}
	}

	//gestisce le eccezioni degli input raggruppati prendendo come rejectValue il primo valore
	private String gestisciEccezioniKey(String key) {
		switch(key) {
			case "datiAccreditamento.datiEconomici.fatturatoComplessivo": return "datiAccreditamento.datiEconomici.fatturatoComplessivoValoreUno";
			case "datiAccreditamento.datiEconomici.fatturatoFormazione": return "datiAccreditamento.datiEconomici.fatturatoFormazioneValoreUno";
			case "datiAccreditamento.numeroDipendenti": return "datiAccreditamento.numeroDipendentiFormazioneTempoIndeterminato";
			default: return key;
		}
	}
}
