package it.tredi.ecm.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneHistoryContainer;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;
import it.tredi.ecm.dao.repository.FieldIntegrazioneAccreditamentoRepository;
import it.tredi.ecm.dao.repository.FieldIntegrazioneHistoryContainerRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class FieldIntegrazioneAccreditamentoServiceImpl implements FieldIntegrazioneAccreditamentoService{

	private static final Logger LOGGER = LoggerFactory.getLogger(FieldIntegrazioneAccreditamentoServiceImpl.class);

	@Autowired private FieldIntegrazioneAccreditamentoRepository fieldIntegrazioneAccreditamentoRepository;
	@Autowired private FieldIntegrazioneHistoryContainerRepository fieldIntegrazioneHistoryContainerRepository;
	@Autowired private AccreditamentoService accreditamentoService;

	@Override
	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazioneForAccreditamentoByContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long workFlowProcessInstanceId) {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di FieldIntegrazioneAccreditamento per Domanda Accreditamento: " + accreditamentoId));
		FieldIntegrazioneHistoryContainer container = fieldIntegrazioneHistoryContainerRepository.findOneByAccreditamentoIdAndWorkFlowProcessInstanceIdAndStato(accreditamentoId, workFlowProcessInstanceId, stato);
		if(container != null)
			return container.getIntegrazioni();
		else return null;
	}
	@Override
	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazioneForAccreditamentoAndObjectByContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long workFlowProcessInstanceId, Long objectReference) {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di FieldIntegrazioneAccreditamento per Domanda Accreditamento: " + accreditamentoId + " riferiti all'oggetto: " + objectReference));
		FieldIntegrazioneHistoryContainer container = fieldIntegrazioneHistoryContainerRepository.findOneByAccreditamentoIdAndWorkFlowProcessInstanceIdAndStato(accreditamentoId, workFlowProcessInstanceId, stato);
		if(container != null) {
			Set<FieldIntegrazioneAccreditamento> allFieldIntegrazioneList = container.getIntegrazioni();
			Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList = new HashSet<FieldIntegrazioneAccreditamento>();
			for(FieldIntegrazioneAccreditamento fia : allFieldIntegrazioneList) {
				if(fia.getObjectReference() == objectReference)
					fieldIntegrazioneList.add(fia);
			}
			return fieldIntegrazioneList;
		}
		else return null;
	}

	@Override
	@Transactional
	public void save(List<FieldIntegrazioneAccreditamento> fieldIntegrazioneList) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio lista di FieldIntegrazioneAccreditamento"));
		fieldIntegrazioneAccreditamentoRepository.save(fieldIntegrazioneList);
	}

	@Override
	@Transactional
	public void saveSet(Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio lista di FieldIntegrazioneAccreditamento"));
		fieldIntegrazioneAccreditamentoRepository.save(fieldIntegrazioneList);
	}

	@Override
	@Transactional
	public void delete(Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList) {
		LOGGER.debug(Utils.getLogMessage("Eliminazione lista di FieldIntegrazioneAccreditamento"));
		for(FieldIntegrazioneAccreditamento f : fieldIntegrazioneList)
			LOGGER.debug(Utils.getLogMessage("Field: " + f.getId()));
		fieldIntegrazioneAccreditamentoRepository.delete(fieldIntegrazioneList);
	}

	@Override
	@Transactional
	public void update(Set<FieldIntegrazioneAccreditamento> toRemove, List<FieldIntegrazioneAccreditamento> toInsert, Long accreditamentoId, Long processInstanceId, AccreditamentoStatoEnum stato) {
		LOGGER.debug(Utils.getLogMessage("Recupero container history field Integrazione dell'accreditamento " + accreditamentoId + ", nello stato "+ stato + " agganciato al flusso" + processInstanceId + " per l'aggiornamento dei field integrazione"));
		FieldIntegrazioneHistoryContainer container = fieldIntegrazioneHistoryContainerRepository.findOneByAccreditamentoIdAndWorkFlowProcessInstanceIdAndStatoAndApplicatoFalse(accreditamentoId, processInstanceId, stato);
		container.getIntegrazioni().removeAll(toRemove);
		delete(toRemove);
		save(toInsert);
		container.getIntegrazioni().addAll(toInsert);
		saveContainer(container);
	}

	@Override
	public Set<Long> getAllObjectIdByTipoIntegrazione(Long accreditamentoId, AccreditamentoStatoEnum stato, Long workFlowProcessInstanceId, TipoIntegrazioneEnum tipo) {
		LOGGER.debug(Utils.getLogMessage("Recupero oggetti integrazione di tipo " + tipo + " per accreditamento " + accreditamentoId));
		FieldIntegrazioneHistoryContainer container = fieldIntegrazioneHistoryContainerRepository.findOneByAccreditamentoIdAndWorkFlowProcessInstanceIdAndStatoAndApplicatoFalse(accreditamentoId, workFlowProcessInstanceId, stato);
		if(container != null) {
			Set<FieldIntegrazioneAccreditamento> allFieldIntegrazioneList = container.getIntegrazioni();
			Set<Long> objectIdList = new HashSet<Long>();
			for(FieldIntegrazioneAccreditamento fia : allFieldIntegrazioneList) {
				if(fia.getTipoIntegrazioneEnum() == tipo)
					objectIdList.add(fia.getObjectReference());
			}
			return objectIdList;
		}
		else return null;
	}

	@Override
	public Set<FieldIntegrazioneAccreditamento> getModifiedFieldIntegrazioneForAccreditamento(Long accreditamentoId, AccreditamentoStatoEnum stato, Long workFlowProcessInstanceId) {
		LOGGER.debug(Utils.getLogMessage("Recupero oggetti integrazione modificati per accreditamento " + accreditamentoId));
		FieldIntegrazioneHistoryContainer container = fieldIntegrazioneHistoryContainerRepository.findOneByAccreditamentoIdAndWorkFlowProcessInstanceIdAndStatoAndApplicatoFalse(accreditamentoId, workFlowProcessInstanceId, stato);
		if(container != null) {
			Set<FieldIntegrazioneAccreditamento> allFieldIntegrazioneList = container.getIntegrazioni();
			Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList = new HashSet<FieldIntegrazioneAccreditamento>();
			for(FieldIntegrazioneAccreditamento fia : allFieldIntegrazioneList) {
				if(fia.isModificato())
					fieldIntegrazioneList.add(fia);
			}
			return fieldIntegrazioneList;
		}
		else return null;
	}
	//2017-02-24 tiommi: non serve più viene settato flag nel container history come già applicati
//	@Override
//	public void removeAllFieldIntegrazioneForAccreditamento(Long accreditamentoId) {
//		LOGGER.debug(Utils.getLogMessage("Rimozione oggetti integrazione per accreditamento " + accreditamentoId));
//		fieldIntegrazioneAccreditamentoRepository.deleteAllByAccreditamentoId(accreditamentoId);
//	}

	@Override
	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazioneApprovedBySegreteria(Long accreditamentoId, AccreditamentoStatoEnum stato, Long workFlowProcessInstanceId) {
		LOGGER.debug(Utils.getLogMessage("Recupero tutti i field integrazione approvati dalla segreteria dell'accreditamento: " + accreditamentoId));
		return fieldIntegrazioneAccreditamentoRepository.findAllApprovedBySegreteria(accreditamentoId, stato.name(), workFlowProcessInstanceId);
	}

	@Override
	public void createFieldIntegrazioneHistoryContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long processInstanceId) {
		LOGGER.debug(Utils.getLogMessage("Creo il container per il Field Integrazione per l'accreditamento " + accreditamentoId + " in " + stato + " del workflow: " + processInstanceId));
		FieldIntegrazioneHistoryContainer container = new FieldIntegrazioneHistoryContainer(accreditamentoId, stato, processInstanceId);
		fieldIntegrazioneHistoryContainerRepository.save(container);
	}

	@Override
	public void saveContainer(FieldIntegrazioneHistoryContainer container) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio container per il Field Integrazione"));
		fieldIntegrazioneHistoryContainerRepository.save(container);
	}

	@Override
	public FieldIntegrazioneHistoryContainer getContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long processInstanceId) {
		LOGGER.debug(Utils.getLogMessage("Recuper container per accreditamento: " + accreditamentoId + " in stato: " + stato + " del workflow: " + processInstanceId));
		return fieldIntegrazioneHistoryContainerRepository.findOneByAccreditamentoIdAndWorkFlowProcessInstanceIdAndStato(accreditamentoId, processInstanceId, stato);
	}

	@Override
	public void applyIntegrazioneInContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long processInstanceId) {
		LOGGER.debug(Utils.getLogMessage("Segno il container dei Field Integrazione come applicato"));
		FieldIntegrazioneHistoryContainer container = fieldIntegrazioneHistoryContainerRepository.findOneByAccreditamentoIdAndWorkFlowProcessInstanceIdAndStatoAndApplicatoFalse(accreditamentoId, processInstanceId, stato);
		if(container != null){
			container.setApplicato(true);
			saveContainer(container);
		}
	}

	@Override
	public void removeFieldIntegrazioneByObjectReferenceAndContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long workFlowProcessInstanceId, Long objectId) {
		LOGGER.debug(Utils.getLogMessage("Eliminazione dei Field Integrazione dal container relativo all'accreditamento " + accreditamentoId + " in stato " + stato + " relativi all'oggetto " + objectId));
		FieldIntegrazioneHistoryContainer container = fieldIntegrazioneHistoryContainerRepository.findOneByAccreditamentoIdAndWorkFlowProcessInstanceIdAndStatoAndApplicatoFalse(accreditamentoId, workFlowProcessInstanceId, stato);
		Iterator<FieldIntegrazioneAccreditamento> iterator = container.getIntegrazioni().iterator();
		while(iterator.hasNext()) {
			FieldIntegrazioneAccreditamento fia = iterator.next();
			if(fia.getObjectReference() == objectId) {
				iterator.remove();
				delete(fia);
			}
		}
	}

	@Override
	public void delete(FieldIntegrazioneAccreditamento fia) {
		LOGGER.debug(Utils.getLogMessage("Eliminazione FieldIntegrazioneAccreditamento: " + fia.getId()));
		fieldIntegrazioneAccreditamentoRepository.delete(fia);
	}
	@Override
	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazioneFittiziForAccreditamentoByContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long workFlowProcessInstanceId) {
		LOGGER.debug(Utils.getLogMessage("Recupero Field Integrazione FITTIZI dal container relativo all'accreditamento " + accreditamentoId + " in stato " + stato));
		return fieldIntegrazioneAccreditamentoRepository.findAllFittiziByContainer(accreditamentoId, stato.name(), workFlowProcessInstanceId);
	}

	@Override
	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazionePerSbloccoValutazioneForAccreditamentoByContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long workFlowProcessInstanceId){
		LOGGER.debug(Utils.getLogMessage("Recupero Field Integrazione Per sblocco valutazione dal container relativo all'accreditamento " + accreditamentoId + " in stato " + stato));
		Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList = new HashSet<FieldIntegrazioneAccreditamento>();
		fieldIntegrazioneList.addAll(getAllFieldIntegrazioneForAccreditamentoByContainer(accreditamentoId, stato, workFlowProcessInstanceId));
		fieldIntegrazioneList.addAll(getAllFieldIntegrazioneFittiziForAccreditamentoByContainer(accreditamentoId, stato, workFlowProcessInstanceId));
		return fieldIntegrazioneList;
	}
}
