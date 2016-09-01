package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Seduta;

public interface SedutaRepository extends CrudRepository<Seduta, Long>{
	Set<Seduta> findAll();
}
