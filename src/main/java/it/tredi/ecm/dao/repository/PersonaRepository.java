package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.enumlist.Ruolo;

public interface PersonaRepository extends CrudRepository<Persona, Long> {
	Persona findOneByRuoloAndProviderId(Ruolo ruolo, Long providerId);
	Persona findOneByRuoloAndAnagraficaCodiceFiscaleAndProviderId(Ruolo ruolo, String codiceFiscale, Long providerId);
	Persona findOneByRuoloAndCoordinatoreComitatoScientificoAndProviderId(Ruolo ruolo, boolean coordinatoreComitatoScientifico, Long providerId);
	Set<Persona> findAllByRuoloAndProviderId(Ruolo ruolo, Long providerId);
	
	int countByRuoloAndProviderId(Ruolo ruolo, Long providerId);
	int countByRuoloAndProviderIdAndProfessioneSanitaria(Ruolo ruolo, Long providerId, boolean sanitaria);
	@Query("select count(distinct p.professione) from Persona p where p.ruolo = :ruolo and p.provider.id = :providerId")
	int countDistinctProfessioneByRuoloAndProviderId(@Param("ruolo")Ruolo ruolo, @Param("providerId")Long providerId);
	@Query("select count(distinct p.professione) from Persona p where p.ruolo = :ruolo and p.provider.id = :providerId and p.professione in :professioniSelezionate")
	int countDistinctProfessioneByRuoloAndProviderIdInProfessioniSelezionate(@Param("ruolo")Ruolo ruolo, @Param("providerId") Long providerId,@Param("professioniSelezionate") Set<Professione> professioniSelezionate);
}
