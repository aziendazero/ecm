package it.tredi.ecm.service;

import java.util.List;
import java.util.Set;

import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.enumlist.IdentificativoPersonaRuoloEvento;

public interface PersonaEventoService {

	Set<Long> getAllEventoIdByNomeAndCognomeDocente(String nome, String cognome);
	
	IdentificativoPersonaRuoloEvento prossimoIdentificativoPersonaRuoloEventoNonUtilizzato(List<PersonaEvento> personeEvento);
	IdentificativoPersonaRuoloEvento prossimoIdentificativoPersonaRuoloEventoTempNonUtilizzato(List<PersonaEvento> personeEvento);
	void setIdentificativoPersonaRuoloEvento(List<PersonaEvento> personeEvento);
	void setIdentificativoPersonaRuoloEventoTemp(List<PersonaEvento> personeEvento);

}
