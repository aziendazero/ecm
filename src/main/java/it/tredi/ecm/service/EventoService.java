package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.File;

public interface EventoService {
	public Evento getEvento(Long id);
	public Set<Evento> getAllEventiFromProvider(Long providerId);
	public Set<Evento> getAllEventiFromProviderInPianoFormativo(Long providerId, Integer pianoFormativo);
	public void save(Evento evento);
	public void delete(Long id);
	
	public void copyEvento(Evento src, Evento dst) throws CloneNotSupportedException;
	public void validaRendiconto(File rendiconto) throws Exception;
}
