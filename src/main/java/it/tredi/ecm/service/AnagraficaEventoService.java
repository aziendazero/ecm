package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.AnagraficaEvento;

public interface AnagraficaEventoService {
	public AnagraficaEvento getAnagraficaEvento(Long anagraficaEventoId);
	public AnagraficaEvento getAnagraficaEventoByCodiceFiscaleForProvider(String codiceFiscale, Long providerId);
	public Set<AnagraficaEvento> getAllAnagaficheByProvider(Long providerId);
	public void save(AnagraficaEvento anagraficaEvento);
}
