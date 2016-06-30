package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Evento;

public interface EventoService {
	public Evento getEvento(Long id);
	public Set<Evento> getAllEventiFromProvider(Long providerId);
	public Set<Evento> getAllEventiFromProviderInPianoFormativo(Long providerId, Integer pianoFormativo);
	public void save(Evento evento);
	public void delete(Long id);
}
