package it.tredi.ecm.service;

import java.util.List;
import java.util.Set;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.File;

public interface EventoService {
	public Evento getEvento(Long id);
	public Set<Evento> getAllEventiFromProvider(Long providerId);
	public void save(Evento evento);
	public void delete(Long id);

	public void validaRendiconto(Long id, File rendiconto) throws Exception;
	public List<Evento> getAllEventi();
	public Set<Evento> getAllEventiForProviderId(Long providerId);
	public boolean canCreateEvento(Account account);
}
