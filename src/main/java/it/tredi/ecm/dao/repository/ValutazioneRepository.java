package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.entity.Valutazione;

public interface ValutazioneRepository extends CrudRepository<Valutazione, Long>{
	public Valutazione findOne(Long id);
	public Valutazione findOneByAccreditamentoIdAndAccountIdAndDataValutazioneNull(Long accreditamentoId, Long accountId);
	public Set<Valutazione> findAllByAccreditamentoIdOrderByDataValutazioneAsc(Long accreditamentoId);
	public Set<Valutazione> findAllByAccreditamentoIdAndDataValutazioneNotNullOrderByDataValutazioneAsc(Long accreditamentoId);
	@Query("SELECT COUNT(v.account) FROM Valutazione v WHERE v.accreditamento.id = :id AND v.dataValutazione = null")
	public int countRefereeValutatoriWithNoDataValutazioneForAccreditamentoId(@Param("id") Long id);
	@Query("SELECT v.account FROM Valutazione v WHERE v.accreditamento.id = :id AND v.dataValutazione <> null")
	public Set<Account> getAccountValutatoriWithDataValutazioneForAccreditamentoId(@Param("id") Long id);
	@Query("SELECT v.account FROM Valutazione v WHERE v.accreditamento.id = :id AND :profileSegreteria IN ELEMENTS(v.account.profiles)")
	public Account getAccountSegreteriaValutatoreForAccreditamentoId(@Param("id") Long id, @Param("profileSegreteria") Profile profileSegreteria);
}
