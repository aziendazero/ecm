package it.tredi.ecm.service;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;
import it.tredi.ecm.dao.repository.FieldIntegrazioneAccreditamentoRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class FieldIntegrazioneAccreditamentoServiceImpl implements FieldIntegrazioneAccreditamentoService{

	private static final Logger LOGGER = LoggerFactory.getLogger(FieldIntegrazioneAccreditamentoServiceImpl.class);

	@Autowired private FieldIntegrazioneAccreditamentoRepository fieldIntegrazioneAccreditamentoRepository;

	@Override
	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazioneForAccreditamento(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di FieldIntegrazioneAccreditamento per Domanda Accreditamento: " + accreditamentoId));
		return fieldIntegrazioneAccreditamentoRepository.findAllByAccreditamentoId(accreditamentoId);
	}
	@Override
	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazioneForAccreditamentoAndObject(Long accreditamentoId,
			Long objectReference) {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di FieldIntegrazioneAccreditamento per Domanda Accreditamento: " + accreditamentoId + " riferiti all'oggetto: " + objectReference));
		return fieldIntegrazioneAccreditamentoRepository.findAllByAccreditamentoIdAndObjectReference(accreditamentoId, objectReference);
	}

	@Override
	@Transactional
	public void save(List<FieldIntegrazioneAccreditamento> fieldIntegrazioneList) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio lista di FieldIntegrazioneAccreditamento"));
		fieldIntegrazioneAccreditamentoRepository.save(fieldIntegrazioneList);
	}

	@Override
	@Transactional
	public void saveSet(Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio lista di FieldIntegrazioneAccreditamento"));
		fieldIntegrazioneAccreditamentoRepository.save(fieldIntegrazioneList);
	}

	@Override
	@Transactional
	public void delete(Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList) {
		LOGGER.debug(Utils.getLogMessage("Eliminazione lista di FieldIntegrazioneAccreditamento"));
		for(FieldIntegrazioneAccreditamento f : fieldIntegrazioneList)
			LOGGER.debug(Utils.getLogMessage("Field: " + f.getId()));
		fieldIntegrazioneAccreditamentoRepository.delete(fieldIntegrazioneList);
	}

	@Override
	@Transactional
	public void update(Set<FieldIntegrazioneAccreditamento> toRemove, List<FieldIntegrazioneAccreditamento> toInsert) {
		delete(toRemove);
		save(toInsert);
	}

	@Override
	public Set<Long> getAllObjectIdByTipoIntegrazione(Long accreditamentoId, TipoIntegrazioneEnum tipo) {
		LOGGER.debug(Utils.getLogMessage("Recupero oggetti integrazione di tipo " + tipo + " per accreditamento " + accreditamentoId));
		return fieldIntegrazioneAccreditamentoRepository.findAllByAccreditamentoIdAndTipoIntegrazioneEnum(accreditamentoId,tipo);
	}

	@Override
	public Set<FieldIntegrazioneAccreditamento> getModifiedFieldIntegrazioneForAccreditamento(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero oggetti integrazione modificati per accreditamento " + accreditamentoId));
		return fieldIntegrazioneAccreditamentoRepository.findAllByAccreditamentoIdAndModificato(accreditamentoId, true);
	}
	@Override
	public void removeAllFieldIntegrazioneForAccreditamento(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Rimozione oggetti integrazione per accreditamento " + accreditamentoId));
		fieldIntegrazioneAccreditamentoRepository.deleteAllByAccreditamentoId(accreditamentoId);
	}
	@Override
	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazioneApprovedBySegreteria(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero tutti i field integrazione approvati dalla segreteria dell'accreditamento: " + accreditamentoId));
		return fieldIntegrazioneAccreditamentoRepository.findAllApprovedBySegreteria(accreditamentoId);
	}
}
