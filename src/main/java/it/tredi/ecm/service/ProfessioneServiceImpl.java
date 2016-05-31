package it.tredi.ecm.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.repository.ProfessioneRepository;

public class ProfessioneServiceImpl implements ProfessioneService {
	private static Logger LOGGER = LoggerFactory.getLogger(ProfessioneServiceImpl.class);
	
	@Autowired
	private ProfessioneRepository professioneRepository;
	
	@Override
	public Set<Professione> getAllProfessioni() {
		LOGGER.debug("Recupero lista professioni");
		return professioneRepository.findAll();
	}

}
