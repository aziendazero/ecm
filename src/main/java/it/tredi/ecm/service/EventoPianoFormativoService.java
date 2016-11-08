package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.EventoPianoFormativo;
import it.tredi.ecm.dao.entity.File;

public interface EventoPianoFormativoService {
	public EventoPianoFormativo getEvento(Long id);
	public Set<EventoPianoFormativo> getAllEventiFromProvider(Long providerId);
	public Set<EventoPianoFormativo> getAllEventiFromProviderInPianoFormativo(Long providerId, Integer pianoFormativo);
	public void save(EventoPianoFormativo evento) throws Exception;
	public void delete(Long id);

	public void buildPrefix(EventoPianoFormativo evento) throws Exception;
	public void validaRendiconto(File rendiconto) throws Exception;
	public Set<EventoPianoFormativo> getAllEventiAttuabiliForProviderId(Long providerId);
}
