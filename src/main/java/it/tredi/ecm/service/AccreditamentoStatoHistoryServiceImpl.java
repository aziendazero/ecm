package it.tredi.ecm.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.AccreditamentoStatoHistory;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.repository.AccreditamentoStatoHistoryRepository;

@Service
public class AccreditamentoStatoHistoryServiceImpl implements AccreditamentoStatoHistoryService{
	private static Logger LOGGER = LoggerFactory.getLogger(AccreditamentoStatoHistoryServiceImpl.class);

	@Autowired private AccreditamentoStatoHistoryRepository accreditamentoStatoHistoryRepository;

	@Override
	public Set<AccreditamentoStatoHistory> getAllByAccreditamentoId(Long accreditamentoId) {
		LOGGER.info("Recupero history accreditamento");
		return accreditamentoStatoHistoryRepository.findAllByAccreditamentoIdOrderByDataFine(accreditamentoId);
	}

	@Override
	public Set<AccreditamentoStatoHistory> getAllByAccreditamentoIdAndProcessInstanceId(Long accreditamentoId, Long processInstanceId) {
		LOGGER.info("Recupero history accreditamento " + accreditamentoId + " per il workflow " + processInstanceId);
		return accreditamentoStatoHistoryRepository.findAllByAccreditamentoIdAndProcessInstanceIdOrderByDataFine(accreditamentoId, processInstanceId);
	}

	@Override
	public void createHistoryFine(Accreditamento accreditamento, Long processInstanceId, AccreditamentoStatoEnum stato, LocalDateTime dataFine) {
		LOGGER.info("Registrazione history accreditamento");
		AccreditamentoStatoHistory history = new AccreditamentoStatoHistory();
		history.setAccreditamento(accreditamento);
		history.setProcessInstanceId(processInstanceId);
		history.setStato(stato);
		history.setDataFine(dataFine);
		accreditamentoStatoHistoryRepository.save(history);
	}
}
