package it.tredi.ecm.dao.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.AnagraficaEvento;

public interface AnagraficaEventoRepository extends CrudRepository<AnagraficaEvento, Long> {
	public Set<AnagraficaEvento> findAllByProviderId(Long providerId);
	public AnagraficaEvento findOneByAnagraficaCodiceFiscaleIgnoreCaseAndProviderId(String codiceFiscale, Long providerId);

	@Query("SELECT a.id, a.anagrafica.cognome, a.anagrafica.nome, a.anagrafica.codiceFiscale FROM AnagraficaEvento a WHERE a.provider.id = :providerId")
	public List<Object[]> findAllByProviderIdJSONVersion(@Param("providerId") Long providerId);
}
