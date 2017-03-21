package it.tredi.ecm.dao.repository;

import java.math.BigInteger;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.enumlist.RuoloPersonaEventoEnum;

@JaversSpringDataAuditable
public interface PersonaEventoRepository extends CrudRepository<PersonaEvento, Long> {

	Set<PersonaEvento> findAllByRuolo(RuoloPersonaEventoEnum docente);

	@Query(value = "SELECT pe.docente_id FROM ecmdb.persona_evento pe WHERE UPPER(pe.nome) = :nome AND UPPER(pe.cognome) = :cognome AND pe.docente_id IS NOT NULL", nativeQuery = true)
	Set<BigInteger> findAllEventoIdByNomeAndCognome(@Param("nome") String nome, @Param("cognome") String cognome);
}
