package it.tredi.ecm.dao.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoPianoFormativo;

public interface EventoPianoFormativoRepository extends JpaRepository<EventoPianoFormativo, Long> {
	public Set<EventoPianoFormativo> findAllByProviderIdAndPianoFormativo(Long providerId, Integer pianoFormativo);
	public Set<EventoPianoFormativo> findAllByProviderId(Long providerId);
	public Set<EventoPianoFormativo> findAllByProviderIdAndPianoFormativoInAndAttuatoFalse(Long providerId, List<Integer> anni);
}
