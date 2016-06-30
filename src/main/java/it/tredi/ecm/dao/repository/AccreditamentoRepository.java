package it.tredi.ecm.dao.repository;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.enumlist.AccreditamentoEnum;

public interface AccreditamentoRepository extends CrudRepository<Accreditamento, Long> {
	public Set<Accreditamento> findByProviderId(Long providerId);
	public Set<Accreditamento> findByProviderIdAndTipoDomandaAndDataScadenzaAfter(Long providerId, AccreditamentoEnum tipoDomanda, LocalDate data);
	public Accreditamento findOneByProviderIdAndStatoAndDataScadenzaAfter(Long providerId, AccreditamentoEnum stato, LocalDate data);
}
