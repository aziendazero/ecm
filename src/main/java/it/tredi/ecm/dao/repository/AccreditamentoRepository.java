package it.tredi.ecm.dao.repository;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;

public interface AccreditamentoRepository extends CrudRepository<Accreditamento, Long> {
	public Set<Accreditamento> findByProviderId(Long providerId);
	public Set<Accreditamento> findByProviderIdAndTipoDomandaAndDataScadenzaAfter(Long providerId, AccreditamentoTipoEnum tipoDomanda, LocalDate data);
	public Accreditamento findOneByProviderIdAndStatoAndDataFineAccreditamentoAfter(Long providerId, AccreditamentoStatoEnum stato, LocalDate data);
	@Query("SELECT a.datiAccreditamento FROM Accreditamento a WHERE a.id = :accreditamentoId")
	public DatiAccreditamento getDatiAccreditamentoForAccreditamento(@Param("accreditamentoId") Long accreditamentoId);
	
	@Query("SELECT a.provider.id FROM Accreditamento a WHERE a.id = :accreditamentoId")
	public Long getProviderIdById(@Param("accreditamentoId") Long accreditamentoId);
	
	public Set<Accreditamento> findAllByStato(AccreditamentoStatoEnum stato);
}
