package it.tredi.ecm.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.web.bean.FieldValutazioniRipetibiliWrapper;

public interface ValutazioneService {
	public Valutazione getValutazione(Long valutazioneId);
	public Valutazione getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(Long accreditamentoId, Long accountId);
	public Set<Valutazione> getAllValutazioniForAccreditamentoIdAndNotStoricizzato(Long accreditamentoId);
	public Set<Valutazione> getAllValutazioniCompleteForAccreditamentoIdAndNotStoricizzato(Long accreditamentoId);
	public void save(Valutazione valutazione);
	public void saveAndFlush(Valutazione valutazione);
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
	public void dataOraScadenzaPossibilitaValutazione(Long accreditamentoId, LocalDateTime date) throws Exception;
	public Map<Long,LocalDateTime> getScadenzaValutazioneByValutatoreId(Long id);
	public Set<Valutazione> getAllValutazioniForAccount(Long accountId);
	public Valutazione detachValutazione(Valutazione valutazione) throws Exception;
	public void cloneDetachedValutazione(Valutazione valStoricizzata);
	public void copiaInStorico(Valutazione valutazione) throws Exception;
	public Valutazione getValutazioneSegreteriaForAccreditamentoIdNotStoricizzato(Long accreditamentoId);
	public Set<Valutazione> getAllValutazioniStoricizzateForAccreditamentoId(Long accreditamentoId);
	public Map<String, Map<IdFieldEnum, FieldValutazioneAccreditamento>> getMapAllValutazioneSingoli(Valutazione valutazione, Accreditamento accreditamento);
	public Map<String, FieldValutazioniRipetibiliWrapper> getMapAllValutazioneRipetibili(Valutazione valutazione, Accreditamento accreditamento);
	public void setEsitoForEnabledFields(Valutazione valutazione, Boolean esito) throws Exception;
	public void valutaTuttiSi(Accreditamento accreditamento, Account account);
	public void resetEsitoAndEnabledForSubset(Valutazione valutazioneReferee, Map<IdFieldEnum, Long> campiDaValutare);
}
