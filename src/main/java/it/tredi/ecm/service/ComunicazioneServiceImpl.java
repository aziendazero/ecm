package it.tredi.ecm.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Comunicazione;
import it.tredi.ecm.dao.entity.ComunicazioneResponse;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.dao.repository.ComunicazioneRepository;
import it.tredi.ecm.dao.repository.ComunicazioneResponseRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class ComunicazioneServiceImpl implements ComunicazioneService {
	private static Logger LOGGER = LoggerFactory.getLogger(ComunicazioneServiceImpl.class);

	@Autowired private AccountService accountService;
	@Autowired private ProviderService providerService;
	@Autowired private ComunicazioneRepository comunicazioneRepository;
	@Autowired private ComunicazioneResponseRepository comunicazioneResponseRepository;

	@Override
	public Comunicazione getComunicazioneById(Long id) {
		return comunicazioneRepository.findOne(id);
	}

	@Override
	public int countAllComunicazioniRicevuteByAccountId(Long id) {
		Account user = accountService.getUserById(id);
		return comunicazioneRepository.countAllComunicazioniReceivedByAccount(user);
	}

	@Override
	public int countAllComunicazioniInviateByAccountId(Long id) {
		Account user = accountService.getUserById(id);
		return comunicazioneRepository.countAllComunicazioniByMittente(user);
	}

	@Override
	public int countAllComunicazioniBloccateByAccountId(Long id) {
		Account user = accountService.getUserById(id);
		return comunicazioneRepository.countAllComunicazioniChiuseForUser(user);
	}

	@Override
	public Comunicazione getUltimaComunicazioneCreata(Long id) {
		Account user = accountService.getUserById(id);
		return comunicazioneRepository.findFirstByMittenteOrderByDataCreazioneDesc(user);
	}

	@Override
	public List<Comunicazione> getUltimi10MessaggiNonLetti(Long id) {
		Page<Comunicazione> results = comunicazioneRepository.findMessaggiNonLettiOrderByDataCreazioneDesc(id, new PageRequest(0, 10));
		List<Comunicazione> ultimi10MessaggiNonLetti= results.getContent();
		return ultimi10MessaggiNonLetti;
	}

	@Override
	public long countAllMessaggiNonLetti(Long id) {
		return comunicazioneRepository.countAllMessaggiNonLetti(id);
	}

	@Override
	public long getIdUltimaComunicazioneRicevuta(Long id) {
		Page<Comunicazione> results = comunicazioneRepository.findMessaggiNonLettiOrderByDataCreazioneDesc(id, new PageRequest(0, 1));
		List<Comunicazione> ultimoMessaggioNonLetto = results.getContent();
		if (!ultimoMessaggioNonLetto.isEmpty())
			return ultimoMessaggioNonLetto.get(0).getId();
		else return 0;
	}

	//controlla l'utente se segreteria o provider e restituisce la lista di tutti i possibili destinatari, mappati
	//per l'esigenza di avere il nome del provider di riferimento (caso segreteria)
	@Override
	public Map<String, Account> getAllDestinatariDisponibili(Long id) {
		Map<String, Account> destinatariMap = new HashMap<String, Account>();
		Account richiedente = accountService.getUserById(id);
		if(richiedente.isSegreteria()) {
			Set<Provider> listaProvider = providerService.getAll();
			for (Provider p : listaProvider) {
				destinatariMap.put(p.getDenominazioneLegale(), p.getAccount());
			}
		}
		else {
			Set<Account> listaSegretari = accountService.getUserByProfileEnum(ProfileEnum.SEGRETERIA);
			int index = 1;
			for (Account a : listaSegretari) {
				destinatariMap.put("segretario" + index, a);
				index++;
			}
		}
		return destinatariMap;
	}

	//salvataggio comunicazione, controllo mittente, se è provider aggiungo i segretari ai destinatari
	//aggiungo anche la data di invio
	@Override
	public void send(Comunicazione comunicazione) {
		if(comunicazione.getMittente().isProvider()) {
			comunicazione.setDestinatari(accountService.getUserByProfileEnum(ProfileEnum.SEGRETERIA));
		}
		//inserisco gli id dei destinatari tra gli utenti che non hanno ancora letto la comunicazione
		Set<Long> utentiCheDevonoLeggere =  new HashSet<Long>();
		for (Account a : comunicazione.getDestinatari()) {
			utentiCheDevonoLeggere.add(a.getId());
		}
		comunicazione.setUtentiCheDevonoLeggere(utentiCheDevonoLeggere);
		comunicazione.setDataCreazione(LocalDateTime.now());
		comunicazione.setDataUltimaModifica(LocalDateTime.now());
		comunicazioneRepository.save(comunicazione);
	}

	@Override
	public boolean canAccountRespondToComunicazione(Account account, Comunicazione comunicazione) {
		if(!comunicazione.isChiusa() && (comunicazione.getDestinatari().contains(account) || comunicazione.getMittente().equals(account)))
			return true;
		else return false;
	}

	@Override
	public void contrassegnaComeLetta(Long id) {
		Comunicazione comunicazione = getComunicazioneById(id);
		Set<Long> utentiCheDevonoLeggere = comunicazione.getUtentiCheDevonoLeggere();
		utentiCheDevonoLeggere.remove(Utils.getAuthenticatedUser().getAccount().getId());
		comunicazione.setUtentiCheDevonoLeggere(utentiCheDevonoLeggere);
		comunicazioneRepository.save(comunicazione);
	}

	//salvataggio risposta per la comunicazione il cui id è passato come parametro
	@Override
	public void reply(ComunicazioneResponse risposta, Long id) {
		Comunicazione comunicazione = getComunicazioneById(id);
		Set<ComunicazioneResponse> risposte = comunicazione.getRisposte();
		risposta.setDataRisposta(LocalDateTime.now());
		comunicazione.setDataUltimaModifica(LocalDateTime.now());
		risposta.setComunicazione(comunicazione);
		risposte.add(risposta);
		Set<Long> utentiCheDevonoLeggere = comunicazione.getUtentiCheDevonoLeggere();
		utentiCheDevonoLeggere.add(comunicazione.getMittente().getId());
		//aggiungo tutti i destinatari alla lista degli id utente che devono rileggere la comunicazione
		for (Account a : comunicazione.getDestinatari()) {
			utentiCheDevonoLeggere.add(a.getId());
		}
		//rimuovo l'utente che ha creato la risposta
		utentiCheDevonoLeggere.remove(Utils.getAuthenticatedUser().getAccount().getId());
		//e salvo
		comunicazioneResponseRepository.save(risposta);
		comunicazione.setRisposte(risposte);
		comunicazioneRepository.save(comunicazione);
	}

	@Override
	public Set<Comunicazione> getAllComunicazioniRicevute(Account user) {
		return comunicazioneRepository.findAllComunicazioniByDestinatario(user);
	}

	@Override
	public Set<Comunicazione> getAllComunicazioniInviate(Account user) {
		return comunicazioneRepository.findAllComunicazioneByMittente(user);
	}

	@Override
	public Set<Comunicazione> getAllComunicazioniChiuse(Account user) {
		return comunicazioneRepository.findAllComunicazioneChiusaByUser(user);
	}


}
