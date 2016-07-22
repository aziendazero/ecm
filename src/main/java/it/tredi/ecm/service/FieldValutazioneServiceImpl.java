package it.tredi.ecm.service;

import java.util.Set;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import it.tredi.ecm.dao.entity.FieldValutazione;
import it.tredi.ecm.dao.repository.FieldValutazioneRepository;

public class FieldValutazioneServiceImpl implements FieldValutazioneService {
	private static Logger LOGGER = LoggerFactory.getLogger(FieldValutazioneServiceImpl.class);
	
	@Autowired
	private FieldValutazioneRepository fieldValutazioneRepository;
	
	@Override
	public Set<FieldValutazione> getAllValutazioniForAccreditamento(Long accreditamentoId) {
		LOGGER.debug("Recupero delle valutazioni per la domanda di accreditamento: " + accreditamentoId); 
		return fieldValutazioneRepository.findAllByAccreditamentoId(accreditamentoId);
	}

	@Override
	@Transactional
	public void save(FieldValutazione valutazione) {
		LOGGER.debug("Salvataggio domanda di accreditamento");
		fieldValutazioneRepository.save(valutazione);
	}

}
