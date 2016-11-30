package it.tredi.ecm.dao.repository;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.ValutazioneTipoEnum;

public interface ValutazioneRepository extends JpaRepository<Valutazione, Long>{
	public Valutazione findOne(Long id);
	public Valutazione findOneByAccreditamentoIdAndAccountIdAndStoricizzatoFalse(Long accreditamentoId, Long accountId);
	public Set<Valutazione> findAllByAccreditamentoIdAndStoricizzatoFalseOrderByAccount(Long accreditamentoId);
	public Set<Valutazione> findAllByAccreditamentoIdAndStoricizzatoFalseAndDataValutazioneNotNullOrderByAccount(Long accreditamentoId);
	@Query("SELECT COUNT(v.account) FROM Valutazione v WHERE v.accreditamento.id = :id AND v.dataValutazione <> null AND v.storicizzato = false AND :profileReferee IN ELEMENTS(v.account.profiles)")
	public int countRefereeValutatoriWithDataValutazioneForAccreditamentoId(@Param("id") Long id, @Param("profileReferee") Profile profileReferee);
	@Query("SELECT v.account FROM Valutazione v WHERE v.accreditamento.id = :id AND v.dataValutazione <> null AND v.storicizzato = false")
	public Set<Account> getAccountValutatoriWithDataValutazioneForAccreditamentoId(@Param("id") Long id);
	@Query("SELECT v.account FROM Valutazione v WHERE v.accreditamento.id = :id AND v.storicizzato = false AND :profileSegreteria IN ELEMENTS(v.account.profiles)")
	public Account getAccountSegreteriaValutatoreForAccreditamentoId(@Param("id") Long id, @Param("profileSegreteria") Profile profileSegreteria);
	@Query("SELECT v.account FROM Valutazione v WHERE v.accreditamento.id = :id AND v.storicizzato = false ORDER BY v.account.id")
	public Set<Account> getAllAccountValutatoriForAccreditamentoIdOrderByAccount(@Param("id") Long id);
	public Set<Valutazione> findAllByAccountId(Long accountId);
	public Valutazione findOneByAccreditamentoIdAndTipoValutazioneAndStoricizzatoFalse(Long accreditamentoId, ValutazioneTipoEnum segreteriaEcm);
}
