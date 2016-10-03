package it.tredi.ecm.service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.enumlist.ValutazioneTipoEnum;
import it.tredi.ecm.dao.repository.ProfileRepository;
import it.tredi.ecm.dao.repository.ValutazioneRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class ValutazioneServiceImpl implements ValutazioneService {
	private static Logger LOGGER = LoggerFactory.getLogger(ValutazioneServiceImpl.class);

	@Autowired private ValutazioneRepository valutazioneRepository;
	@Autowired private FieldValutazioneAccreditamentoService fieldValutazioneAccreditamentoService;
	@Autowired private ProfileRepository profileRepository;
	@Autowired private AccountService accountService;

	@Override
	public Valutazione getValutazione(Long valutazioneId) {
		LOGGER.debug(Utils.getLogMessage("Recupero Valutazione " + valutazioneId));
		Valutazione valutazione = valutazioneRepository.findOne(valutazioneId);
		return valutazione;
	}

	@Override
	@Transactional
	public void save(Valutazione valutazione) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio Valutazione"));
		valutazioneRepository.save(valutazione);
	}

	@Override
	public void delete(Valutazione valutazione) {
		LOGGER.debug(Utils.getLogMessage("Eliminazione Valutazione id: " + valutazione.getId()));
		valutazioneRepository.delete(valutazione);
	}

	@Override
	public Valutazione getValutazioneByAccreditamentoIdAndAccountId(Long accreditamentoId, Long accountId) {
		LOGGER.debug(Utils.getLogMessage("Recupero Valutazione per l'accreditamento " + accreditamentoId + " eseguita dall'utente " + accountId));
		Valutazione valutazione = valutazioneRepository.findOneByAccreditamentoIdAndAccountId(accreditamentoId, accountId);
		return valutazione;
	}

	@Override
	public Set<Valutazione> getAllValutazioniForAccreditamentoId(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero tutte le Valutazione per l'accreditamento " + accreditamentoId));
		Set<Valutazione> allValutazioni = valutazioneRepository.findAllByAccreditamentoIdOrderByDataValutazioneAsc(accreditamentoId);
		return allValutazioni;
	}

	@Override
	public Set<Valutazione> getAllValutazioniCompleteForAccreditamentoId(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero tutte le Valutazioni complete per l'accreditamento " + accreditamentoId));
		Set<Valutazione> allCompleteValutazioni = valutazioneRepository.findAllByAccreditamentoIdAndDataValutazioneNotNullOrderByDataValutazioneAsc(accreditamentoId);
		return allCompleteValutazioni;
	}

	@Override
	public Set<Account> getAllValutatoriForAccreditamentoId(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Ottengo la lista dei valutatori dell'accreditamento " + accreditamentoId));
		Set<Valutazione> valutazioni = valutazioneRepository.findAllByAccreditamentoIdOrderByDataValutazioneAsc(accreditamentoId);
		Set<Account> valutatori = new HashSet<Account>();
		for (Valutazione v : valutazioni) {
			valutatori.add(v.getAccount());
		}
		return valutatori;
	}

	@Override
	public Map<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>> getMapValutatoreValutazioniByAccreditamentoIdAndSubSet(Long accreditamentoId, SubSetFieldEnum subset) {
		LOGGER.debug(Utils.getLogMessage("Genero la mappa valutatori - valutazione dell'accreditamento " + accreditamentoId + " per il subset " + subset));
		Map<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>> mappaValutatoreValutazioni = new HashMap<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>>();
		Set<Valutazione> allValutazioni = valutazioneRepository.findAllByAccreditamentoIdOrderByDataValutazioneAsc(accreditamentoId);
		//per ogni valutazione dell'accreditamento
		for (Valutazione v : allValutazioni) {
			//mi faccio restituire la valutazione relativa al subset e al determinato valutatore
			Map<IdFieldEnum, FieldValutazioneAccreditamento> mapValutazioni = fieldValutazioneAccreditamentoService.filterFieldValutazioneBySubSetAsMap(v.getValutazioni(), subset);
			//inserisco il tutto nella mappa valutatoreValutazioni
			mappaValutatoreValutazioni.put(v.getAccount(), mapValutazioni);
		}
		return mappaValutatoreValutazioni;
	}

	@Override
	public Map<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>> getMapValutatoreValutazioniByAccreditamentoIdAndObjectId(Long accreditamentoId, Long id) {
		LOGGER.debug(Utils.getLogMessage("Genero la mappa valutatori - valutazione dell'accreditamento " + accreditamentoId + " per l'oggetto " + id));
		Map<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>> mappaValutatoreValutazioni = new HashMap<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>>();
		Set<Valutazione> allValutazioni = valutazioneRepository.findAllByAccreditamentoIdOrderByDataValutazioneAsc(accreditamentoId);
		//per ogni valutazione dell'accreditamento
		for (Valutazione v : allValutazioni) {
			//mi faccio restituire la valutazione relativa al id dell'oggetto e al determinato valutatore
			Map<IdFieldEnum, FieldValutazioneAccreditamento> mapValutazioni = fieldValutazioneAccreditamentoService.filterFieldValutazioneByObjectAsMap(v.getValutazioni(), id);
			//inserisco il tutto nella mappa valutatoreValutazioni
			mappaValutatoreValutazioni.put(v.getAccount(), mapValutazioni);
		}
		return mappaValutatoreValutazioni;
	}

	@Override
	public int countRefereeNotValutatoriForAccreditamentoId(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Conto i referee che non hanno valutato l'accreditamento " + accreditamentoId));
		return valutazioneRepository.countRefereeValutatoriWithNoDataValutazioneForAccreditamentoId(accreditamentoId);
	}

	@Override
	public Set<Account> getAccountValutatoriWithDataForAccreditamentoId(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Restituisco i referee che hanno valutato l'accreditamento " + accreditamentoId));
		Set<Account> valutatori = new HashSet<Account>();
		valutatori = valutazioneRepository.getAccountValutatoriWithDataValutazioneForAccreditamentoId(accreditamentoId);
		return valutatori;
	}

	@Override
	public Map<Long, Account> getValutatoreSegreteriaForAccreditamentiList(Set<Accreditamento> accreditamentoSet) {
		LOGGER.debug(Utils.getLogMessage("Carico la mappa di chi ha preso in carica gli accreditamenti"));
		Map<Long, Account> mappaAccreditamentoAccountValutatore = new HashMap<Long, Account>();
		for (Accreditamento a : accreditamentoSet) {
			Account account = valutazioneRepository.getAccountSegreteriaValutatoreForAccreditamentoId(a.getId(), profileRepository.findOneByProfileEnum(ProfileEnum.SEGRETERIA).get());
			if (account != null)
				mappaAccreditamentoAccountValutatore.put(a.getId(), account);
		}
		return mappaAccreditamentoAccountValutatore;
	}
	
	@Override
	public void updateValutazioniNonDate(Long accreditamentoId) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Aggiornamento valutazioni non date per accreditamento: " + accreditamentoId));
		Set<Valutazione> valutazioni = getAllValutazioniForAccreditamentoId(accreditamentoId);
		for(Valutazione v : valutazioni){
			if(v.getTipoValutazione() == ValutazioneTipoEnum.REFEREE && v.getDataValutazione() == null){
				Account referee = v.getAccount();
				referee.setValutazioniNonDate(referee.getValutazioniNonDate() + 1);
				accountService.save(referee);
			}
		}
	}

	@Override
	public void dataOraScadenzaPossibiltaValutazioneCRECM(Long accreditamentoId, LocalDateTime date) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Aggiornamento dataora massima (" + date + ") entro la quale effettuare la valutazione CRECM per accreditamento: " + accreditamentoId));
		Set<Valutazione> valutazioni = getAllValutazioniForAccreditamentoId(accreditamentoId);
		for(Valutazione v : valutazioni){
			if(v.getTipoValutazione() == ValutazioneTipoEnum.REFEREE){
				v.setDataOraScadenzaPossibiltaValutazione(date);
				valutazioneRepository.save(v);
			}
		}
	}
}
