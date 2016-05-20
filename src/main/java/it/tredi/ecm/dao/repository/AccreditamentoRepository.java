package it.tredi.ecm.dao.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Accreditamento;

public interface AccreditamentoRepository extends CrudRepository<Accreditamento, Long> {
	public Set<Accreditamento> findByProviderId(Long providerId);
	public Set<Accreditamento> findByProviderIdAndTipoDomandaAndDataScadenzaAfter(Long providerId, String tipoDomanda, LocalDate data);
}
