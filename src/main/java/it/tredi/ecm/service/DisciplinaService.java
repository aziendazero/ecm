package it.tredi.ecm.service;

import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.Disciplina;

public interface DisciplinaService {
	public Set<Disciplina> getAllDiscipline();
	public void save(Disciplina disciplina);
	public void saveAll(Set<Disciplina> disciplinaList);

	public Map<String,String> getDisciplineMap();
}
