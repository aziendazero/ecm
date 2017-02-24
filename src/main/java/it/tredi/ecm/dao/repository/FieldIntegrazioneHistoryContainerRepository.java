package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneHistoryContainer;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;

public interface FieldIntegrazioneHistoryContainerRepository extends CrudRepository<FieldIntegrazioneHistoryContainer, Long> {

	FieldIntegrazioneHistoryContainer findOneByAccreditamentoIdAndWorkFlowProcessInstanceIdAndStatoAndApplicatoFalse(Long accreditamentoId, Long processInstanceId, AccreditamentoStatoEnum stato);

	FieldIntegrazioneHistoryContainer findOneByAccreditamentoIdAndWorkFlowProcessInstanceIdAndStato(Long accreditamentoId, Long processInstanceId, AccreditamentoStatoEnum stato);

}
