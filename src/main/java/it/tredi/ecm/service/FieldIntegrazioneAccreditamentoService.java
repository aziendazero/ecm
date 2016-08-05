package it.tredi.ecm.service;

import java.util.List;
import java.util.Set;

import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;

public interface FieldIntegrazioneAccreditamentoService {
	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazioneForAccreditamento(Long accreditamentoId);
	public Set<FieldIntegrazioneAccreditamento> getAllFieldIntegrazioneForAccreditamentoAndObject(Long accreditamentoId, Long objectReference);
	public void save(List<FieldIntegrazioneAccreditamento> fieldIntegrazioneList);
	public void delete(Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList);
}
