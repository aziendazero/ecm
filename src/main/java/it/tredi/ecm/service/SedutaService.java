package it.tredi.ecm.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.dao.entity.ValutazioneCommissione;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;

public interface SedutaService {
	public void save(Seduta seduta);
	public Set<Seduta> getAllSedute();
	public Seduta getSedutaById(Long sedutaId);
	public void removeSedutaById(Long sedutaId);
	public boolean canEditSeduta(Seduta seduta);
	public boolean canBeEvaluated(Seduta seduta);
	public boolean canBeLocked(Seduta seduta);
	public boolean canBeRemoved(Long sedutaId);
	public Set<Accreditamento> getAccreditamentiInSeduta(Long sedutaId);
	public void moveValutazioneCommissione(ValutazioneCommissione val, Seduta from, Seduta to) throws Exception;
	public Set<Seduta> getAllSeduteAfter(LocalDate date, LocalTime time);
	public void chiudiSeduta(Long sedutaId) throws Exception;
	public Map<Long, Set<AccreditamentoStatoEnum>> prepareMappaStatiValutazione(Seduta seduta) throws Exception;
	public void addValutazioneCommissioneToSeduta(String motivazioneDaInserire, Long idAccreditamentoDaInserire, Seduta seduta);
	public void removeValutazioneCommissioneFromSeduta(Long valutazioneCommissioneId);
	public Seduta getNextSeduta();
	public void inviaMailACommissioneEcm() throws Exception;
	public void bloccaSeduta(Long sedutaId) throws Exception;
	public void eseguiBloccoSeduteDaBloccare();
}
