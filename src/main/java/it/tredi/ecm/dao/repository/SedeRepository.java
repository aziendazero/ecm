package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import it.tredi.ecm.dao.entity.Sede;

public interface SedeRepository extends JpaRepository<Sede, Long> {
	Set<Sede> findAllByProviderId(Long providerId);
}
