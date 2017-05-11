package it.tredi.ecm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.tredi.ecm.dao.entity.BonitaSemaphore;
import it.tredi.ecm.dao.entity.Token;
import it.tredi.ecm.dao.repository.SemaphoreRepository;
import it.tredi.ecm.dao.repository.TokenRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class TokenServiceImpl implements TokenService {
	private static Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

	@Autowired
	private TokenRepository tokenRepository;
	@Autowired
	private SemaphoreRepository semaphoreRepository;

	@Override
	public boolean checkTokenAndDelete(String token) {
		LOGGER.info("checkTokenAndDelete token: " + token);
		Token tokenEntity = tokenRepository.findOneByToken(token).orElse(null);
		if(tokenEntity == null) {
			String msg = "Impossibile trovare il token passato token: " + token;
			LOGGER.error(msg);
			return false;
		}
		//cancello il token e modifico lo stato
		tokenRepository.delete(tokenEntity);
		return true;
	}

	//controlla nella tabella dei semafori se c'è un semaforo "rosso" per il determinato accreditamento
	@Override
	public boolean checkReadyForBonita(Long accreditamentoId) {
		return checkReadyForBonita(accreditamentoId, null);
	}

	@Override
	public boolean checkReadyForBonita(Long accreditamentoId, Integer tentativo) {
		LOGGER.info("checkReadyForBonita accreditamento: " + accreditamentoId + (tentativo != null ? " tentativo numero: " + tentativo : ""));
		BonitaSemaphore semaphore = semaphoreRepository.findOneByAccreditamentoId(accreditamentoId).orElse(null);
		if(semaphore == null)
			return true;
		else
			return false;
	}

	//gestione race-condition bonita e applicazione, crea il semaforo per il setStatoFromBonita, che aspetta che i dati in transactional siano flushati su DB
	//la sua creazione è flushata instantaneamente su db
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void createBonitaSemaphore(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio semaforo per accreditamento: " + accreditamentoId));
		BonitaSemaphore semaphore = new BonitaSemaphore(accreditamentoId);
		semaphoreRepository.saveAndFlush(semaphore);
	}

	//gestione race-condition bonita e applicazione, rimuove il semaforo per il setStatoFromBonita, che aspetta che i dati in transactional siano flushati su DB
	//la sua rimozione NON è flushata instantaneamente su db, ma viene fatta insieme agli altri dati alla fine del metodo transactional
	//questo dovrebbe assicurare che il semaforo resti "rosso" fino al momento in cui siamo sicuri che il setStatoFromBonita può proseguire
	@Override
	public void removeBonitaSemaphore(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Rimozione semaforo per accreditamento: " + accreditamentoId));
		BonitaSemaphore semaphore = semaphoreRepository.findOneByAccreditamentoId(accreditamentoId).orElse(null);
		if(semaphore != null)
			semaphoreRepository.delete(semaphore);
	}





}
