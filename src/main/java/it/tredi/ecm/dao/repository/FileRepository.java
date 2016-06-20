package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.File;

public interface FileRepository extends CrudRepository<File, Long> {
	Set<File> findByProviderId(Long id); 
	Set<File> findAll();
	@Query("SELECT f FROM File f WHERE f.tipo LIKE :prefix%")
    public Set<File> findModelFiles(@Param("prefix") String prefix);
	public void saveAndFlush(File file);
}
