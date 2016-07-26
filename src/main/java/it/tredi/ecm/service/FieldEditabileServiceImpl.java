package it.tredi.ecm.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.FieldEditabile;
import it.tredi.ecm.dao.repository.FieldEditabileRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class FieldEditabileServiceImpl implements FieldEditabileService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FieldEditabileServiceImpl.class);
	
	@Autowired FieldEditabileRepository fieldEditabileRepository;  
	
	@Override
	public Set<FieldEditabile> getAllFieldEditabileForAccreditamento(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recuipero lista di IdEditabili per Domanda Accreditamento: " + accreditamentoId));
		return fieldEditabileRepository.findAllByAccreditamentoId(accreditamentoId);
	}
	@Override
	public Set<FieldEditabile> getAllFieldEditabileForAccreditamentoAndObject(Long accreditamentoId,
			Long objectReference) {
		LOGGER.debug(Utils.getLogMessage("Recuipero lista di IdEditabili per Domanda Accreditamento: " + accreditamentoId + " riferiti all'oggetto: " + objectReference));
		return fieldEditabileRepository.findAllByAccreditamentoIdAndObjectReference(accreditamentoId, objectReference);
	}
}
