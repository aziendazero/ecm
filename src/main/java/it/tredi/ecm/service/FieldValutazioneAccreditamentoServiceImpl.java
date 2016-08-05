package it.tredi.ecm.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.repository.FieldValutazioneAccreditamentoRepository;

@Service
public class FieldValutazioneAccreditamentoServiceImpl implements FieldValutazioneAccreditamentoService {
	private static Logger LOGGER = LoggerFactory.getLogger(FieldValutazioneAccreditamentoServiceImpl.class);

	@Autowired
	private FieldValutazioneAccreditamentoRepository fieldValutazioneRepository;

	@Override
	public Set<FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamento(Long accreditamentoId) {
		LOGGER.debug("Recupero delle valutazioni per la domanda di accreditamento: " + accreditamentoId);
		return fieldValutazioneRepository.findAllByAccreditamentoId(accreditamentoId);
	}

	@Override
	public Map<IdFieldEnum, FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamentoAsMap(
			Long accreditamentoId) {
		LOGGER.debug("Recupero delle valutazioni as MAP per la domanda di accreditamento: " + accreditamentoId);
		Set<FieldValutazioneAccreditamento> fieldValutazioni = getAllFieldValutazioneForAccreditamento(accreditamentoId);

		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		for(FieldValutazioneAccreditamento field : fieldValutazioni){
			mappa.put(field.getIdField(), field);
		}

		return mappa;
	}

	@Override
	@Transactional
	public void save(FieldValutazioneAccreditamento valutazione) {
		LOGGER.debug("Salvataggio domanda di accreditamento");
		fieldValutazioneRepository.save(valutazione);
	}

}
