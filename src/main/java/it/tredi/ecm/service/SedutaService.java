package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Seduta;

public interface SedutaService {
	Set<Seduta> getAllSedute();

	Seduta getSedutaById(Long sedutaId);

	void removeSedutaById(Long sedutaId);

	boolean canEditSeduta(Seduta seduta);

	boolean canBeRemoved(Long sedutaId);

	Set<Accreditamento> getAccreditamentiInSeduta(Long sedutaId);
}
