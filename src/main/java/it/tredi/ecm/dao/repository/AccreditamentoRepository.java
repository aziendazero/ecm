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
	public Set<Accreditamento> findAllByProviderIdAndTipoDomanda(Long providerId, AccreditamentoTipoEnum tipoDomanda);
	public Set<Accreditamento> findByProviderIdAndTipoDomandaAndDataScadenzaAfter(Long providerId, AccreditamentoTipoEnum tipoDomanda, LocalDate data);
	public Accreditamento findOneByProviderIdAndStatoAndDataFineAccreditamentoAfter(Long providerId, AccreditamentoStatoEnum stato, LocalDate data);
	@Query("SELECT a.stato FROM Accreditamento a WHERE a.id = :accreditamentoId")
	public AccreditamentoStatoEnum getStatoByAccreditamentoId(@Param("accreditamentoId") Long accreditamentoId);
	@Query("SELECT a.datiAccreditamento FROM Accreditamento a WHERE a.id = :accreditamentoId")
	public DatiAccreditamento getDatiAccreditamentoForAccreditamento(@Param("accreditamentoId") Long accreditamentoId);

	@Query("SELECT a.provider.id FROM Accreditamento a WHERE a.id = :accreditamentoId")
	public Long getProviderIdById(@Param("accreditamentoId") Long accreditamentoId);

	public int countAllByStato(AccreditamentoStatoEnum stato);
	public Set<Accreditamento> findAllByStato(AccreditamentoStatoEnum stato);
	public int countAllByStatoAndProviderId(AccreditamentoStatoEnum stato, Long providerId);
	public Set<Accreditamento> findAllByStatoAndProviderId(AccreditamentoStatoEnum stato, Long providerId);
	@Query("SELECT v.accreditamento FROM Valutazione v WHERE v.account.id = :id")
	public Set<Accreditamento> findAllAccreditamentoInValutazioneAssignedToAccountId(@Param("id") Long id);
	@Query("SELECT COUNT(v.accreditamento) FROM Valutazione v WHERE v.account.id = :id")
	public int countAllAccreditamentoInValutazioneAssignedToAccountId(@Param("id") Long id);
}
