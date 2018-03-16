package it.tredi.ecm.web.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.enumlist.RuoloPersonaEventoEnum;

@Component
public class PersonaEventoValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(PersonaEventoValidator.class);

	private boolean checkSamePersonaEvento(PersonaEvento personaEvento, PersonaEvento personaCompare) {
		if(
			equals(personaEvento.getAnagrafica().getCodiceFiscale(), personaCompare.getAnagrafica().getCodiceFiscale())
			&& equals(personaEvento.getRuolo(), personaCompare.getRuolo())
			&& equals(personaEvento.getTitolare(), personaCompare.getTitolare())			
			//&& personaEvento.isSvolgeAttivitaDiDocenza() == personaCompare.isSvolgeAttivitaDiDocenza()
			) {
			return true;
		}
		return false;
	}
	
	private boolean equals(String a, String b) {
		if(a == null || a.isEmpty()) {
			if(b == null || b.isEmpty())
				return true;
			else
				return false;
		} else {
			if(b == null || b.isEmpty())
				return false;
			else
				return a.equals(b);
		}
	}

	private boolean equals(RuoloPersonaEventoEnum a, RuoloPersonaEventoEnum b) {
		if(a == null) {
			if(b == null)
				return true;
			else
				return false;
		} else {
			if(b == null)
				return false;
			else
				return a == b;
		}
	}
	
	public Map<String, String> validateAnagraficaBaseEvento(PersonaEvento personaEvento, List<PersonaEvento> personaEventoList, boolean modifica, String prefix) throws Exception {
		Map<String, String> errMap = new HashMap<String, String>();
		for(PersonaEvento p : personaEventoList) {
			if(!modifica || !personaEvento.getId().equals(p.getId())) {
				if(checkSamePersonaEvento(personaEvento, p)) {
					errMap.put(prefix + "codice_fiscale", "error.persona_evento_duplicated");
					break;
				}
			}
		}

		return errMap;
	}
	
}
