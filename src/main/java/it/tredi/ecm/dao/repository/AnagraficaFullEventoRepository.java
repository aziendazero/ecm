package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.AnagraficaFullEvento;

public interface AnagraficaFullEventoRepository extends CrudRepository<AnagraficaFullEvento, Long> {
	public Set<AnagraficaFullEvento> findAllByProviderId(Long providerId);
	public AnagraficaFullEvento findOneByAnagraficaCodiceFiscaleAndProviderId(String codiceFiscale, Long providerId);
}
