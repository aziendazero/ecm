package it.tredi.ecm.service;

import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;

public interface FieldValutazioneAccreditamentoService {
	public Set<FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamento(Long accreditamentoId);
	public Set<FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamentoAndObject(Long accreditamentoId, Long objectReference);
	public Map<IdFieldEnum,FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamentoAsMap(Long accreditamentoId);
	public Map<IdFieldEnum,FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamentoAndObjectAsMap(Long accreditamentoId, Long objectReference);
	public void save(FieldValutazioneAccreditamento valutazione);
	public void saveMapList(Map<IdFieldEnum,FieldValutazioneAccreditamento> valutazioneAsMap);
}
