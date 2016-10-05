package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.EventoPianoFormativo;

public interface EventoPianoFormativoRepository extends CrudRepository<EventoPianoFormativo, Long> {
	public Set<EventoPianoFormativo> findAll();
	public Set<EventoPianoFormativo> findAllByProviderIdAndPianoFormativo(Long providerId, Integer pianoFormativo);
	public Set<EventoPianoFormativo> findAllByProviderId(Long providerId);
}
