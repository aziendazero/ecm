package it.tredi.ecm.dao.repository;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.enumlist.Ruolo;

public interface PersonaRepository extends CrudRepository<Persona, Long> {
	Persona findOneByRuoloAndProviderId(Ruolo ruolo, Long providerId);
}
