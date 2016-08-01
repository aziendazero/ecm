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
import it.tredi.ecm.utils.Utils;

@Service
public class PersonaServiceImpl implements PersonaService {
	private static Logger LOGGER = LoggerFactory.getLogger(PersonaServiceImpl.class);
	
	@Autowired private PersonaRepository personaRepository;
	@Autowired private AnagraficaService anagraficaService;
	
	@Override
	public Persona getPersona(Long id) {
		LOGGER.debug(Utils.getLogMessage("Recupero Persona " + id));
		Persona persona = personaRepository.findOne(id);
		log(persona);
		return persona;
	}
	
	@Override
	public Persona getPersonaByRuolo(Ruolo ruolo, Long providerId) {
		LOGGER.info(Utils.getLogMessage("Recupero " + ruolo + " del provider " + providerId));
		Persona persona =  personaRepository.findOneByRuoloAndProviderId(ruolo, providerId);
		log(persona);
		return persona;
	}
	
	@Override
	public Persona getPersonaByRuoloAndAnagraficaId(Ruolo ruolo, Long anagraficaId, Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero Persona (" + ruolo.name() + ") con angrafica " + anagraficaId + " del provider (" + providerId + ")"));
		Persona persona = personaRepository.findOneByRuoloAndAnagraficaIdAndProviderId(ruolo, anagraficaId, providerId);
		log(persona);
		return persona;
	}
	
	@Override
	public Persona getCoordinatoreComitatoScientifico(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero Persona (" + Ruolo.COMPONENTE_COMITATO_SCIENTIFICO.name() + ") del provider (" + providerId + ")"));
		Persona persona = personaRepository.findOneByRuoloAndCoordinatoreComitatoScientificoAndProviderId(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, true, providerId);
		log(persona);
		return persona;
	}
		
	@Override
	@Transactional
	public void save(Persona persona) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio Persona (" + persona.getRuolo() + ")"));
		if(persona.getAnagrafica().getProvider() == null)
			persona.getAnagrafica().setProvider(persona.getProvider());
		personaRepository.save(persona);
	}
	
	@Override
	public Set<Anagrafica> getAllAnagraficheByProviderId(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero Anagrafiche del Provider " + providerId));
		Set<Anagrafica> listAnagrafiche = anagraficaService.getAllAnagraficheByProviderId(providerId);
		
		//Elimino l'anagrafica del Rappresentante Legale
		Persona persona = getPersonaByRuolo(Ruolo.LEGALE_RAPPRESENTANTE, providerId);
		if(persona != null){
			listAnagrafiche.remove(persona.getAnagrafica());
		}
		return listAnagrafiche;
	}
	
	@Override
	public Set<Persona> getComitatoScientifico(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero Comitato Scientifico del Provider " + providerId));
		return personaRepository.findAllByRuoloAndProviderId(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, providerId);
	}
	
	@Override
	public int numeroComponentiComitatoScientifico(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero numero componenti Comitato Scientifico del Provider " + providerId));
		return personaRepository.countByRuoloAndProviderId(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, providerId);
	}
	
	@Override
	public int numeroComponentiComitatoScientificoConProfessioneSanitaria(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero numero componenti Comitato Scientifico con professione sanitaria del Provider " + providerId));
		return personaRepository.countByRuoloAndProviderIdAndProfessioneSanitaria(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, providerId, true);
	}
	
	@Override
	public int numeroProfessioniDistinteDeiComponentiComitatoScientifico(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero numero di professioni distinte dei componenti del Comitato Scientifico del Provider " + providerId));
		return personaRepository.countDistinctProfessioneByRuoloAndProviderId(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, providerId);
	}
	
	@Override
	public int numeroProfessioniDistinteAnalogheAProfessioniSelezionateDeiComponentiComitatoScientifico(Long providerId, Set<Professione> professioniSelezionate) {
		LOGGER.debug(Utils.getLogMessage("Recupero numero di professioni distinte analoghe a qulle selezionate dei componenti del Comitato Scientifico del Provider " + providerId));
		return personaRepository.countDistinctProfessioneByRuoloAndProviderIdInProfessioniSelezionate(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, providerId, professioniSelezionate);
	}
	
	@Override
	public Set<Professione> elencoProfessioniDistinteDeiComponentiComitatoScientifico(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero elenco delle professioni distinte dei componenti del Comitato Scientifico del Provider " + providerId));
		return personaRepository.findDistinctProfessioneByRuoloAndProviderId(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, providerId);
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
