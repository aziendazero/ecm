package it.tredi.ecm.dao.repository;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.enumlist.EventoStatoEnum;

public interface EventoRepository extends JpaRepository<Evento, Long> {
	public Set<Evento> findAllByProviderId(Long providerId);

	public Set<Evento> findAllByProviderIdAndStatoNotAndDataInizioBefore(Long providerId, EventoStatoEnum stato, LocalDate now);
}
