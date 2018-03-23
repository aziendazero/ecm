package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Comunicazione;

public interface ComunicazioneRepository  extends CrudRepository<Comunicazione, Long> {

	@Query ("SELECT DISTINCT COUNT (c) FROM Comunicazione c WHERE :user MEMBER OF c.destinatari")
	int countAllComunicazioniRicevuteByAccount(@Param("user") Account user);

	@Query ("SELECT DISTINCT COUNT (c) FROM Comunicazione c WHERE :user MEMBER OF c.destinatari OR :segreteria MEMBER OF c.destinatari")
	int countAllComunicazioniRicevuteByAccountOrBySegreteria(@Param("user") Account user, @Param("segreteria") Account segreteriaComunicazioni);

	@Query ("SELECT DISTINCT COUNT (c) FROM Comunicazione c WHERE c.fakeAccountComunicazioni = :accountComunicazioni")
	int countAllComunicazioniInviateForAccount(@Param("accountComunicazioni") Account accountComunicazioni);

	@Query ("SELECT DISTINCT COUNT (c) FROM Comunicazione c WHERE (:user MEMBER OF c.destinatari OR :user = c.mittente) AND c.chiusa = true")
	int countAllComunicazioniChiuseByAccount(@Param("user") Account user);

	@Query ("SELECT DISTINCT COUNT (c) FROM Comunicazione c WHERE (:accountComunicazioni MEMBER OF c.destinatari OR c.fakeAccountComunicazioni = :accountComunicazioni) AND c.chiusa = true")
	int countAllComunicazioniChiuseByGroup(@Param("accountComunicazioni") Account accountComunicazioni);

	@Query ("SELECT DISTINCT COUNT (c) FROM Comunicazione c WHERE (:user MEMBER OF c.destinatari OR :user = c.mittente OR :accountComunicazioni MEMBER OF c.destinatari OR c.fakeAccountComunicazioni = :accountComunicazioni) AND c.chiusa = true")
	int countAllComunicazioniChiuseByAccountOrByGroup(@Param("user") Account user, @Param("accountComunicazioni") Account accountComunicazioni);

	@Query ("SELECT DISTINCT COUNT (c) FROM Comunicazione c WHERE :user MEMBER OF c.destinatari OR :user = c.mittente")
	int countAllComunicazioniStoricoByAccount(@Param("user") Account user);

	@Query ("SELECT DISTINCT COUNT (c) FROM Comunicazione c WHERE :accountComunicazioni MEMBER OF c.destinatari OR c.fakeAccountComunicazioni = :accountComunicazioni")
	int countAllComunicazioniStoricoByGroup(@Param("accountComunicazioni") Account accountComunicazioni);

	@Query ("SELECT DISTINCT COUNT (c) FROM Comunicazione c WHERE :user MEMBER OF c.destinatari OR :user = c.mittente OR :accountComunicazioni MEMBER OF c.destinatari OR c.fakeAccountComunicazioni = :accountComunicazioni")
	int countAllComunicazioniStoricoByAccountOrByGroup(@Param("user") Account user, @Param("accountComunicazioni") Account accountComunicazioni);

	@Query ("SELECT DISTINCT COUNT (c) FROM Comunicazione c WHERE c.chiusa = FALSE AND :user MEMBER OF c.destinatari AND NOT EXISTS (SELECT cr FROM ComunicazioneResponse cr WHERE cr.comunicazione = c AND cr.mittente = :user)")
	int countAllComunicazioniNonRisposteByAccount(@Param("user") Account user);

	@Query ("SELECT DISTINCT COUNT (c) FROM Comunicazione c WHERE c.chiusa = FALSE AND :accountComunicazioni MEMBER OF c.destinatari AND NOT EXISTS (SELECT cr FROM ComunicazioneResponse cr WHERE cr.comunicazione = c AND cr.fakeAccountComunicazioni = :accountComunicazioni)")
	int countAllComunicazioniNonRisposteByGroup(@Param("accountComunicazioni") Account accountComunicazioni);

	@Query ("SELECT DISTINCT COUNT (c) FROM Comunicazione c WHERE c.chiusa = FALSE AND (:user MEMBER OF c.destinatari OR :accountComunicazioni MEMBER OF c.destinatari) AND NOT EXISTS (SELECT cr FROM ComunicazioneResponse cr WHERE cr.comunicazione = c AND (cr.mittente = :user OR cr.fakeAccountComunicazioni = :accountComunicazioni))")
	int countAllComunicazioniNonRisposteByAccountOrByGroup(@Param("user") Account user, @Param("accountComunicazioni") Account accountComunicazioni);


	@Query ("SELECT DISTINCT COUNT (c) FROM Comunicazione c WHERE c.chiusa = FALSE AND :user MEMBER OF c.destinatari AND EXISTS (SELECT cr FROM ComunicazioneResponse cr WHERE cr.comunicazione = c AND cr.mittente = :user)")
	int countAllComunicazioniAperteByAccount(@Param("user") Account user);

	@Query ("SELECT DISTINCT COUNT (c) FROM Comunicazione c WHERE c.chiusa = FALSE AND :accountComunicazioni MEMBER OF c.destinatari AND EXISTS (SELECT cr FROM ComunicazioneResponse cr WHERE cr.comunicazione = c AND cr.fakeAccountComunicazioni = :accountComunicazioni)")
	int countAllComunicazioniAperteByGroup(@Param("accountComunicazioni") Account accountComunicazioni);

	@Query ("SELECT DISTINCT COUNT (c) FROM Comunicazione c WHERE c.chiusa = FALSE AND (:user MEMBER OF c.destinatari OR :accountComunicazioni MEMBER OF c.destinatari) AND EXISTS (SELECT cr FROM ComunicazioneResponse cr WHERE cr.comunicazione = c AND (cr.mittente = :user OR cr.fakeAccountComunicazioni = :accountComunicazioni))")
	int countAllComunicazioniAperteByAccountOrByGroup(@Param("user") Account user, @Param("accountComunicazioni") Account accountComunicazioni);


	@Query ("SELECT c FROM Comunicazione c WHERE :user MEMBER OF c.destinatari")
	Set<Comunicazione> findAllComunicazioniRicevuteByAccount(@Param("user") Account user);

	@Query ("SELECT c FROM Comunicazione c WHERE :user MEMBER OF c.destinatari OR :segreteria MEMBER OF c.destinatari")
	Set<Comunicazione> findAllComunicazioniRicevuteByAccountOrBySegreteria(@Param("user") Account user, @Param("segreteria") Account segreteriaComunicazioni);

	@Query ("SELECT c FROM Comunicazione c WHERE c.fakeAccountComunicazioni = :accountComunicazioni")
	Set<Comunicazione> findAllComunicazioniInviateForAccount(@Param("accountComunicazioni") Account accountComunicazioni);

	@Query ("SELECT c FROM Comunicazione c WHERE (:user MEMBER OF c.destinatari OR :user = c.mittente OR :segreteria MEMBER OF c.destinatari OR c.inviatoAllaSegreteria = false) AND c.chiusa = true")
	Set<Comunicazione> findAllComunicazioniChiuseByAccountOrBySegreteria(@Param("user") Account user, @Param("segreteria") Account segreteriaComunicazioni);

	@Query ("SELECT c FROM Comunicazione c WHERE (:user MEMBER OF c.destinatari OR :user = c.mittente) AND c.chiusa = true")
	Set<Comunicazione> findAllComunicazioniChiuseByAccount(@Param("user") Account user);

	@Query ("SELECT c FROM Comunicazione c WHERE (:accountComunicazioni MEMBER OF c.destinatari OR c.fakeAccountComunicazioni = :accountComunicazioni) AND c.chiusa = true")
	Set<Comunicazione> findAllComunicazioniChiuseByGroup(@Param("accountComunicazioni") Account accountComunicazioni);

	@Query ("SELECT c FROM Comunicazione c WHERE (:user MEMBER OF c.destinatari OR :user = c.mittente OR :accountComunicazioni MEMBER OF c.destinatari OR c.fakeAccountComunicazioni = :accountComunicazioni) AND c.chiusa = true")
	Set<Comunicazione> findAllComunicazioniChiuseByAccountOrByGroup(@Param("user") Account user, @Param("accountComunicazioni") Account segreteriaComunicazioni);

	@Query ("SELECT c FROM Comunicazione c WHERE :user MEMBER OF c.destinatari OR :user = c.mittente")
	Set<Comunicazione> findAllComunicazioniStoricoByAccount(@Param("user") Account user);

	@Query ("SELECT c FROM Comunicazione c WHERE :accountComunicazioni MEMBER OF c.destinatari OR c.fakeAccountComunicazioni = :accountComunicazioni")
	Set<Comunicazione> findAllComunicazioniStoricoByGroup(@Param("accountComunicazioni") Account accountComunicazioni);

	@Query ("SELECT c FROM Comunicazione c WHERE :user MEMBER OF c.destinatari OR :user = c.mittente OR :accountComunicazioni MEMBER OF c.destinatari OR c.fakeAccountComunicazioni = :accountComunicazioni")
	Set<Comunicazione> findAllComunicazioniStoricoByAccountOrByGroup(@Param("user") Account user, @Param("accountComunicazioni") Account segreteriaComunicazioni);

	@Query ("SELECT c FROM Comunicazione c WHERE :user MEMBER OF c.destinatari AND NOT EXISTS (SELECT cr FROM ComunicazioneResponse cr WHERE cr.comunicazione = c AND cr.mittente = :user)")
	Set<Comunicazione> findAllComunicazioniNonRisposteByAccount(@Param("user") Account user);

	@Query ("SELECT c FROM Comunicazione c WHERE c.chiusa = FALSE AND :accountComunicazioni MEMBER OF c.destinatari AND NOT EXISTS (SELECT cr FROM ComunicazioneResponse cr WHERE cr.comunicazione = c AND cr.fakeAccountComunicazioni = :accountComunicazioni)")
	Set<Comunicazione> findAllComunicazioniNonRisposteByGroup(@Param("accountComunicazioni") Account accountComunicazioni);

	@Query ("SELECT c FROM Comunicazione c WHERE c.chiusa = FALSE AND (:user MEMBER OF c.destinatari OR :accountComunicazioni MEMBER OF c.destinatari) AND NOT EXISTS (SELECT cr FROM ComunicazioneResponse cr WHERE cr.comunicazione = c AND (cr.mittente = :user OR cr.fakeAccountComunicazioni = :accountComunicazioni))")
	Set<Comunicazione> findAllComunicazioniNonRisposteByAccountOrByGroup(@Param("user") Account user, @Param("accountComunicazioni") Account accountComunicazioni);

	@Query ("SELECT c FROM Comunicazione c WHERE :user MEMBER OF c.destinatari AND EXISTS (SELECT cr FROM ComunicazioneResponse cr WHERE cr.comunicazione = c AND cr.mittente = :user)")
	Set<Comunicazione> findAllComunicazioniAperteByAccount(@Param("user") Account user);

	@Query ("SELECT c FROM Comunicazione c WHERE c.chiusa = FALSE AND :accountComunicazioni MEMBER OF c.destinatari AND EXISTS (SELECT cr FROM ComunicazioneResponse cr WHERE cr.comunicazione = c AND cr.fakeAccountComunicazioni = :accountComunicazioni)")
	Set<Comunicazione> findAllComunicazioniAperteByGroup(@Param("accountComunicazioni") Account accountComunicazioni);

	@Query ("SELECT c FROM Comunicazione c WHERE c.chiusa = FALSE AND (:user MEMBER OF c.destinatari OR :accountComunicazioni MEMBER OF c.destinatari) AND EXISTS (SELECT cr FROM ComunicazioneResponse cr WHERE cr.comunicazione = c AND (cr.mittente = :user OR cr.fakeAccountComunicazioni = :accountComunicazioni))")
	Set<Comunicazione> findAllComunicazioniAperteByAccountOrByGroup(@Param("user") Account user, @Param("accountComunicazioni") Account accountComunicazioni);

	@Query ("SELECT c FROM Comunicazione c WHERE c.chiusa = FALSE AND :accountComunicazioniFrom = c.fakeAccountComunicazioni AND NOT EXISTS (SELECT cr FROM ComunicazioneResponse cr WHERE cr.comunicazione = c AND cr.fakeAccountComunicazioni = :accountComunicazioniTo)")
	Set<Comunicazione> findAllComunicazioniNonRisposteFromGroupByGroup(@Param("accountComunicazioniFrom") Account accountComunicazioniFrom, @Param("accountComunicazioniTo") Account accountComunicazioniTo);

	@Query("SELECT COUNT (c) FROM Comunicazione c WHERE :userId MEMBER OF c.utentiCheDevonoLeggere")
	int countAllMessaggiNonLetti(@Param ("userId") Long userId);

	//pageable
	@Query("SELECT c FROM Comunicazione c WHERE :userId MEMBER OF c.utentiCheDevonoLeggere ORDER BY c.dataUltimaModifica DESC")
	Page<Comunicazione> findMessaggiNonLettiOrderByDataUltimaModificaDesc(@Param ("userId") Long userId, Pageable pageable);

	//non pageble
	@Query("SELECT c FROM Comunicazione c WHERE :userId MEMBER OF c.utentiCheDevonoLeggere ORDER BY c.dataUltimaModifica DESC")
	Set<Comunicazione> findAllComunicazioneNonLetteOrderByDataUltimaModificaDesc(@Param("userId") Long userId);

}