package it.tredi.ecm.dao.repository;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.entity.Valutazione;

public interface ValutazioneRepository extends CrudRepository<Valutazione, Long>{
	public Valutazione findOne(Long id);
	public Valutazione findOneByAccreditamentoIdAndAccountId(Long accreditamentoId, Long accountId);
	public Set<Valutazione> findAllByAccreditamentoIdOrderByAccount(Long accreditamentoId);
	public Set<Valutazione> findAllByAccreditamentoIdAndDataValutazioneNotNullOrderByAccount(Long accreditamentoId);
	@Query("SELECT COUNT(v.account) FROM Valutazione v WHERE v.accreditamento.id = :id AND v.dataValutazione <> null AND :profileReferee IN ELEMENTS(v.account.profiles)")
	public int countRefereeValutatoriWithDataValutazioneForAccreditamentoId(@Param("id") Long id, @Param("profileReferee") Profile profileReferee);
	@Query("SELECT v.account FROM Valutazione v WHERE v.accreditamento.id = :id AND v.dataValutazione <> null")
	public Set<Account> getAccountValutatoriWithDataValutazioneForAccreditamentoId(@Param("id") Long id);
	@Query("SELECT v.account FROM Valutazione v WHERE v.accreditamento.id = :id AND :profileSegreteria IN ELEMENTS(v.account.profiles)")
	public Account getAccountSegreteriaValutatoreForAccreditamentoId(@Param("id") Long id, @Param("profileSegreteria") Profile profileSegreteria);
	@Query("SELECT v.account FROM Valutazione v WHERE v.accreditamento.id = :id ORDER BY v.account.id")
	public Set<Account> getAllAccountValutatoriForAccreditamentoIdOrderByAccount(@Param("id") Long id);
	public Set<Valutazione> findAllByAccountId(Long accountId);
}
