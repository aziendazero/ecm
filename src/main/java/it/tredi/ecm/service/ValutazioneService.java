package it.tredi.ecm.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;

public interface ValutazioneService {
	public Valutazione getValutazione(Long valutazioneId);
	public Valutazione getValutazioneByAccreditamentoIdAndAccountId(Long accreditamentoId, Long accountId);
	public Set<Valutazione> getAllValutazioniForAccreditamentoId(Long accreditamentoId);
	public Set<Valutazione> getAllValutazioniCompleteForAccreditamentoId(Long accreditamentoId);
	public void save(Valutazione valutazione);
	public void delete(Valutazione valutazione);
	public Set<Account> getAllValutatoriForAccreditamentoId(Long accreditamentoId);
	public Map<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>> getMapValutatoreValutazioniByAccreditamentoIdAndSubSet(Long accreditamentoId, SubSetFieldEnum subset);
	public Map<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>> getMapValutatoreValutazioniByAccreditamentoIdAndObjectId(Long accreditamentoId, Long id);
	public int countRefereeNotValutatoriForAccreditamentoId(Long accreditamentoId);
	public Set<Account> getAccountValutatoriWithDataForAccreditamentoId(Long accreditamentoId);
	public Map<Long, Account> getValutatoreSegreteriaForAccreditamentiList(Set<Accreditamento> accreditamentoSet);
	public Map<Long, Set<Account>> getValutatoriForAccreditamentiList(Set<Accreditamento> accreditamentoSet);
	public void updateValutazioniNonDate(Long accreditamentoId) throws Exception;
	public void dataOraScadenzaPossibilitaValutazioneCRECM(Long accreditamentoId, LocalDateTime date) throws Exception;
	public LocalDateTime getScadenzaValutazioneByValutatoreId(Long id);
}
