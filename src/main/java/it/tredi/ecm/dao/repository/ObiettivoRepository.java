package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Obiettivo;

public interface ObiettivoRepository extends CrudRepository<Obiettivo, Long> {
	public Set<Obiettivo> findAll();
	public Set<Obiettivo> findAllByNazionale(boolean nazionale);
}
