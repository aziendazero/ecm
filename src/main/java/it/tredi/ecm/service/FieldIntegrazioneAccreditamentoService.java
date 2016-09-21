package it.tredi.ecm.service;

import java.util.List;
import java.util.Set;

import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;

public interface FieldIntegrazioneAccreditamentoService {
	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazioneForAccreditamento(Long accreditamentoId);
	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazioneForAccreditamentoAndObject(Long accreditamentoId, Long objectReference);
	public void save(List<FieldIntegrazioneAccreditamento> fieldIntegrazioneList);
	public void delete(Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList);
	public void update(Set<FieldIntegrazioneAccreditamento> toRemove, List<FieldIntegrazioneAccreditamento> toInsert);
	
	public Set<Long> getAllObjectIdByTipoIntegrazione(Long accreditamentoId, TipoIntegrazioneEnum tipo);
	public Set<FieldIntegrazioneAccreditamento> getModifiedFieldIntegrazioneForAccreditamento(Long accreditamentoId);
}
