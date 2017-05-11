package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Disciplina;

public interface DisciplinaRepository extends CrudRepository<Disciplina, Long> {
	public Set<Disciplina> findAll();
	Disciplina findOneByCodiceCogeaps(String codiceCogeaps);
}
