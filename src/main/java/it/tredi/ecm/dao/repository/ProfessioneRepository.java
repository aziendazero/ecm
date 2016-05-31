package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Professione;

public interface ProfessioneRepository extends CrudRepository<Professione, Long> {
	public Set<Professione> findAll();
}
