package it.tredi.ecm.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.repository.PersonaRepository;

@Service
public class PersonaServiceImpl implements PersonaService {
	private static Logger LOGGER = LoggerFactory.getLogger(PersonaServiceImpl.class);
	
	@Autowired
	private PersonaRepository personaRepository;
	
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
		LOGGER.debug("Saving Persona");
		if(persona.getAnagrafica().getProvider() == null)
			persona.getAnagrafica().setProvider(persona.getProvider());
		personaRepository.save(persona);
	}

}
