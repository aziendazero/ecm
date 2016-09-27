package it.tredi.ecm.service;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.FieldEditabileAccreditamento;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.repository.FieldEditabileAccreditamentoRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class FieldEditabileAccreditamentoServiceImpl implements FieldEditabileAccreditamentoService{

	private static final Logger LOGGER = LoggerFactory.getLogger(FieldEditabileAccreditamentoServiceImpl.class);

	@Autowired private FieldEditabileAccreditamentoRepository fieldEditabileAccreditamentoRepository;
	@Autowired private AccreditamentoService accreditamentoService;

	@Override
	public Set<FieldEditabileAccreditamento> getAllFieldEditabileForAccreditamento(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di IdEditabili per Domanda Accreditamento: " + accreditamentoId));
		return fieldEditabileAccreditamentoRepository.findAllByAccreditamentoId(accreditamentoId);
	}
	@Override
	public Set<FieldEditabileAccreditamento> getAllFieldEditabileForAccreditamentoAndObject(Long accreditamentoId,
			Long objectReference) {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di IdEditabili per Domanda Accreditamento: " + accreditamentoId + " riferiti all'oggetto: " + objectReference));
		return fieldEditabileAccreditamentoRepository.findAllByAccreditamentoIdAndObjectReference(accreditamentoId, objectReference);
	}

	@Override
	public Set<FieldEditabileAccreditamento> getFullLista(Long accreditamentoId, Long objectReference) {
		if(objectReference == null)
			return getAllFieldEditabileForAccreditamento(accreditamentoId);
		else
			return getAllFieldEditabileForAccreditamentoAndObject(accreditamentoId, objectReference);
	}

	@Override
	@Transactional
	public void insertFieldEditabileForAccreditamento(Long accreditamentoId, Long objectReference, SubSetFieldEnum subset, Set<IdFieldEnum> toInsert) {
		LOGGER.debug(Utils.getLogMessage("Inserimento di " + toInsert + " IdEditabili per Domanda Accreditamento: " + accreditamentoId));
		Set<FieldEditabileAccreditamento> subSetList = new HashSet<FieldEditabileAccreditamento>();
		if(objectReference == null)
			subSetList = Utils.getSubset(getAllFieldEditabileForAccreditamento(accreditamentoId), subset);
 		else
 			subSetList = Utils.getSubset(getAllFieldEditabileForAccreditamentoAndObject(accreditamentoId, objectReference), subset);

		if(toInsert != null){
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
			for(IdFieldEnum id : toInsert){
				if(Utils.getField(subSetList,id) == null){
					FieldEditabileAccreditamento field = new FieldEditabileAccreditamento();
					field.setAccreditamento(accreditamento);
					field.setIdField(id);
					if(objectReference != null)
						field.setObjectReference(objectReference);
					fieldEditabileAccreditamentoRepository.save(field);
				}
			}
		}
	}

	@Override
	@Transactional
	public void removeFieldEditabileForAccreditamento(Long accreditamentoId, Long objectReference, SubSetFieldEnum subset) {
		LOGGER.debug(Utils.getLogMessage("Eliminazione di alcuni IdEditabili per Domanda Accreditamento: " + accreditamentoId));
		Set<FieldEditabileAccreditamento> toRemove = new HashSet<FieldEditabileAccreditamento>();
 		if(objectReference == null)
 			toRemove = Utils.getSubset(getAllFieldEditabileForAccreditamento(accreditamentoId), subset);
 		else
 			toRemove = Utils.getSubset(getAllFieldEditabileForAccreditamentoAndObject(accreditamentoId, objectReference), subset);

 		fieldEditabileAccreditamentoRepository.delete(toRemove);
	}

	@Override
	@Transactional
	public void removeAllFieldEditabileForAccreditamento(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Eliminazione di tutti gli IdEditabili per Domanda Accreditamento: " + accreditamentoId));
		fieldEditabileAccreditamentoRepository.deleteAllByAccreditamentoId(accreditamentoId);
	}

	@Override
	@Transactional
	public void delete(FieldEditabileAccreditamento field){
		LOGGER.debug(Utils.getLogMessage("Eliminazione di un IdEditabili"));
		fieldEditabileAccreditamentoRepository.delete(field);
	}
}
