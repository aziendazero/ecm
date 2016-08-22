package it.tredi.ecm.service;

import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;

public interface ValutazioneService {
	public Valutazione getValutazione(Long valutazioneId);
	public Valutazione getValutazioneByAccreditamentoIdAndAccountId(Long accreditamentoId, Long accountId);
	public Set<Valutazione> getAllValutazioniForAccreditamentoId(Long accreditamentoId);
	public void save(Valutazione valutazione);
	public Set<Account> getAllValutatoriForAccreditamentoId(Long accreditamentoId);
	public Map<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>> getMapValutatoreValutazioniByAccreditamentoIdAndSubSet(Long accreditamentoId, SubSetFieldEnum subset);
	public Map<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>> getMapValutatoreValutazioniByAccreditamentoIdAndObjectId(Long accreditamentoId, Long id);
}
