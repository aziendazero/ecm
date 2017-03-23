package it.tredi.ecm.service;

import java.util.List;
import java.util.Set;

import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneHistoryContainer;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;

public interface FieldIntegrazioneAccreditamentoService {
	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazioneForAccreditamentoByContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long workFlowProcessInstanceId);
	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazioneForAccreditamentoAndObjectByContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long workFlowProcessInstanceId, Long objectReference);
	public void save(List<FieldIntegrazioneAccreditamento> fieldIntegrazioneList);
	public void saveSet(Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList);
	public void delete(Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList);
	public void update(Set<FieldIntegrazioneAccreditamento> toRemove, List<FieldIntegrazioneAccreditamento> toInsert, Long accreditamentoId, Long processInstanceId, AccreditamentoStatoEnum stato);

	public Set<Long> getAllObjectIdByTipoIntegrazione(Long accreditamentoId, AccreditamentoStatoEnum stato, Long workFlowProcessInstanceId, TipoIntegrazioneEnum tipo);
	public Set<FieldIntegrazioneAccreditamento> getModifiedFieldIntegrazioneForAccreditamento(Long accreditamentoId, AccreditamentoStatoEnum stato, Long workFlowProcessInstanceId);
	//2017-02-24 tiommi: non serve più viene settato flag nel container history come già applicati
//	public void removeAllFieldIntegrazioneForAccreditamento(Long accreditamentoId);

	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazioneApprovedBySegreteria(Long accreditamentoId, AccreditamentoStatoEnum stato, Long workFlowProcessInstanceId);
	public void createFieldIntegrazioneHistoryContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long processInstanceId);
	public void saveContainer(FieldIntegrazioneHistoryContainer container);
	public void applyIntegrazioneInContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long processInstanceId);
	public void removeFieldIntegrazioneByObjectReferenceAndContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long workFlowProcessInstanceId, Long objectId);
	public void delete(FieldIntegrazioneAccreditamento fia);

	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazioneFittiziForAccreditamentoByContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long workFlowProcessInstanceId);
	public FieldIntegrazioneHistoryContainer getContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long processInstanceId);
}
