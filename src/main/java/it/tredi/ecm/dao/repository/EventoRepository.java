package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Evento;

public interface EventoRepository extends CrudRepository<Evento, Long> {
	public Set<Evento> findAll();
	public Set<Evento> findAllByProviderIdAndPianoFormativo(Long providerId, Integer pianoFormativo);
	public Set<Evento> findAllByProviderId(Long providerId);
}
