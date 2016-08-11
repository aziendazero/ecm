package it.tredi.ecm.service;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Valutazione;
 import it.tredi.ecm.dao.repository.ValutazioneRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class ValutazioneServiceImpl implements ValutazioneService {
	private static Logger LOGGER = LoggerFactory.getLogger(ValutazioneServiceImpl.class);

	@Autowired private ValutazioneRepository valutazioneRepository;

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
		Set<Valutazione> allValutazioni = valutazioneRepository.findAllByAccreditamentoId(accreditamentoId);
		return allValutazioni;
	}

}
