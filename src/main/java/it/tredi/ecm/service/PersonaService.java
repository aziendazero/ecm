package it.tredi.ecm.service;

import it.tredi.ecm.dao.entity.Persona;

public interface PersonaService {
	public Persona getPersona(Long id); 
	public void save(Persona persona);
}
