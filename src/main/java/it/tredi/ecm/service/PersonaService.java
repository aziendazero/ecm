package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.enumlist.Ruolo;

public interface PersonaService {
	public Persona getPersona(Long id);
	public Persona getPersonaByRuolo(Ruolo ruolo, Long providerId);
	public Persona getPersonaByRuoloAndCodiceFiscale(Ruolo ruolo, String codiceFiscale, Long providerId);
	public Persona getCoordinatoreComitatoScientifico(Long providerId);
	public void save(Persona persona);
	public void delete(Long id);
	
	public int numeroComponentiComitatoScientifico(Long providerId);
	public int numeroComponentiComitatoScientificoConProfessioneSanitaria(Long providerId);
	public int numeroProfessioniDistinteDeiComponentiComitatoScientifico(Long providerId);
	public int numeroProfessioniDistinteAnalogheAProfessioniSelezionateDeiComponentiComitatoScientifico(Long providerId, Set<Professione> professioniSelezionate);
	
	public Set<Anagrafica> getAllAnagraficheByProviderId(Long providerId);
	Set<Persona> getComitatoScientifico(Long providerId);
}
