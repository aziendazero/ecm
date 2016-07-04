package it.tredi.ecm.dao.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Anagrafica;

public interface AnagraficaRepository extends CrudRepository<Anagrafica, Long> {
	public Set<Anagrafica> findAllByProviderId(Long providerId);
	@Query("SELECT a.id FROM Anagrafica a WHERE a.codiceFiscale = :codiceFiscale AND a.provider.id = :providerId")
	public Optional<Long> findOneByCodiceFiscaleAndProviderId(@Param("codiceFiscale") String codiceFiscale, @Param("providerId") Long providerId);  
}
