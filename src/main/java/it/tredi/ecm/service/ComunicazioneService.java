package it.tredi.ecm.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Comunicazione;
import it.tredi.ecm.dao.entity.ComunicazioneResponse;

public interface ComunicazioneService {

	Comunicazione getComunicazioneById(Long id);
	int countAllComunicazioniRicevuteByAccountId(Long id);
	int countAllComunicazioniInviateByAccountId(Long id);
	int countAllComunicazioniBloccateByAccountId(Long id);
	Comunicazione getUltimaComunicazioneCreata(Long id);
	long getIdUltimaComunicazioneRicevuta(Long id);
	List<Comunicazione> getUltimi10MessaggiNonLetti(Long id);
	long countAllMessaggiNonLetti(Long id);
	Map<String, Account> getAllDestinatariDisponibili(Long id);
	void send(Comunicazione comunicazione);
	boolean canAccountRespondToComunicazione(Account account, Comunicazione comunicazione);
	void contrassegnaComeLetta(Long id);
	void reply(ComunicazioneResponse risposta, Long id);
	Set<Comunicazione> getAllComunicazioniRicevute(Account user);
	Set<Comunicazione> getAllComunicazioniInviate(Account user);
	Set<Comunicazione> getAllComunicazioniChiuse(Account user);

}
