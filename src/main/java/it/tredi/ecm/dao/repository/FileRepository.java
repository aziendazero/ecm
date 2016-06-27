package it.tredi.ecm.dao.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.enumlist.FileEnum;

public interface FileRepository extends CrudRepository<File, Long> {
	@Query("SELECT f.tipo,f.id FROM File f WHERE f.tipo IN :modelli")
	List<Object[]> findModelFilesIds(@Param("modelli") Set<FileEnum> modelli);
}
