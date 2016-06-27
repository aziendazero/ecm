package it.tredi.ecm.service;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.repository.PersonaRepository;

@Service
public class PersonaServiceImpl implements PersonaService {
	private static Logger LOGGER = LoggerFactory.getLogger(PersonaServiceImpl.class);
	
	@Autowired
	private PersonaRepository personaRepository;
	@Autowired
	private AnagraficaService anagraficaService;
	
	@Override
	public Persona getPersona(Long id) {
		LOGGER.debug("Recupero Persona " + id);
		Persona persona = personaRepository.findOne(id);
		log(persona);
		return persona;
	}
	
	@Override
	public Persona getPersonaByRuolo(Ruolo ruolo, Long providerId) {
		LOGGER.info("Recupero " + ruolo + " del provider " + providerId);
		Persona persona =  personaRepository.findOneByRuoloAndProviderId(ruolo, providerId);
		log(persona);
		return persona;
	}
	
	@Override
	public Persona getPersonaByRuoloAndCodiceFiscale(Ruolo ruolo, String codiceFiscale, Long providerId) {
		LOGGER.debug("Recupero Persona (" + ruolo.name() + ") con codice fiscale " + codiceFiscale + " del provider (" + providerId + ")");
		Persona persona = personaRepository.findOneByRuoloAndAnagraficaCodiceFiscaleAndProviderId(ruolo, codiceFiscale, providerId);
		log(persona);
		return persona;
	}
	
	@Override
	public Persona getCoordinatoreComitatoScientifico(Long providerId) {
		LOGGER.debug("Recupero Persona (" + Ruolo.COMPONENTE_COMITATO_SCIENTIFICO.name() + ") del provider (" + providerId + ")");
		Persona persona = personaRepository.findOneByRuoloAndCoordinatoreComitatoScientificoAndProviderId(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, true, providerId);
		log(persona);
		return persona;
	}
		
	@Override
	@Transactional
	public void save(Persona persona) {
		LOGGER.debug("Salvataggio Persona (" + persona.getRuolo() + ")");
		if(persona.getAnagrafica().getProvider() == null)
			persona.getAnagrafica().setProvider(persona.getProvider());
		personaRepository.save(persona);
	}
	
	@Override
	public Set<Anagrafica> getAllAnagraficheByProviderId(Long providerId) {
		LOGGER.debug("Recupero Anagrafiche del Provider " + providerId);
		return anagraficaService.getAllAnagraficheByProviderId(providerId);
	}
	
	@Override
	public Set<Persona> getComitatoScientifico(Long providerId) {
		LOGGER.debug("Recupero Comitato Scientifico del Provider " + providerId);
		return personaRepository.findAllByRuoloAndProviderId(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, providerId);
	}
	
	@Override
	public int numeroComponentiComitatoScientifico(Long providerId) {
		LOGGER.debug("Recupero numero componenti Comitato Scientifico del Provider " + providerId);
		return personaRepository.countByRuoloAndProviderId(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, providerId);
	}
	
	@Override
	public int numeroComponentiComitatoScientificoConProfessioneSanitaria(Long providerId) {
		LOGGER.debug("Recupero numero componenti Comitato Scientifico con professione sanitaria del Provider " + providerId);
		return personaRepository.countByRuoloAndProviderIdAndProfessioneSanitaria(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, providerId, true);
	}
	
	@Override
	public int numeroProfessioniDistinteDeiComponentiComitatoScientifico(Long providerId) {
		LOGGER.debug("Recupero numero di professioni distinte dei componenti del Comitato Scientifico del Provider " + providerId);
		return personaRepository.countDistinctProfessioneByRuoloAndProviderId(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, providerId);
	}
	
	@Override
	public int numeroProfessioniDistinteAnalogheAProfessioniSelezionateDeiComponentiComitatoScientifico(Long providerId, Set<Professione> professioniSelezionate) {
		LOGGER.debug("Recupero numero di professioni distinte analoghe a qulle selezionate dei componenti del Comitato Scientifico del Provider " + providerId);
		return personaRepository.countDistinctProfessioneByRuoloAndProviderIdInProfessioniSelezionate(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, providerId, professioniSelezionate);
	}
	
	@Override
	@Transactional
	public void delete(Long id) {
		LOGGER.debug("Eliminazione Persona " + id);
		personaRepository.delete(id);
	}
	
	private void log(Persona persona){
		if(persona == null)
			LOGGER.debug("Persona non trovata");
		else
			LOGGER.debug("Persona trovata (" + persona.getId() + ")");
	}
}
