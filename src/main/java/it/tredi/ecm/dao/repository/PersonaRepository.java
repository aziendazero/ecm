package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.enumlist.Ruolo;

public interface PersonaRepository extends CrudRepository<Persona, Long> {
	Persona findOneByRuoloAndProviderId(Ruolo ruolo, Long providerId);
	Persona findOneByRuoloAndAnagraficaCodiceFiscaleAndProviderId(Ruolo ruolo, String codiceFiscale, Long providerId);
	Persona findOneByRuoloAndCoordinatoreComitatoScientificoAndProviderId(Ruolo ruolo, boolean coordinatoreComitatoScientifico, Long providerId);
	Set<Persona> findAllByRuoloAndProviderId(Ruolo ruolo, Long providerId);
}
