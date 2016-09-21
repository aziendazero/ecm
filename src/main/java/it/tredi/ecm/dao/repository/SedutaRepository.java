package it.tredi.ecm.dao.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Seduta;

public interface SedutaRepository extends CrudRepository<Seduta, Long>{
	Set<Seduta> findAll();

	@Query("SELECT s FROM Seduta s WHERE (s.data = :date AND s.ora > :time) OR (s.data > :date)")
	Set<Seduta> findAllByDataAndOraAceptable(@Param("date") LocalDate date, @Param("time") LocalTime time);

	Seduta findFirstByDataAfterOrderByDataAsc(LocalDate date);

	Seduta findFirstByDataAndOraAfterOrderByOraAsc(LocalDate date, LocalTime time);
}
