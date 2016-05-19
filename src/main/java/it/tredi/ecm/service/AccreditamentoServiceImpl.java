package it.tredi.ecm.service;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.repository.AccreditamentoRepository;

@Service
public class AccreditamentoServiceImpl implements AccreditamentoService {

	private static Logger LOGGER = LoggerFactory.getLogger(AccreditamentoServiceImpl.class);
	
	@Autowired
	private AccreditamentoRepository accreditamentoRepository;
	
	@Override
	public Set<Accreditamento> getAllAccreditamentiForProvider(Long providerId) {
		LOGGER.debug("Recupero domande di accreditamento per il provider " + providerId);
		return accreditamentoRepository.findByProviderId(providerId);
	}
	
	@Override
	@Transactional
	public void save(Accreditamento accreditamento) {
		LOGGER.debug("Salvataggio domanda di accreditamento");
		accreditamentoRepository.save(accreditamento);
	}

}
