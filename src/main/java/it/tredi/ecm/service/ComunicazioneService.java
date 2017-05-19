package it.tredi.ecm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.validation.BindingResult;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Comunicazione;
import it.tredi.ecm.dao.entity.ComunicazioneResponse;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.web.bean.ComunicazioneWrapper;
import it.tredi.ecm.web.bean.RicercaComunicazioneWrapper;

public interface ComunicazioneService {

	Comunicazione getComunicazioneById(Long id);
	int countAllComunicazioniRicevuteByAccountId(Long id);
	int countAllComunicazioniInviateByAccountId(Long id);
	int countAllComunicazioniChiuseByAccountId(Long id);
	int countAllComunicazioniNonRisposteByAccountId(Long id);
	long getIdUltimaComunicazioneRicevuta(Long id);
	List<Comunicazione> getUltimi10MessaggiNonLetti(Long id);
	long countAllMessaggiNonLetti(Long id);
	Map<String, Set<Account>> getAllDestinatariDisponibili(Long id);
	void send(Comunicazione comunicazione, File allegato) throws Exception;
	boolean canAccountRespondToComunicazione(Account account, Comunicazione comunicazione);
	boolean canAccountSeeResponse(Account account, ComunicazioneResponse response);
	boolean canAccountCloseComunicazione(Account account, Comunicazione comunicazione);
	void contrassegnaComeLetta(Long id);
	void reply(ComunicazioneResponse risposta, Long id, File allegato);
	Set<Comunicazione> getAllComunicazioniRicevuteByAccount(Account user);
	Set<Comunicazione> getAllComunicazioniInviateByAccount(Account user);
	Set<Comunicazione> getAllComunicazioniChiuseByAccount(Account user);
	Set<Comunicazione> getAllComunicazioniNonRisposteByAccount(Account account);
	Set<Comunicazione> getAllComunicazioniNonLetteByAccount(Account user);
	void chiudiComunicazioneById(Long id);
	int countAllComunicazioniStoricoByAccountId(Long currentAccountId);
	Set<Comunicazione> getAllComunicazioniByAccount(Account user);
	Set<Comunicazione> getAllComunicazioniByProvider(Provider provider);
	Set<Comunicazione> getAllComunicazioniNonRisposteFromProviderBySegreteria(Provider provider);
	List<Comunicazione> cerca(RicercaComunicazioneWrapper wrapper);
	HashMap<Long, Boolean> createMappaVisibilitaResponse(Account account, Comunicazione comunicazione);
	void archiviaSelezionati(Set<Long> ids);

}
