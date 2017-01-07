package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Provider;

public interface PianoFormativoRepository extends CrudRepository<PianoFormativo, Long> {
	public Set<PianoFormativo> findAllByProviderId(Long providerId);
	public PianoFormativo findOneByProviderIdAndAnnoPianoFormativo(Long providerId, Integer annoPianoFormativo);
	@Query("SELECT a.pianoFormativo.id FROM Accreditamento a WHERE a.provider.id = :providerId")
	public Set<Long> findAllByProviderIdInAccreditamento(@Param("providerId") Long providerId);

	@Query("SELECT p.provider FROM PianoFormativo p WHERE p.provider NOT IN (SELECT pp.provider FROM PianoFormativo pp WHERE pp.annoPianoFormativo = :annoPianoFormativo) AND (p.provider.status = 'ACCREDITATO_PROVVISORIAMENTE' OR p.provider.status = 'ACCREDITATO_STANDARD')")
	public Set<Provider> findAllProviderNotPianoFormativoInseritoPerAnno(@Param("annoPianoFormativo") Integer annoPianoFormativo);
}
