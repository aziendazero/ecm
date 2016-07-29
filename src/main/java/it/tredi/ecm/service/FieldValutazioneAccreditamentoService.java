package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;

public interface FieldValutazioneAccreditamentoService {
	public Set<FieldValutazioneAccreditamento> getAllValutazioniForAccreditamento(Long accreditamentoId);
	public void save(FieldValutazioneAccreditamento valutazione);
}
