package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.Set;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.dao.entity.ValutazioneCommissione;

public interface SedutaService {
	Set<Seduta> getAllSedute();

	Seduta getSedutaById(Long sedutaId);

	void removeSedutaById(Long sedutaId);

	boolean canEditSeduta(Seduta seduta);

	boolean canBeRemoved(Long sedutaId);

	Set<Accreditamento> getAccreditamentiInSeduta(Long sedutaId);

	Set<Seduta> getAllSeduteAfter(LocalDate date);

	void moveValutazioneCommissione(ValutazioneCommissione val, Seduta from, Seduta to);
}
