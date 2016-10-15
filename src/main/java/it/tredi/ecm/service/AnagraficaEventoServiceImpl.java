package it.tredi.ecm.service;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.AnagraficaEvento;
import it.tredi.ecm.dao.repository.AnagraficaEventoRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class AnagraficaEventoServiceImpl implements AnagraficaEventoService {

	private static Logger LOGGER = LoggerFactory.getLogger(AnagraficaEventoServiceImpl.class);
	
	@Autowired private AnagraficaEventoRepository anagraficaEventoRepository;
	
	@Override
	public AnagraficaEvento getAnagraficaEvento(Long anagraficaEventoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero AnagraficheEvento : " + anagraficaEventoId));
		return anagraficaEventoRepository.findOne(anagraficaEventoId);
	}
	
	@Override
	public AnagraficaEvento getAnagraficaEventoByCodiceFiscaleForProvider(String codiceFiscale, Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero AnagraficheEvento : " + codiceFiscale + " per provider " + providerId));
		return anagraficaEventoRepository.findOneByAnagraficaCodiceFiscaleAndProviderId(codiceFiscale, providerId);
	}
	
	@Override
	public Set<AnagraficaEvento> getAllAnagaficheByProvider(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero AnagraficheEvento per provider: " + providerId));
		return anagraficaEventoRepository.findAllByProviderId(providerId);
	}

	@Override
	@Transactional
	public void save(AnagraficaEvento anagraficaEvento) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio AnagraficheEvento per provider: " + anagraficaEvento.getProvider().getId()));
		anagraficaEventoRepository.save(anagraficaEvento);
	}

}
