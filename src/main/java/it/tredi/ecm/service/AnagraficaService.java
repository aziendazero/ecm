package it.tredi.ecm.service;

import java.util.Optional;
import java.util.Set;

import it.tredi.ecm.dao.entity.Anagrafica;

public interface AnagraficaService {
	public Set<Anagrafica> getAllAnagraficheAttiveByProviderId(Long providerId);
	public Anagrafica getAnagrafica(Long id);
	public Optional<Long> getAnagraficaIdWithCodiceFiscaleForProvider(String codiceFiscale, Long providerId);
	public void save(Anagrafica anagrafica);
	public void delete(Long id);
}
