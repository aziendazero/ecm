package it.tredi.ecm.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Comunicazione;
import it.tredi.ecm.dao.entity.ComunicazioneResponse;
import it.tredi.ecm.dao.entity.File;

public interface ComunicazioneService {

	Comunicazione getComunicazioneById(Long id);
	int countAllComunicazioniRicevuteByAccountId(Long id);
	int countAllComunicazioniInviateByAccountId(Long id);
	int countAllComunicazioniBloccateByAccountId(Long id);
	Comunicazione getUltimaComunicazioneCreata(Long id);
	long getIdUltimaComunicazioneRicevuta(Long id);
	List<Comunicazione> getUltimi10MessaggiNonLetti(Long id);
	long countAllMessaggiNonLetti(Long id);
	Map<String, Set<Account>> getAllDestinatariDisponibili(Long id);
	void send(Comunicazione comunicazione, File allegato);
	boolean canAccountRespondToComunicazione(Account account, Comunicazione comunicazione);
	boolean canAccountCloseComunicazione(Account account, Comunicazione comunicazione);
	void contrassegnaComeLetta(Long id);
	void reply(ComunicazioneResponse risposta, Long id, File allegato);
	Set<Comunicazione> getAllComunicazioniRicevuteByAccount(Account user);
	Set<Comunicazione> getAllComunicazioniInviateByAccount(Account user);
	Set<Comunicazione> getAllComunicazioniChiuseByAccount(Account user);
	Set<Comunicazione> getAllComunicazioniNonLetteByAccount(Account user);
	void chiudiComunicazioneById(Long id);
	int countAllComunicazioniByAccountId(Long currentAccountId);
	Set<Comunicazione> getAllComunicazioniByAccount(Account user);

}
