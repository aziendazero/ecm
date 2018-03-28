package it.tredi.ecm.web.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.enumlist.RuoloPersonaEventoEnum;

@Component
public class PersonaEventoValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(PersonaEventoValidator.class);

	private boolean checkSamePersonaEventoWithRuoloAndTitolare(PersonaEvento personaEvento, PersonaEvento personaCompare) {
		if(
			equals(personaEvento.getAnagrafica().getCodiceFiscale(), personaCompare.getAnagrafica().getCodiceFiscale())
			&& equals(personaEvento.getRuolo(), personaCompare.getRuolo())
			&& equals(personaEvento.getTitolare(), personaCompare.getTitolare())			
			) {
			return true;
		}
		return false;
	}
	
	private boolean checkSamePersonaEventoWithSvolgeAttivitaDiDocenza(PersonaEvento personaEvento, PersonaEvento personaCompare) {
		if(
			equals(personaEvento.getAnagrafica().getCodiceFiscale(), personaCompare.getAnagrafica().getCodiceFiscale())
			&& personaEvento.isSvolgeAttivitaDiDocenza() == personaCompare.isSvolgeAttivitaDiDocenza()
			) {
			return true;
		}
		return false;
	}

	private boolean checkSamePersonaEvento(PersonaEvento personaEvento, PersonaEvento personaCompare) {
		if(
			equals(personaEvento.getAnagrafica().getCodiceFiscale(), personaCompare.getAnagrafica().getCodiceFiscale())
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
	
	public Map<String, String> validateAnagraficaBaseEventoWithRuoloAndTitolare(PersonaEvento personaEvento, List<PersonaEvento> personaEventoList, boolean modifica, String prefix) throws Exception {
		Map<String, String> errMap = new HashMap<String, String>();
		for(PersonaEvento p : personaEventoList) {
			if(!modifica || !personaEvento.getId().equals(p.getId())) {
				if(checkSamePersonaEventoWithRuoloAndTitolare(personaEvento, p)) {
					errMap.put(prefix + "codice_fiscale", "error.persona_evento_duplicated_ruolo_titolare");
					break;
				}
			}
		}

		return errMap;
	}
	
	public Map<String, String> validateAnagraficaBaseEventoWithSvolgeAttivitaDiDocenza(PersonaEvento personaEvento, List<PersonaEvento> personaEventoList, boolean modifica, String prefix) throws Exception {
		Map<String, String> errMap = new HashMap<String, String>();
		for(PersonaEvento p : personaEventoList) {
			if(!modifica || !personaEvento.getId().equals(p.getId())) {
				if(checkSamePersonaEventoWithSvolgeAttivitaDiDocenza(personaEvento, p)) {
					errMap.put(prefix + "codice_fiscale", "error.persona_evento_duplicated_svolge_attivita_di_ricerca");
					break;
				}
			}
		}

		return errMap;
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
	
	private String codiceFiscaleSvolgeAttivitaDocenzaKey(PersonaEvento p) {
		return p.getAnagrafica().getCodiceFiscale() + p.isSvolgeAttivitaDiDocenza();
	}

	public boolean validateAnagraficaBaseEventoWithSvolgeAttivitaDiDocenza(List<PersonaEvento> personaEventoList, Errors errors, String listName) throws Exception {
		Map<String, List<Integer>> checkMap = new HashMap<String, List<Integer>>();
		boolean isValid = true;
		int counter = 0;
		String key;
		for(PersonaEvento p : personaEventoList) {
			key = codiceFiscaleSvolgeAttivitaDocenzaKey(p);
			List<Integer> integerList = checkMap.get(key);
			if(integerList == null) {
				integerList = new ArrayList<Integer>();
				integerList.add(counter);
				checkMap.put(key, integerList);
			} else {
				//trovato doppione
				integerList.add(counter);
				isValid = false;
			}
			counter++;
		}
		for(List<Integer> counters : checkMap.values()) {
			if(counters.size() > 1) {
				for(Integer i : counters) {
					errors.rejectValue(listName + "["+i+"]", "");
				}
			}
		}

		return isValid;
	}

	public boolean validateAnagraficaBaseEvento(List<PersonaEvento> personaEventoList, Errors errors, String listName) throws Exception {
		Map<String, List<Integer>> checkMap = new HashMap<String, List<Integer>>();
		boolean isValid = true;
		int counter = 0;
		for(PersonaEvento p : personaEventoList) {
			List<Integer> integerList = checkMap.get(p.getAnagrafica().getCodiceFiscale());
			if(integerList == null) {
				integerList = new ArrayList<Integer>();
				integerList.add(counter);
				checkMap.put(p.getAnagrafica().getCodiceFiscale(), integerList);
			} else {
				//trovato doppione
				integerList.add(counter);
				isValid = false;
			}
			counter++;
		}
		for(List<Integer> counters : checkMap.values()) {
			if(counters.size() > 1) {
				for(Integer i : counters) {
					errors.rejectValue(listName + "["+i+"]", "");
				}
			}
		}

		return isValid;
	}
}
