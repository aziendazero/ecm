package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.PianoFormativo;

public interface PianoFormativoRepository extends CrudRepository<PianoFormativo, Long> {
	public Set<PianoFormativo> findAllByProviderId(Long providerId);
	public PianoFormativo findOneByProviderIdAndAnnoPianoFormativo(Long providerId, Integer annoPianoFormativo);
	@Query("SELECT a.pianoFormativo.id FROM Accreditamento a WHERE a.provider.id = :providerId")
	public Set<Long> findAllByProviderIdInAccreditamento(@Param("providerId") Long providerId);
}
