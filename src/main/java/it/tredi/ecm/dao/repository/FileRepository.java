package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.File;

public interface FileRepository extends CrudRepository<File, Long> {
	Set<File> findByPersonaId(Long id); 
	Set<File> findByProviderId(Long id); 
	File findOneByPersonaIdAndTipo(Long id, String tipo); 
	Set<File> findAll();
}
