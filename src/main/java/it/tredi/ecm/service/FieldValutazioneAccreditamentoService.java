package it.tredi.ecm.service;

import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;

public interface FieldValutazioneAccreditamentoService {
	public Set<FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamento(Long accreditamentoId);
	public Map<IdFieldEnum,FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamentoAsMap(Long accreditamentoId);
	public void save(FieldValutazioneAccreditamento valutazione);
	public void saveMapList(Map<IdFieldEnum,FieldValutazioneAccreditamento> valutazioneAsMap);
}
