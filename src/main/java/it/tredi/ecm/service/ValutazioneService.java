package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Valutazione;

public interface ValutazioneService {
	public Valutazione getValutazione(Long valutazioneId);
	public Valutazione getValutazioneByAccreditamentoIdAndAccountId(Long accreditamentoId, Long accountId);
	public Set<Valutazione> getAllValutazioniForAccreditamentoId(Long accreditamentoId);
	public void save(Valutazione valutazione);
}
