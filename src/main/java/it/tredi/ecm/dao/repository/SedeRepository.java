package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.Ruolo;

public interface SedeRepository extends CrudRepository<Sede, Long> {
	Set<Sede> findAllByProviderId(Long providerId);
}
