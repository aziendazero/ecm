package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.enumlist.Ruolo;

public interface PersonaService {
	public Persona getPersona(Long id); 
	public Persona getPersonaByRuolo(Ruolo ruolo, Long providerId);
	public void save(Persona persona);
	
	public Set<Anagrafica> getAllAnagraficheByProviderId(Long providerId);
}
