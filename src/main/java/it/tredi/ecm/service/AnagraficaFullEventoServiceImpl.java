package it.tredi.ecm.service;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.AnagraficaFullEvento;
import it.tredi.ecm.dao.repository.AnagraficaFullEventoRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class AnagraficaFullEventoServiceImpl implements AnagraficaFullEventoService {

	private static Logger LOGGER = LoggerFactory.getLogger(AnagraficaFullEventoServiceImpl.class);
	
	@Autowired private AnagraficaFullEventoRepository anagraficaFullEventoRepository;
	
	@Override
	public AnagraficaFullEvento getAnagraficaFullEvento(Long anagraficaFullEventoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero AnagraficheFullEvento : " + anagraficaFullEventoId));
		return anagraficaFullEventoRepository.findOne(anagraficaFullEventoId);
	}
	
	@Override
	public AnagraficaFullEvento getAnagraficaFullEventoByCodiceFiscaleForProvider(String codiceFiscale, Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero AnagraficheFullEvento : " + codiceFiscale + " per provider " + providerId));
		return anagraficaFullEventoRepository.findOneByAnagraficaCodiceFiscaleAndProviderId(codiceFiscale, providerId);
	}
	
	@Override
	public Set<AnagraficaFullEvento> getAllAnagraficheFullEventoByProvider(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero AnagraficheFullEvento per provider: " + providerId));
		return anagraficaFullEventoRepository.findAllByProviderId(providerId);
	}

	@Override
	@Transactional
	public void save(AnagraficaFullEvento anagraficaFullEvento) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio AnagraficheFullEvento per provider: " + anagraficaFullEvento.getProvider().getId()));
		anagraficaFullEventoRepository.save(anagraficaFullEvento);
	}

}
