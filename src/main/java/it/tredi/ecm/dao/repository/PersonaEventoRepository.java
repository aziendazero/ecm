package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.enumlist.RuoloPersonaEventoEnum;

public interface PersonaEventoRepository extends CrudRepository<PersonaEvento, Long> {

	Set<PersonaEvento> findAllByRuolo(RuoloPersonaEventoEnum docente);
}
