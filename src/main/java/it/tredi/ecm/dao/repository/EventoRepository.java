package it.tredi.ecm.dao.repository;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.enumlist.EventoStatoEnum;

public interface EventoRepository extends JpaRepository<Evento, Long> {
	public Set<Evento> findAllByProviderId(Long providerId);

	public Set<Evento> findAllByProviderIdAndStatoNotAndDataInizioBefore(Long providerId, EventoStatoEnum stato, LocalDate now);

	@Query("SELECT e.edizione FROM Evento e WHERE e.prefix = :prefix ORDER BY e.edizione DESC")
	public Page<Integer> findLastEdizioneOfEventoByPrefix(@Param("prefix") String prefix, Pageable pageable);
	
	@Query("SELECT e FROM Evento e WHERE e.id = :id")
	@EntityGraph(value = "graph.evento.forRiedizione", type = EntityGraphType.FETCH)
	public Evento findOneForRiedizione(@Param("id") Long id);
}
