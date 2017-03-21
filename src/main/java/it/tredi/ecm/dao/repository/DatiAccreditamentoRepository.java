package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.DatiAccreditamento;

//public interface DatiAccreditamentoRepository extends CrudRepository<DatiAccreditamento, Long> {
public interface DatiAccreditamentoRepository extends JpaRepository<DatiAccreditamento, Long> {
	@Query("SELECT files.tipo From DatiAccreditamento d JOIN d.files files WHERE d.id = :id")
	public Set<String> findAllFileTipoByDatiAccreditamentoId(@Param("id") Long id);
}
