package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.FieldValutazione;

public interface FieldValutazioneService {
	public Set<FieldValutazione> getAllValutazioniForAccreditamento(Long accreditamentoId);
	public void save(FieldValutazione valutazione);
}
