package it.tredi.ecm.dao.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.AnagraficaFullEvento;

public interface AnagraficaFullEventoRepository extends CrudRepository<AnagraficaFullEvento, Long> {
	public Set<AnagraficaFullEvento> findAllByProviderId(Long providerId);
	@Query("SELECT a.id, a.anagrafica.cognome, a.anagrafica.nome, a.anagrafica.codiceFiscale FROM AnagraficaFullEvento a WHERE a.provider.id = :providerId")
	public List<Object[]> findAllByProviderIdJSONVersion(@Param("providerId") Long providerId);
	public AnagraficaFullEvento findOneByAnagraficaCodiceFiscaleAndProviderId(String codiceFiscale, Long providerId);
}
