package it.tredi.ecm.service;
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
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.repository.ValutazioneRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class ValutazioneServiceImpl implements ValutazioneService {
	private static Logger LOGGER = LoggerFactory.getLogger(ValutazioneServiceImpl.class);

	@Autowired private ValutazioneRepository valutazioneRepository;
	@Autowired private FieldValutazioneAccreditamentoService fieldValutazioneAccreditamentoService;

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
}
