package it.tredi.ecm.dao.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.PersonaEvento;

@JaversSpringDataAuditable
public interface PersonaEventoRepository extends CrudRepository<PersonaEvento, Long> {
}
