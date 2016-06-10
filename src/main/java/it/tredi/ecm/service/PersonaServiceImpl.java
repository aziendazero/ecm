package it.tredi.ecm.service;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.Persona;
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
		return personaRepository.findOne(id);
	}
	
	@Override
	public Persona getPersonaByRuolo(Ruolo ruolo, Long providerId) {
		LOGGER.info("Recupero " + ruolo + " del provider " + providerId);
		return personaRepository.findOneByRuoloAndProviderId(ruolo, providerId);
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
	@Transactional
	public void delete(Long id) {
		LOGGER.debug("Eliminazione Persona " + id);
		personaRepository.delete(id);
	}

}
