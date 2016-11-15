package it.tredi.ecm.dao.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Provincia;

public interface ProvinciaRepository extends CrudRepository<Provincia, String> {
	//Set<Sede> findAllByProviderId(Long providerId);
	
	
	List<Provincia> findAll();
	
	List<Provincia> findAllByOrderByNomeAsc();
}
