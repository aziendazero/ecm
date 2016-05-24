package it.tredi.ecm.service;

import java.util.Set;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.repository.ValutazioneRepository;

public class ValutazioneServiceImpl implements ValutazioneService {
	private static Logger LOGGER = LoggerFactory.getLogger(ValutazioneServiceImpl.class);
	
	@Autowired
	private ValutazioneRepository valutazioneRepository;
	
	@Override
	public Set<Valutazione> getAllValutazioniForAccreditamento(Long accreditamentoId) {
		LOGGER.debug("Recupero delle valutazioni per la domanda di accreditamento: " + accreditamentoId); 
		return valutazioneRepository.findAllByAccreditamentoId(accreditamentoId);
	}

	@Override
	public Set<Valutazione> getAllValutazioniForSezioneAccreditamento(Long accreditamentoId, int start, int end) {
		LOGGER.debug("Recupero delle valutazioni per i campi (" + start + "-" + end + ") per la domanda di accreditamento: " + accreditamentoId); 
		return valutazioneRepository.findAllByAccreditamentoIdAndCampoBetween(accreditamentoId, start, end);
	}

	@Override
	@Transactional
	public void save(Valutazione valutazione) {
		LOGGER.debug("Salvataggio domanda di accreditamento");
		valutazioneRepository.save(valutazione);
	}

}
