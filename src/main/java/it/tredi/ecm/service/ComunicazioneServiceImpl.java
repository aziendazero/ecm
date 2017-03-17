package it.tredi.ecm.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Comunicazione;
import it.tredi.ecm.dao.entity.ComunicazioneResponse;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.dao.repository.ComunicazioneRepository;
import it.tredi.ecm.dao.repository.ComunicazioneResponseRepository;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.RicercaComunicazioneWrapper;

@Service
public class ComunicazioneServiceImpl implements ComunicazioneService {
	private static Logger LOGGER = LoggerFactory.getLogger(ComunicazioneServiceImpl.class);

	@Autowired private AccountService accountService;
	@Autowired private ComunicazioneRepository comunicazioneRepository;
	@Autowired private ComunicazioneResponseRepository comunicazioneResponseRepository;
	@PersistenceContext EntityManager entityManager;

	@Override
	public Comunicazione getComunicazioneById(Long id) {
		return comunicazioneRepository.findOne(id);
	}

	@Override
	public int countAllComunicazioniRicevuteByAccountId(Long id) {
		Account user = accountService.getUserById(id);
		if(user.isProviderVisualizzatore()) {
			Account providerComunicazioni = accountService.getAccountComunicazioniProviderForProvider(user.getProvider());
			return comunicazioneRepository.countAllComunicazioniRicevuteByAccount(providerComunicazioni);
		}
		if(!user.isSegreteria()) {
			return comunicazioneRepository.countAllComunicazioniRicevuteByAccount(user);
		}
		else {
			//l'utente è anche segreteria quindi amplio la query con le comunicazioni inviate alla segreteria
			Account segreteriaComunicazioni = accountService.getAccountComunicazioniSegretereria();
			return comunicazioneRepository.countAllComunicazioniRicevuteByAccountOrBySegreteria(user, segreteriaComunicazioni);
		}
	}

	@Override
	public int countAllComunicazioniInviateByAccountId(Long id) {
		Account user = accountService.getUserById(id);
		Account accountComunicazioni = null;
		if(user.isProviderVisualizzatore()) {
			accountComunicazioni = accountService.getAccountComunicazioniProviderForProvider(user.getProvider());
		}
		else {
			//user appartiene alla segreteria dal momento che gli altri utenti possono aprire comunicazioni solo verso la segreteria
			accountComunicazioni = accountService.getAccountComunicazioniSegretereria();
		}
		//non ci sono altri casi poichè le altre tipologia di utenti non possono inviare comunicazioni, ma solo riceverle
		return comunicazioneRepository.countAllComunicazioniInviateForAccount(accountComunicazioni);
	}

	@Override
	public int countAllComunicazioniChiuseByAccountId(Long id) {
		Account user = accountService.getUserById(id);
		if(user.isProviderVisualizzatore()) {
			Account providerComunicazioni = accountService.getAccountComunicazioniProviderForProvider(user.getProvider());
			return comunicazioneRepository.countAllComunicazioniChiuseByGroup(providerComunicazioni);
		}
		if(!user.isSegreteria()) {
			return comunicazioneRepository.countAllComunicazioniChiuseByAccount(user);
		}
		else {
			//l'utente è anche segreteria quindi amplio la query con le comunicazioni inviate alla segreteria
			//controllo se l'utente è nei destinatari o è il mittente, se il fake della segreteria è nei destinatari o se è stato inviata una comunicazione per suo conto;
			Account segreteriaComunicazioni = accountService.getAccountComunicazioniSegretereria();
			return comunicazioneRepository.countAllComunicazioniChiuseByAccountOrByGroup(user, segreteriaComunicazioni);
		}
	}

	@Override
	public int countAllComunicazioniStoricoByAccountId(Long id) {
		Account user = accountService.getUserById(id);
		if(user.isProviderVisualizzatore()) {
			Account providerComunicazioni = accountService.getAccountComunicazioniProviderForProvider(user.getProvider());
			return comunicazioneRepository.countAllComunicazioniStoricoByGroup(providerComunicazioni);
		}
		if(!user.isSegreteria()) {
			return comunicazioneRepository.countAllComunicazioniStoricoByAccount(user);
		}
		else {
			//l'utente è anche segreteria quindi amplio la query con le comunicazioni inviate alla segreteria
			//controllo se l'utente è nei destinatari o è il mittente, se il fake della segreteria è nei destinatari o se è stato inviata una comunicazione per suo conto;
			Account segreteriaComunicazioni = accountService.getAccountComunicazioniSegretereria();
			return comunicazioneRepository.countAllComunicazioniStoricoByAccountOrByGroup(user, segreteriaComunicazioni);
		}
	}

	@Override
	public int countAllComunicazioniNonRisposteByAccountId(Long id) {
		Account user = accountService.getUserById(id);
		if(user.isProviderVisualizzatore()) {
			Account providerComunicazioni = accountService.getAccountComunicazioniProviderForProvider(user.getProvider());
			return comunicazioneRepository.countAllComunicazioniNonRisposteByGroup(providerComunicazioni);
		}
		if(!user.isSegreteria()) {
			return comunicazioneRepository.countAllComunicazioniNonRisposteByAccount(user);
		}
		else {
			//l'utente è anche segreteria quindi amplio la query con le comunicazioni inviate alla segreteria
			//controllo se l'utente è nei destinatari o è il mittente, se il fake della segreteria è nei destinatari o se è stato inviata una comunicazione per suo conto;
			Account segreteriaComunicazioni = accountService.getAccountComunicazioniSegretereria();
			return comunicazioneRepository.countAllComunicazioniNonRisposteByAccountOrByGroup(user, segreteriaComunicazioni);
		}
	}

	@Override
	public Set<Comunicazione> getAllComunicazioniRicevuteByAccount(Account user) {
		if(user.isProviderVisualizzatore()) {
			Account providerComunicazioni = accountService.getAccountComunicazioniProviderForProvider(user.getProvider());
			return comunicazioneRepository.findAllComunicazioniRicevuteByAccount(providerComunicazioni);
		}
		if(!user.isSegreteria()) {
			return comunicazioneRepository.findAllComunicazioniRicevuteByAccount(user);
		}
		else {
			//l'utente è anche segreteria quindi amplio la query con le comunicazioni inviate alla segreteria
			Account segreteriaComunicazioni = accountService.getAccountComunicazioniSegretereria();
			return comunicazioneRepository.findAllComunicazioniRicevuteByAccountOrBySegreteria(user, segreteriaComunicazioni);
		}
	}

	@Override
	public Set<Comunicazione> getAllComunicazioniInviateByAccount(Account user) {
		Account accountComunicazioni = null;
		if(user.isProviderVisualizzatore()) {
			accountComunicazioni = accountService.getAccountComunicazioniProviderForProvider(user.getProvider());
		}
		else {
			//user appartiene alla segreteria dal momento che gli altri utenti possono aprire comunicazioni solo verso la segreteria
			accountComunicazioni = accountService.getAccountComunicazioniSegretereria();
		}
		//non ci sono altri casi poichè le altre tipologia di utenti non possono inviare comunicazioni, ma solo riceverle
		return comunicazioneRepository.findAllComunicazioniInviateForAccount(accountComunicazioni);
	}

	@Override
	public Set<Comunicazione> getAllComunicazioniChiuseByAccount(Account user) {
		if(user.isProviderVisualizzatore()) {
			Account providerComunicazioni = accountService.getAccountComunicazioniProviderForProvider(user.getProvider());
			return comunicazioneRepository.findAllComunicazioniChiuseByGroup(providerComunicazioni);
		}
		if(!user.isSegreteria()) {
			return comunicazioneRepository.findAllComunicazioniChiuseByAccount(user);
		}
		else {
			//l'utente è anche segreteria quindi amplio la query con le comunicazioni inviate alla segreteria
			//controllo se l'utente è nei destinatari o è il mittente, se il fake della segreteria è nei destinatari o se è stato inviata una comunicazione per suo conto;
			Account segreteriaComunicazioni = accountService.getAccountComunicazioniSegretereria();
			return comunicazioneRepository.findAllComunicazioniChiuseByAccountOrByGroup(user, segreteriaComunicazioni);
		}
	}

	@Override
	public Set<Comunicazione> getAllComunicazioniByAccount(Account user) {
		if(user.isProviderVisualizzatore()) {
			Account providerComunicazioni = accountService.getAccountComunicazioniProviderForProvider(user.getProvider());
			return comunicazioneRepository.findAllComunicazioniStoricoByGroup(providerComunicazioni);
		}
		if(!user.isSegreteria()) {
			return comunicazioneRepository.findAllComunicazioniStoricoByAccount(user);
		}
		else {
			//l'utente è anche segreteria quindi amplio la query con le comunicazioni inviate alla segreteria
			//controllo se l'utente è nei destinatari o è il mittente, se il fake della segreteria è nei destinatari o se è stato inviata una comunicazione per suo conto;
			Account segreteriaComunicazioni = accountService.getAccountComunicazioniSegretereria();
			return comunicazioneRepository.findAllComunicazioniStoricoByAccountOrByGroup(user, segreteriaComunicazioni);
		}
	}

	@Override
	public Set<Comunicazione> getAllComunicazioniNonRisposteByAccount(Account user) {
		if(user.isProviderVisualizzatore()) {
			Account providerComunicazioni = accountService.getAccountComunicazioniProviderForProvider(user.getProvider());
			return comunicazioneRepository.findAllComunicazioniNonRisposteByGroup(providerComunicazioni);
		}
		if(!user.isSegreteria()) {
			return comunicazioneRepository.findAllComunicazioniNonRisposteByAccount(user);
		}
		else {
			//l'utente è anche segreteria quindi amplio la query con le comunicazioni inviate alla segreteria
			//controllo se l'utente è nei destinatari o è il mittente, se il fake della segreteria è nei destinatari o se è stato inviata una comunicazione per suo conto;
			Account segreteriaComunicazioni = accountService.getAccountComunicazioniSegretereria();
			return comunicazioneRepository.findAllComunicazioniNonRisposteByAccountOrByGroup(user, segreteriaComunicazioni);
		}
	}

	@Override
	public List<Comunicazione> getUltimi10MessaggiNonLetti(Long id) {
		Page<Comunicazione> results = comunicazioneRepository.findMessaggiNonLettiOrderByDataUltimaModificaDesc(id, new PageRequest(0, 10));
		List<Comunicazione> ultimi10MessaggiNonLetti= results.getContent();
		for(Comunicazione c : ultimi10MessaggiNonLetti) {
			c.getDataUltimaModifica().toString();
		}
		return ultimi10MessaggiNonLetti;
	}

	@Override
	public long countAllMessaggiNonLetti(Long id) {
		return comunicazioneRepository.countAllMessaggiNonLetti(id);
	}

	@Override
	public long getIdUltimaComunicazioneRicevuta(Long id) {
		Page<Comunicazione> results = comunicazioneRepository.findMessaggiNonLettiOrderByDataUltimaModificaDesc(id, new PageRequest(0, 1));
		List<Comunicazione> ultimoMessaggioNonLetto = results.getContent();
		if (!ultimoMessaggioNonLetto.isEmpty())
			return ultimoMessaggioNonLetto.get(0).getId();
		else return 0;
	}

	//controlla l'utente se segreteria o provider (gli unici a poter creare da 0 una comunicazine)
	//e restituisce la lista di tutti i possibili destinatari
	@Override
	public Map<String, Set<Account>> getAllDestinatariDisponibili(Long id) {
		Map<String, Set<Account>> destinatariDisponibili = new HashMap<String, Set<Account>>();
		Account richiedente = accountService.getUserById(id);
		//caso segreteria invia a tutti (tranne ad altri segretari)
		if(richiedente.isSegreteria()) {
			Set<Account> providerSet = new HashSet<Account>();
			Set<Account> commissioneSet = new HashSet<Account>();
			Set<Account> refereeSet = new HashSet<Account>();
			Set<Account> osservatoriSet = new HashSet<Account>();
			Set<Account> allUsers = accountService.getAllUsers();
			for(Account a : allUsers) {
				if(!a.isSegreteria() && a.isProviderAccountComunicazioni()) {
					providerSet.add(a);
				}
				if(!a.isSegreteria() && a.isCommissioneEcm()) {
					commissioneSet.add(a);
				}
				if(!a.isSegreteria() && a.isReferee()) {
					refereeSet.add(a);
				}
				if(!a.isSegreteria() && a.isComponenteOsservatorioEcm()) {
					osservatoriSet.add(a);
				}
			}
			destinatariDisponibili.put("Provider", providerSet);
			destinatariDisponibili.put("Commissione ECM", commissioneSet);
			destinatariDisponibili.put("Referee ECM", refereeSet);
			destinatariDisponibili.put("Osservatori ECM", osservatoriSet);
		}
		return destinatariDisponibili;
	}

	//tiommi 06/03/2017 a seguito richiesta MEV in cui le comunicazioni sono gestite per gruppi provider e segreteria
	//salvataggio comunicazione, controllo mittente, se è provider aggiungo i segretari ai destinatari
	//aggiungo anche la data di invio
	@Override
	public void send(Comunicazione comunicazione, File allegato) {
		//inserisco gli id dei destinatari tra gli utenti che non hanno ancora letto la comunicazione
		Set<Long> utentiCheDevonoLeggere =  new HashSet<Long>();
		//gestioni mittente NON segreteria (possono inviare solo a segreteria)
		if(!comunicazione.getMittente().isSegreteria()) {
			//aggiungo ai destinatari il fake account segreteria comunicazioni
			Set<Account> destinatari = new HashSet<Account>();
			destinatari.add(accountService.getAccountComunicazioniSegretereria());
			comunicazione.setDestinatari(destinatari);
			comunicazione.setInviatoAllaSegreteria(true);
			//inserisco negli utenti che devono leggere tutti gli utenti segreteria
			Set<Account> membriSegreteria = accountService.getUserByProfileEnum(ProfileEnum.SEGRETERIA);
			for(Account s : membriSegreteria)
				utentiCheDevonoLeggere.add(s.getId());
			//se ad inviare è un provider aggiungo agli utenti che devono leggere tutti gli altri utenti del provider stesso
			if(comunicazione.getMittente().isProvider()) {
				Set<Account> providerUsers = accountService.getAllByProviderId(comunicazione.getMittente().getProvider().getId());
				for(Account pu : providerUsers) {
					if(pu.getId() != comunicazione.getMittente().getId() && !pu.isProviderAccountComunicazioni())
						utentiCheDevonoLeggere.add(pu.getId());
				}
				//aggiungo il fake account del provider
				Account providerComunicazioni = accountService.getAccountComunicazioniProviderForProvider(comunicazione.getMittente().getProvider());
				comunicazione.setFakeAccountComunicazioni(providerComunicazioni);
			}
		}
		//gestione mittente segreteria
		else {
			comunicazione.setInviatoAllaSegreteria(false);
			for (Account a : comunicazione.getDestinatari()) {
				Set<Account> membriSegreteria = accountService.getUserByProfileEnum(ProfileEnum.SEGRETERIA);
				for(Account s : membriSegreteria)
					if(s.getId() != comunicazione.getMittente().getId())
						utentiCheDevonoLeggere.add(s.getId());
				//tiommi 06/03/2017 a seguito richiesta MEV in cui le comunicazioni sono gestite per gruppi provider e segreteria
				//se trovo un provider aggiungo agli utenti che devono leggere tutti gli utenti del provider
				//N.B. la segreteria può aprire comunicazioni con il provider solo attraverso fake account comunicazione provider
				if(a.isProviderAccountComunicazioni()) {
					Set<Account> providerUsers = accountService.getAllByProviderId(a.getProvider().getId());
					for(Account pu : providerUsers)
						if(!pu.isProviderAccountComunicazioni())
							utentiCheDevonoLeggere.add(pu.getId());
				}
				else
					utentiCheDevonoLeggere.add(a.getId());
			}
			//aggiungo il fake account della segreteria
			Account segreteriaComunicazioni = accountService.getAccountComunicazioniSegretereria();
			comunicazione.setFakeAccountComunicazioni(segreteriaComunicazioni);
		}
		comunicazione.setUtentiCheDevonoLeggere(utentiCheDevonoLeggere);
		comunicazione.setDataCreazione(LocalDateTime.now());
		comunicazione.setDataUltimaModifica(LocalDateTime.now());
		if (allegato != null && !allegato.isNew())
			comunicazione.setAllegatoComunicazione(allegato);
		comunicazioneRepository.save(comunicazione);
	}

	@Override
	public boolean canAccountRespondToComunicazione(Account account, Comunicazione comunicazione) {
		if(comunicazione.isChiusa())
			return false;
		if(account.isSegreteria())
			return true;
		if(account.isProvider()){
			Account providerComunicazioni = accountService.getAccountComunicazioniProviderForProvider(account.getProvider());
			Set<Account> providerUsers = accountService.getAllByProviderId(account.getProvider().getId());
			if(comunicazione.getDestinatari().contains(providerComunicazioni) || providerUsers.contains(comunicazione.getMittente()))
				return true;
		}
		else
			if(comunicazione.getDestinatari().contains(account) || comunicazione.getMittente().equals(account))
				return true;
		return false;
	}

	@Override
	public boolean canAccountSeeResponse(Account account, ComunicazioneResponse response) {
		if(account.isSegreteria())
			return true;
		if(account.isProvider()){
			Account providerComunicazioni = accountService.getAccountComunicazioniProviderForProvider(account.getProvider());
			Set<Account> providerUsers = accountService.getAllByProviderId(account.getProvider().getId());
			if(response.getDestinatari().contains(providerComunicazioni) || providerUsers.contains(response.getMittente()))
				return true;
		}
		else
			if(response.getDestinatari().contains(account) || response.getMittente().equals(account))
				return true;
		return false;
	}


	@Override
	public boolean canAccountCloseComunicazione(Account account, Comunicazione comunicazione) {
		if(!comunicazione.isChiusa() && account.isSegreteria())
			return true;
		return false;
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
	public void reply(ComunicazioneResponse risposta, Long id, File allegato) {
		Comunicazione comunicazione = getComunicazioneById(id);
		Set<ComunicazioneResponse> risposte = comunicazione.getRisposte();
		risposta.setDataRisposta(LocalDateTime.now());
		comunicazione.setDataUltimaModifica(LocalDateTime.now());
		risposta.setComunicazione(comunicazione);
		if (allegato != null && !allegato.isNew())
			risposta.setAllegatoRisposta(allegato);
		risposte.add(risposta);
		Set<Long> utentiCheDevonoLeggere = comunicazione.getUtentiCheDevonoLeggere();
		//risposta di un utente NON segreteria, allerto i membri della segreteria e
		// se l'utente è un provider gli altri membri del provider
		if(!risposta.getMittente().isSegreteria()) {
			//aggiungo ai destinatari il fake account segreteria comunicazioni
			Set<Account> destinatari = new HashSet<Account>();
			destinatari.add(accountService.getAccountComunicazioniSegretereria());
			risposta.setDestinatari(destinatari);
			risposta.setInviatoAllaSegreteria(true);
			for(Account a : accountService.getUserByProfileEnum(ProfileEnum.SEGRETERIA))
				utentiCheDevonoLeggere.add(a.getId());
			if(risposta.getMittente().isProvider()) {
				for(Account pu : accountService.getAllByProviderId(risposta.getMittente().getProvider().getId())) {
					if(pu.getId() != risposta.getMittente().getId())
						utentiCheDevonoLeggere.add(pu.getId());
				}
				//aggiungo il fake account del provider
				Account providerComunicazioni = accountService.getAccountComunicazioniProviderForProvider(risposta.getMittente().getProvider());
				risposta.setFakeAccountComunicazioni(providerComunicazioni);
			}
		}
		//risposta di un utente segreteria, allerto tutti gli altri utenti segreteria e i destinatari della risposta
		// se tra questi vi sono provider, allerto tutti gli utenti del provider.
		else{
			risposta.setInviatoAllaSegreteria(false);
			for(Account a : accountService.getUserByProfileEnum(ProfileEnum.SEGRETERIA)) {
				if(a.getId() != risposta.getMittente().getId())
					utentiCheDevonoLeggere.add(a.getId());
			}
			for(Account a : risposta.getDestinatari())
				if(a.isProviderAccountComunicazioni()) {
					Set<Account> providerUsers = accountService.getAllByProviderId(a.getProvider().getId());
					for(Account pu : providerUsers)
						utentiCheDevonoLeggere.add(pu.getId());
				}
				else
					utentiCheDevonoLeggere.add(a.getId());
			//aggiungo il fake account della segreteria
			Account segreteriaComunicazioni = accountService.getAccountComunicazioniSegretereria();
			risposta.setFakeAccountComunicazioni(segreteriaComunicazioni);
		}
		//tolgo l'utente che ha mandato la risposta dagli utenti che devono leggere
		comunicazione.getUtentiCheDevonoLeggere().remove(risposta.getMittente().getId());
		comunicazioneResponseRepository.save(risposta);
		comunicazione.setRisposte(risposte);
		comunicazioneRepository.save(comunicazione);
	}

	@Override
	public void chiudiComunicazioneById(Long id) {
		Comunicazione comunicazione = comunicazioneRepository.findOne(id);
		comunicazione.setChiusa(true);
		comunicazioneRepository.save(comunicazione);
	}

	@Override
	public Set<Comunicazione> getAllComunicazioniNonLetteByAccount(Account user) {
		return comunicazioneRepository.findAllComunicazioneNonLetteOrderByDataUltimaModificaDesc(user.getId());
	}

	@Override
	public Set<Comunicazione> getAllComunicazioniByProvider(Provider provider) {
		Account providerComunicazione = accountService.getAccountComunicazioniProviderForProvider(provider);
		return comunicazioneRepository.findAllComunicazioniStoricoByGroup(providerComunicazione);
	}

	@Override
	public Set<Comunicazione> getAllComunicazioniNonRisposteFromProviderBySegreteria(Provider provider) {
		//lista delle comunicazioni dal provider alla segreteria alle quali la segreteria non ha ancora risposto
		Account providerComunicazione = accountService.getAccountComunicazioniProviderForProvider(provider);
		Account segreteriaComunicazione = accountService.getAccountComunicazioniSegretereria();
		return comunicazioneRepository.findAllComunicazioniNonRisposteFromGroupByGroup(providerComunicazione, segreteriaComunicazione);
	}

	@Override
	public List<Comunicazione> cerca(RicercaComunicazioneWrapper wrapper) {
		String query = "";
		HashMap<String, Object> params = new HashMap<String, Object>();

		query ="SELECT c FROM Comunicazione c JOIN c.destinatari d JOIN c.mittente m";

		if(wrapper.getDenominazioneLegale() != null && !wrapper.getDenominazioneLegale().isEmpty()){
			//devo fare il join con la tabella provider
			query = Utils.QUERY_AND(query,"UPPER(d.provider.denominazioneLegale) LIKE :denominazioneLegale");
			params.put("denominazioneLegale", "%" + wrapper.getDenominazioneLegale().toUpperCase() + "%");
		}

		//PROVIDER ID
		if(wrapper.getCampoIdProvider() != null){
			query = Utils.QUERY_AND(query, "(d.provider.id = :providerId OR m.provider.id = :providerId)");
			params.put("providerId", wrapper.getCampoIdProvider());
		}

		//OGGETTO
		if(wrapper.getOggetto() != null && !wrapper.getOggetto().isEmpty()){
			query = Utils.QUERY_AND(query, "UPPER(c.oggetto) LIKE :oggetto");
			params.put("oggetto", "%" + wrapper.getOggetto().toUpperCase() + "%");
		}

		//AMBITO
		if(wrapper.getAmbitiSelezionati() != null){
			query = Utils.QUERY_AND(query, "c.ambito IN (:ambitiSelezionati)");
			params.put("ambitiSelezionati", wrapper.getAmbitiSelezionati());
		}

		//TIPOLOGIA
		if(wrapper.getTipologieSelezionate() != null){
			query = Utils.QUERY_AND(query, "c.tipologia IN (:tipologieSelezionate)");
			params.put("tipologieSelezionate", wrapper.getTipologieSelezionate());
		}

		//DATA CREAZIONE
		if(wrapper.getDataCreazioneStart() != null){
			query = Utils.QUERY_AND(query, "c.dataCreazione >= :dataCreazioneStart");
			LocalDateTime ldt = Timestamp.valueOf(wrapper.getDataCreazioneStart().atStartOfDay()).toLocalDateTime();
			params.put("dataCreazioneStart", ldt);
		}

		if(wrapper.getDataCreazioneEnd() != null){
			query = Utils.QUERY_AND(query, "c.dataCreazione <= :dataCreazioneEnd");
			LocalDateTime ldt = Timestamp.valueOf(wrapper.getDataCreazioneEnd().plusDays(1).atStartOfDay()).toLocalDateTime();
			params.put("dataCreazioneEnd", ldt);
		}


		LOGGER.info(Utils.getLogMessage("Cerca Comunicazione: " + query));
		Query q = entityManager.createQuery(query, Comunicazione.class);

		Iterator<Entry<String, Object>> iterator = params.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<String, Object> pairs = iterator.next();
			q.setParameter(pairs.getKey(), pairs.getValue());
			LOGGER.info(Utils.getLogMessage(pairs.getKey() + ": " + pairs.getValue()));
		}

		List<Comunicazione> result = q.getResultList();

		return result;
	}

	@Override
	public HashMap<Long, Boolean> createMappaVisibilitaResponse(Account account, Comunicazione comunicazione) {
		HashMap<Long, Boolean> mappa = new HashMap<Long, Boolean>();
		for(ComunicazioneResponse cr : comunicazione.getRisposte()) {
			mappa.put(cr.getId(), canAccountSeeResponse(account, cr));
		}
		return mappa;
	}

}
