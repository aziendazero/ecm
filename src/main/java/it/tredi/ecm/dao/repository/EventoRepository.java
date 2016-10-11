package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import it.tredi.ecm.dao.entity.Evento;

public interface EventoRepository extends JpaRepository<Evento, Long> {
	public Set<Evento> findAllByProviderId(Long providerId);
}
