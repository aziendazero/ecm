package it.tredi.ecm.service;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.FieldEditabile;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.repository.FieldEditabileRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class FieldEditabileServiceImpl implements FieldEditabileService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FieldEditabileServiceImpl.class);
	
	@Autowired FieldEditabileRepository fieldEditabileRepository;  
	
	@Override
	public Set<FieldEditabile> getAllFieldEditabileForAccreditamento(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di IdEditabili per Domanda Accreditamento: " + accreditamentoId));
		return fieldEditabileRepository.findAllByAccreditamentoId(accreditamentoId);
	}
	@Override
	public Set<FieldEditabile> getAllFieldEditabileForAccreditamentoAndObject(Long accreditamentoId,
			Long objectReference) {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di IdEditabili per Domanda Accreditamento: " + accreditamentoId + " riferiti all'oggetto: " + objectReference));
		return fieldEditabileRepository.findAllByAccreditamentoIdAndObjectReference(accreditamentoId, objectReference);
	}
	
	@Override
	@Transactional
	public void removeFieldEditabileForAccreditamento(Long accreditamentoId, Long objectReference, SubSetFieldEnum subset) {
		LOGGER.debug(Utils.getLogMessage("Eliminazione alcuni IdEditabili per Domanda Accreditamento: " + accreditamentoId));
		Set<FieldEditabile> toRemove = new HashSet<FieldEditabile>();
 		if(objectReference == null)
 			toRemove = Utils.getSubset(getAllFieldEditabileForAccreditamento(accreditamentoId), subset);
 		else
 			toRemove = Utils.getSubset(getAllFieldEditabileForAccreditamentoAndObject(accreditamentoId, objectReference), subset);
 		
 		fieldEditabileRepository.delete(toRemove);
	}
}
