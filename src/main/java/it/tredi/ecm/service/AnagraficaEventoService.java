package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.AnagraficaEvento;

public interface AnagraficaEventoService {
	public Set<AnagraficaEvento> getAllAnagaficheByProvider(Long providerId);
	public void save(AnagraficaEvento anagraficaEvento);
}
