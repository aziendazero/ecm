package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Valutazione;

public interface ValutazioneService {
	public Set<Valutazione> getAllValutazioniForAccreditamento(Long accreditamentoId);
	public Set<Valutazione> getAllValutazioniForSezioneAccreditamento(Long accreditamentoId, int start, int end);
	public void save(Valutazione valutazione);
}
