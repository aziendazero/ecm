package it.tredi.ecm.dao.repository;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Persona;

public interface PersonaRepository extends CrudRepository<Persona, Long> {
	Persona findOneByRuoloAndProviderId(String ruolo, Long providerId);
}
