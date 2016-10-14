package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.AnagraficaEvento;

public interface AnagraficaEventoRepository extends CrudRepository<AnagraficaEvento, Long> {
	public Set<AnagraficaEvento> findAllByProviderId(Long providerId);
}
