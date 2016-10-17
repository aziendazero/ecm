package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.AnagraficaFullEvento;

public interface AnagraficaFullEventoService {
	public AnagraficaFullEvento getAnagraficaFullEvento(Long anagraficaFullEventoId);
	public AnagraficaFullEvento getAnagraficaFullEventoByCodiceFiscaleForProvider(String codiceFiscale, Long providerId);
	public Set<AnagraficaFullEvento> getAllAnagraficheFullEventoByProvider(Long providerId);
	public void save(AnagraficaFullEvento anagraficaFullEvento);
}
