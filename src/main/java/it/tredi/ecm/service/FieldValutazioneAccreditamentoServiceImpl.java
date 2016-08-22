package it.tredi.ecm.service;

import java.util.Collection;
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
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.repository.FieldValutazioneAccreditamentoRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class FieldValutazioneAccreditamentoServiceImpl implements FieldValutazioneAccreditamentoService {
	private static Logger LOGGER = LoggerFactory.getLogger(FieldValutazioneAccreditamentoServiceImpl.class);

	@Autowired
	private FieldValutazioneAccreditamentoRepository fieldValutazioneAccreditamentoRepository;

	@Override
	public Set<FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamento(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero delle valutazioni per la domanda di accreditamento: " + accreditamentoId));
		return fieldValutazioneAccreditamentoRepository.findAllByAccreditamentoId(accreditamentoId);
	}

	@Override
	public Set<FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamentoAndObject(Long accreditamentoId, Long objectReference) {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di FieldValutazioneAccreditamento per Domanda Accreditamento: " + accreditamentoId + " riferiti all'oggetto: " + objectReference));
		return fieldValutazioneAccreditamentoRepository.findAllByAccreditamentoIdAndObjectReference(accreditamentoId, objectReference);
	}

	@Override
	public Map<IdFieldEnum, FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamentoAsMap(
			Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero delle valutazioni as MAP per la domanda di accreditamento: " + accreditamentoId));
		Set<FieldValutazioneAccreditamento> fieldValutazioni = getAllFieldValutazioneForAccreditamento(accreditamentoId);

		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		for(FieldValutazioneAccreditamento field : fieldValutazioni){
			mappa.put(field.getIdField(), field);
		}
		return mappa;
	}

	@Override
	public Map<IdFieldEnum, FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamentoAndObjectAsMap(
			Long accreditamentoId, Long objectReference) {
		LOGGER.debug(Utils.getLogMessage("Recupero delle valutazioni as MAP per la domanda di accreditamento: " + accreditamentoId + " riferiti all'oggetto: " + objectReference));
		Set<FieldValutazioneAccreditamento> fieldValutazioni = getAllFieldValutazioneForAccreditamentoAndObject(accreditamentoId, objectReference);

		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		for(FieldValutazioneAccreditamento field : fieldValutazioni){
			mappa.put(field.getIdField(), field);
		}

		return mappa;
	}

	@Override
	public Map<IdFieldEnum, FieldValutazioneAccreditamento> filterFieldValutazioneByObjectAsMap(Set<FieldValutazioneAccreditamento> set, Long id) {
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		for (FieldValutazioneAccreditamento f : set) {
			if(f.getObjectReference() == id) {
				mappa.put(f.getIdField(), f);
			}
		}
		return mappa;
	}

	@Override
	public Map<IdFieldEnum, FieldValutazioneAccreditamento> filterFieldValutazioneBySubSetAsMap(Set<FieldValutazioneAccreditamento> set, SubSetFieldEnum subset) {
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		for (FieldValutazioneAccreditamento f : set) {
			if(f.getIdField().getSubSetField().equals(subset))
				mappa.put(f.getIdField(), f);
		}
		return mappa;
	}

	@Override
	public Map<IdFieldEnum, FieldValutazioneAccreditamento> putSetFieldValutazioneInMap(Set<FieldValutazioneAccreditamento> set) {
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		for (FieldValutazioneAccreditamento f : set) {
			mappa.put(f.getIdField(), f);
		}
		return mappa;
	}

	@Override
	@Transactional
	public void save(FieldValutazioneAccreditamento valutazione) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio FieldValutazioni per la domanda di accreditamento"));
		fieldValutazioneAccreditamentoRepository.save(valutazione);
	}

	@Override
	@Transactional
	public Collection<FieldValutazioneAccreditamento> saveMapList(Map<IdFieldEnum, FieldValutazioneAccreditamento> valutazioneAsMap) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio FieldValutazioni per la domanda di accreditamento"));
		fieldValutazioneAccreditamentoRepository.save(valutazioneAsMap.values());
		return valutazioneAsMap.values();
	}
}
