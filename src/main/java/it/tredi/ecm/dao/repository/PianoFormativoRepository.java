package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import it.tredi.ecm.dao.entity.PianoFormativo;

public interface PianoFormativoRepository extends CrudRepository<PianoFormativo, Long> {
	public Set<PianoFormativo> findAllByProviderId(Long providerId);
	public PianoFormativo findOneByProviderIdAndAnnoPianoFormativo(Long providerId, Integer annoPianoFormativo);
}
