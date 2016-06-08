package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Anagrafica;

public interface AnagraficaService {
	public Set<Anagrafica> getAllAnagraficheFromProvider(Long providerId);
}
