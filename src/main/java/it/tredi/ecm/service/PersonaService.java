package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.enumlist.Ruolo;

public interface PersonaService {
	public Persona getPersona(Long id);
	public Persona getPersonaByRuolo(Ruolo ruolo, Long providerId);
	public Persona getPersonaByRuoloAndAnagraficaId(Ruolo ruolo, Long anagraficaId, Long providerId);
	public Persona getCoordinatoreComitatoScientifico(Long providerId);
	public void save(Persona persona);
	public void delete(Long id);
	
	public int numeroComponentiComitatoScientifico(Long providerId);
	public int numeroComponentiComitatoScientificoConProfessioneSanitaria(Long providerId);
	public int numeroProfessioniDistinteDeiComponentiComitatoScientifico(Long providerId);
	public int numeroProfessioniDistinteAnalogheAProfessioniSelezionateDeiComponentiComitatoScientifico(Long providerId, Set<Professione> professioniSelezionate);
	public Set<Professione> elencoProfessioniDistinteDeiComponentiComitatoScientifico(Long providerId);
	
	public Set<Anagrafica> getAllAnagraficheAttiveByProviderId(Long providerId);
	Set<Persona> getComitatoScientifico(Long providerId);
	
	public void saveFromIntegrazione(Persona persona);
	public void deleteFromIntegrazione(Long id);
	public Set<Persona> getComponentiComitatoScientificoFromIntegrazione(Long providerId);
}
