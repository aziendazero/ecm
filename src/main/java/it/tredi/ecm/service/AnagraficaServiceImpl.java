package it.tredi.ecm.service;

import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.repository.AnagraficaRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class AnagraficaServiceImpl implements AnagraficaService {
	private static Logger LOGGER = LoggerFactory.getLogger(AnagraficaServiceImpl.class);
	
	@Autowired private AnagraficaRepository anagraficaRepository;
	
	@Override
	public Set<Anagrafica> getAllAnagraficheByProviderId(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero tutte le anagrafiche del provider " + providerId));
		return anagraficaRepository.findAllByProviderId(providerId);
	}

	@Override
	public Anagrafica getAnagrafica(Long id) {
		LOGGER.debug(Utils.getLogMessage("Recupero anagrafica " + id));
		return anagraficaRepository.findOne(id);
	}
	
	@Override
	public Optional<Long> getAnagraficaIdWithCodiceFiscaleForProvider(String codiceFiscale, Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Controllo univocità dell'anagrafica con codice fiscale " + codiceFiscale + " per il provider " + providerId));
		return anagraficaRepository.findOneByCodiceFiscaleAndProviderId(codiceFiscale, providerId);
	}
	
	@Override
	@Transactional
	public void save(Anagrafica anagrafica) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio anagrafica"));
		anagraficaRepository.save(anagrafica);
	}
}
