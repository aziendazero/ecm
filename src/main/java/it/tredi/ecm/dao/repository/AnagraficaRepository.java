package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Anagrafica;

public interface AnagraficaRepository extends CrudRepository<Anagrafica, Long> {
	Set<Anagrafica> findAllByProviderId(Long providerId);
}
