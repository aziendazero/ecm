package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Comunicazione;

public interface ComunicazioneRepository  extends CrudRepository<Comunicazione, Long> {

	@Query ("SELECT COUNT (c) FROM Comunicazione c WHERE :user IN ELEMENTS(c.destinatari)")
	int countAllComunicazioniReceivedByAccount(@Param("user") Account user);

	int countAllComunicazioniByMittente(Account user);

	@Query ("SELECT COUNT (c) FROM Comunicazione c WHERE (:user IN ELEMENTS(c.destinatari) OR :user = c.mittente) AND c.chiusa = true")
	int countAllComunicazioniChiuseForUser(@Param("user") Account user);

	Comunicazione findFirstByMittenteOrderByDataCreazioneDesc(Account user);

	@Query("SELECT c FROM Comunicazione c WHERE :userId IN ELEMENTS(c.utentiCheDevonoLeggere) ORDER BY c.dataUltimaModifica DESC")
	Page<Comunicazione> findMessaggiNonLettiOrderByDataCreazioneDesc(@Param ("userId") Long userId, Pageable pageable);

	@Query("SELECT COUNT (c) FROM Comunicazione c WHERE :userId IN ELEMENTS(c.utentiCheDevonoLeggere)")
	long countAllMessaggiNonLetti(@Param ("userId") Long userId);

	@Query ("SELECT c FROM Comunicazione c WHERE :user IN ELEMENTS(c.destinatari)")
	Set<Comunicazione> findAllComunicazioniByDestinatario(@Param("user") Account user);

	Set<Comunicazione> findAllComunicazioneByMittente(Account user);

	@Query ("SELECT c FROM Comunicazione c WHERE (:user IN ELEMENTS(c.destinatari) OR :user = c.mittente) AND c.chiusa = true")
	Set<Comunicazione> findAllComunicazioneChiusaByUser(@Param("user") Account user);

}
