package it.tredi.ecm.dao.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.enumlist.ContenutiEventoEnum;
import it.tredi.ecm.dao.enumlist.EventoStatoEnum;
import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;

@JaversSpringDataAuditable
public interface EventoRepository extends JpaRepository<Evento, Long> {
	public Set<Evento> findAllByProviderId(Long providerId);

	public Page<Evento> findAllByProviderId(Long providerId, Pageable pageable);
	public Page<Evento> findAllByProviderIdAndDataUltimaModificaAfter(Long providerId, LocalDateTime ultimaModifica, Pageable pageable);
	public Page<Evento> findAllByDataUltimaModificaAfter(LocalDateTime ultimaModifica, Pageable pageable);

	public Set<Evento> findAllByProviderIdOrderByDataUltimaModificaDesc(Long providerId);

	public Set<Evento> findAllByProviderIdAndStatoNotAndDataInizioBefore(Long providerId, EventoStatoEnum stato, LocalDate now);

	@Query("SELECT e.edizione FROM Evento e WHERE e.prefix = :prefix ORDER BY e.edizione DESC")
	public Page<Integer> findLastEdizioneOfEventoByPrefix(@Param("prefix") String prefix, Pageable pageable);

	public Set<Evento> findAllByProviderIdAndDataFineBetween(Long providerId, LocalDate start, LocalDate end);
	public Set<Evento> findAllByProviderIdAndDataFineBetweenAndStato(Long providerId, LocalDate start, LocalDate end, EventoStatoEnum stato);
	public Set<Evento> findAllByProviderIdAndDataFineBetweenAndStatoAndEventoPadreNull(Long providerId, LocalDate start, LocalDate end, EventoStatoEnum stato);
	public Set<Evento> findAllByProviderIdAndDataFineBetweenAndStatoNot(Long providerId, LocalDate start, LocalDate end, EventoStatoEnum stato);

	@Query("SELECT e FROM Evento e WHERE e.id = :id")
	@EntityGraph(value = "graph.evento.forRiedizione", type = EntityGraphType.FETCH)
	public Evento findOneForRiedizione(@Param("id") Long id);

	public Set<Evento> findAllByProviderIdAndDataScadenzaPagamentoBetweenAndPagatoFalseAndStatoNotAndEventoNoEcmFalse(Long providerId, LocalDate start, LocalDate end, EventoStatoEnum cancellato);
	public Set<Evento> findAllByProviderIdAndDataScadenzaInvioRendicontazioneBetweenAndStatoAndEventoNoEcmFalse(Long providerId, LocalDate start, LocalDate end, EventoStatoEnum validato);
	public Set<Evento> findAllByProviderIdAndPagatoFalseAndDataScadenzaPagamentoBeforeAndStatoNotAndEventoNoEcmFalse(Long providerId, LocalDate now, EventoStatoEnum cancellato);
	public Set<Evento> findAllByProviderIdAndDataScadenzaInvioRendicontazioneBeforeAndStatoAndEventoNoEcmFalse(Long providerId, LocalDate now, EventoStatoEnum validato);
	public Set<Evento> findAllByProviderIdAndStato(Long id, EventoStatoEnum stato);
	public Integer countAllByProviderIdAndStato(Long id, EventoStatoEnum stato);

	public Set<Evento> findAllByConfermatiCreditiFalseAndStato(EventoStatoEnum stato);
	public Integer countAllByConfermatiCreditiFalseAndStato(EventoStatoEnum stato);
	public Evento findOneByPrefix(String prefix);
	public Evento findOneByPrefixAndEdizione(String prefix, int edizione);

	//public Integer countAllByArchiviatoMedicinaliFalseAndContenutiEventoOrObiettivoNazionale(ContenutiEventoEnum medicineNonConvenzionale, Obiettivo nonConvenzionale);
	//public Set<Evento> findAllByArchiviatoMedicinaliFalseAndContenutiEventoOrObiettivoNazionale(ContenutiEventoEnum medicineNonConvenzionale, Obiettivo nonConvenzionale);
	public Integer countAllByContenutiEventoAndArchivatoPrimaInfanziaFalse(ContenutiEventoEnum alimentazionePrimaInfanzia);
	public Set<Evento> findAllByContenutiEventoAndArchivatoPrimaInfanziaFalse(ContenutiEventoEnum alimentazionePrimaInfanzia);

	//MEV RIEDIZIONI 04/2017
	public Set<Evento> findAllByProviderIdAndStatoNotAndStatoNotAndProceduraFormativaInAndDataFineAfter(Long providerId, EventoStatoEnum bozza, EventoStatoEnum cancellato, Set<ProceduraFormativa> procedureFormative, LocalDate fineAnnoScorso);

	@Query("Select e FROM Evento e WHERE e.archiviatoMedicinali <> true AND (e.contenutiEvento = :medicineNonConvenzionale OR e.obiettivoNazionale = :nonConvenzionale)")
	public Set<Evento> findAllByArchiviatoMedicinaliFalseAndContenutiEventoOrObiettivoNazionale(@Param("medicineNonConvenzionale") ContenutiEventoEnum medicineNonConvenzionale, @Param("nonConvenzionale") Obiettivo nonConvenzionale);

	@Query("Select COUNT (e) FROM Evento e WHERE e.archiviatoMedicinali <> true AND (e.contenutiEvento = :medicineNonConvenzionale OR e.obiettivoNazionale = :nonConvenzionale)")
	public int countAllByArchiviatoMedicinaliFalseAndContenutiEventoOrObiettivoNazionale(@Param("medicineNonConvenzionale") ContenutiEventoEnum medicineNonConvenzionale, @Param("nonConvenzionale") Obiettivo nonConvenzionale);

	//RIEDIZIONI CON VERSIONE EVENTO
	public Set<Evento> findAllByProviderIdAndStatoNotAndStatoNotAndProceduraFormativaInAndDataFineAfterAndVersioneIn(Long providerId, EventoStatoEnum bozza, EventoStatoEnum cancellato, Set<ProceduraFormativa> procedureFormative, LocalDate fineAnnoScorso, Set<EventoVersioneEnum> versione);

	@Query("SELECT COUNT (e) FROM Evento e WHERE e.eventoPadre.id = :id AND e.stato <> 'CANCELLATO'")
	public int countRiedizioniOfEventoId(@Param("id") Long id);
	@Query("SELECT e FROM Evento e WHERE e.eventoPadre.id = :id AND e.stato <> 'CANCELLATO'")
	public Set<Evento> getRiedizioniOfEventoId(@Param("id") Long id);

	// ERM014776
	public Set<Evento> findAllByProviderIdAndDataInizioAfter(Long providerId, LocalDate dataCut);

//	@Query(
//			value = "select count(*) from ecmdb.evento e where e.id IN (select p.evento_res_id FROM ecmdb.programma_giornalierores p where p.id IN (select d.programma_giornaliero_res_id FROM ecmdb.dettaglio_attivitares d where d.condivisione_esiti_valutazione = true))",
//			nativeQuery = true)

	@Query("SELECT count(e) FROM Evento e JOIN e.programma p JOIN p.programma d WHERE d.condivisioneEsitiValutazione = TRUE")
	public Integer countAllEventiCondivisioneEsitiValutazione();
	@Query("SELECT e FROM Evento e JOIN e.programma p JOIN p.programma d WHERE d.condivisioneEsitiValutazione = TRUE")
	public Set<Evento> findAllEventiCondivisioneEsitiValutazione();
}
