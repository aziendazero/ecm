package it.tredi.ecm.dao.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Seduta;

public interface SedutaRepository extends CrudRepository<Seduta, Long> {
	Set<Seduta> findAll();

	@Query("SELECT s FROM Seduta s WHERE (s.data = :date AND s.ora > :time) OR (s.data > :date)")
	Set<Seduta> findAllByDataAndOraAceptable(@Param("date") LocalDate date, @Param("time") LocalTime time);

	@Query("SELECT s FROM Seduta s WHERE s.dataoraSeduta < :nowPlusSedutaValidationMinute AND eseguitoTaskInsOdgAccreditamenti = false")
	//@EntityGraph(value = "graph.seduta.dabloccare", type = EntityGraphType.FETCH)
	Set<Seduta> findSeduteDaBloccare(@Param("nowPlusSedutaValidationMinute") LocalDateTime nowPlusSedutaValidationMinute);

	Seduta findFirstByDataAfterOrderByDataAsc(LocalDate date);

	Seduta findFirstByDataAndOraAfterOrderByOraAsc(LocalDate date, LocalTime time);
}
