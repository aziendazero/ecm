package it.tredi.ecm.service;

import java.util.Set;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.repository.FieldValutazioneAccreditamentoRepository;

public class FieldValutazioneAccreditamentoServiceImpl implements FieldValutazioneAccreditamentoService {
	private static Logger LOGGER = LoggerFactory.getLogger(FieldValutazioneAccreditamentoServiceImpl.class);
	
	@Autowired
	private FieldValutazioneAccreditamentoRepository fieldValutazioneRepository;
	
	@Override
	public Set<FieldValutazioneAccreditamento> getAllValutazioniForAccreditamento(Long accreditamentoId) {
		LOGGER.debug("Recupero delle valutazioni per la domanda di accreditamento: " + accreditamentoId); 
		return fieldValutazioneRepository.findAllByAccreditamentoId(accreditamentoId);
	}

	@Override
	@Transactional
	public void save(FieldValutazioneAccreditamento valutazione) {
		LOGGER.debug("Salvataggio domanda di accreditamento");
		fieldValutazioneRepository.save(valutazione);
	}

}
