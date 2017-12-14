package it.tredi.ecm.dao.repository;

import java.time.LocalDate;
import java.util.Set;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.enumlist.ContenutiEventoEnum;
import it.tredi.ecm.dao.enumlist.EventoStatoEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;

@JaversSpringDataAuditable
public interface EventoRepository extends JpaRepository<Evento, Long> {
	public Set<Evento> findAllByProviderId(Long providerId);
	public Set<Evento> findAllByProviderIdOrderByDataUltimaModificaDesc(Long providerId);

	public Set<Evento> findAllByProviderIdAndStatoNotAndDataInizioBefore(Long providerId, EventoStatoEnum stato, LocalDate now);

	@Query("SELECT e.edizione FROM Evento e WHERE e.prefix = :prefix ORDER BY e.edizione DESC")
	public Page<Integer> findLastEdizioneOfEventoByPrefix(@Param("prefix") String prefix, Pageable pageable);

	public Set<Evento> findAllByProviderIdAndDataFineBetween(Long providerId, LocalDate start, LocalDate end);
	public Set<Evento> findAllByProviderIdAndDataFineBetweenAndStato(Long providerId, LocalDate start, LocalDate end, EventoStatoEnum stato);
	public Set<Evento> findAllByProviderIdAndDataFineBetweenAndStatoNot(Long providerId, LocalDate start, LocalDate end, EventoStatoEnum stato);

	@Query("SELECT e FROM Evento e WHERE e.id = :id")
	@EntityGraph(value = "graph.evento.forRiedizione", type = EntityGraphType.FETCH)
	public Evento findOneForRiedizione(@Param("id") Long id);

	public Set<Evento> findAllByProviderIdAndDataScadenzaPagamentoBetweenAndPagatoFalseAndStatoNot(Long providerId, LocalDate start, LocalDate end, EventoStatoEnum cancellato);
	public Set<Evento> findAllByProviderIdAndDataScadenzaInvioRendicontazioneBetweenAndStatoNot(Long providerId, LocalDate start, LocalDate end, EventoStatoEnum cancellato);
	public Set<Evento> findAllByProviderIdAndDataScadenzaPagamentoBeforeAndPagatoFalse(Long providerId, LocalDate now);
	public Set<Evento> findAllByProviderIdAndStato(Long id, EventoStatoEnum stato);
	public Integer countAllByProviderIdAndStato(Long id, EventoStatoEnum stato);

	public Set<Evento> findAllByConfermatiCreditiFalseAndStato(EventoStatoEnum stato);
	public Integer countAllByConfermatiCreditiFalseAndStato(EventoStatoEnum stato);
	public Evento findOneByPrefix(String prefix);
	public Evento findOneByPrefixAndEdizione(String prefix, int edizione);

	public Integer countAllByContenutiEventoOrObiettivoNazionale(ContenutiEventoEnum medicineNonConvenzionale, Obiettivo nonConvenzionale);
	public Set<Evento> findAllByContenutiEventoOrObiettivoNazionale(ContenutiEventoEnum medicineNonConvenzionale, Obiettivo nonConvenzionale);
	public Integer countAllByContenutiEvento(ContenutiEventoEnum alimentazionePrimaInfanzia);
	public Set<Evento> findAllByContenutiEvento(ContenutiEventoEnum alimentazionePrimaInfanzia);

	//MEV RIEDIZIONI 04/2017
	public Set<Evento> findAllByProviderIdAndStatoNotAndStatoNotAndProceduraFormativaInAndDataFineAfter(Long providerId, EventoStatoEnum bozza, EventoStatoEnum cancellato, Set<ProceduraFormativa> procedureFormative, LocalDate fineAnnoScorso);

	@Query("SELECT COUNT (e) FROM Evento e WHERE e.eventoPadre.id = :id AND e.stato <> 'CANCELLATO'")
	public int countRiedizioniOfEventoId(@Param("id") Long id);
	@Query("SELECT e FROM Evento e WHERE e.eventoPadre.id = :id AND e.stato <> 'CANCELLATO'")
	public Set<Evento> getRiedizioniOfEventoId(@Param("id") Long id);
}
