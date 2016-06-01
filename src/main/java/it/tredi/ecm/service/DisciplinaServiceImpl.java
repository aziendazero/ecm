package it.tredi.ecm.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.repository.DisciplinaRepository;

public class DisciplinaServiceImpl implements DisciplinaService {
	private static Logger LOGGER = LoggerFactory.getLogger(DisciplinaServiceImpl.class);
	
	@Autowired
	private DisciplinaRepository disciplinaRepository;
	
	@Override
	public Set<Disciplina> getAllDiscipline() {
		LOGGER.debug("Recupero lista discipline");
		return disciplinaRepository.findAll();
	}
}
