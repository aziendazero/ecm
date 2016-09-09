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
	Set<Seduta> getAllSedute();
	Seduta getSedutaById(Long sedutaId);
	void removeSedutaById(Long sedutaId);
	boolean canEditSeduta(Seduta seduta);
	boolean canBeEvaluated(Seduta seduta);
	boolean canBeLocked(Seduta seduta);
	boolean canBeRemoved(Long sedutaId);
	Set<Accreditamento> getAccreditamentiInSeduta(Long sedutaId);
	void moveValutazioneCommissione(ValutazioneCommissione val, Seduta from, Seduta to);
	Set<Seduta> getAllSeduteAfter(LocalDate date, LocalTime time);
	void lockSeduta(Long sedutaId) throws Exception;
	Map<Long, Set<AccreditamentoStatoEnum>> prepareMappaStatiValutazione(Seduta seduta);
	void addValutazioneCommissioneToSeduta(String motivazioneDaInserire, Long idAccreditamentoDaInserire, Seduta seduta);
	void removeValutazioneCommissioneFromSeduta(Long valutazioneCommissioneId);
}
