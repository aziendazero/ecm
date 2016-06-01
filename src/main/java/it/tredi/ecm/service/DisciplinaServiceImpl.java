package it.tredi.ecm.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.repository.DisciplinaRepository;

@Service
public class DisciplinaServiceImpl implements DisciplinaService {
	private static Logger LOGGER = LoggerFactory.getLogger(DisciplinaServiceImpl.class);
	
	@Autowired
	private DisciplinaRepository disciplinaRepository;
	
	@Override
	public Set<Disciplina> getAllDiscipline() {
		LOGGER.debug("Recupero lista discipline");
		return disciplinaRepository.findAll();
	}
	
	@Override
	public void save(Disciplina disciplina) {
		LOGGER.debug("Salvataggio disciplina");
		disciplinaRepository.save(disciplina);
	}
	
	@Override
	public void saveAll(Set<Disciplina> disciplinaList) {
		LOGGER.debug("Salvataggio lista disciplina");
		disciplinaRepository.save(disciplinaList);
	}
}
