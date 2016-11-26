package it.tredi.ecm.dao.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.AnagrafeRegionaleCrediti;

public interface AnagrafeRegionaleCreditiRepository extends CrudRepository<AnagrafeRegionaleCrediti, Long> {
	@Query("SELECT min(a.data) FROM AnagrafeRegionaleCrediti a")
	public LocalDate getMinData();

	@Query("SELECT DISTINCT a.codiceFiscale, a.cognome, a.nome FROM AnagrafeRegionaleCrediti a WHERE (a.data BETWEEN :dataInizio AND :dataFine)")
	public List<Object[]> findDistinctAll(@Param("dataInizio") LocalDate dataInizio, @Param("dataFine") LocalDate dataFine);

	@Query("SELECT DISTINCT a FROM AnagrafeRegionaleCrediti a WHERE a.codiceFiscale = :codiceFiscale AND (a.data BETWEEN :dataInizio AND :dataFine)")
	public Set<AnagrafeRegionaleCrediti> findDistinctAllByCodiceFiscale(@Param("codiceFiscale") String codiceFiscale, @Param("dataInizio") LocalDate dataInizio, @Param("dataFine") LocalDate dataFine);

	@Query("SELECT sum(a.crediti) FROM AnagrafeRegionaleCrediti a WHERE a.codiceFiscale = :codiceFiscale AND (a.data BETWEEN :dataInizio AND :dataFine)")
	public BigDecimal getSumCreditiByCodiceFiscale(@Param("codiceFiscale") String codiceFiscale, @Param("dataInizio") LocalDate dataInizio, @Param("dataFine") LocalDate dataFine);

	/* Relazione annuale elenco ruoli per contare numero di partecipanti che hanno avuto crediti */
	@Query("SELECT a.ruolo, count(DISTINCT a.codiceFiscale) from AnagrafeRegionaleCrediti a JOIN a.evento e WHERE e.provider.id = :providerId AND (a.data BETWEEN :dataInizio AND :dataFine) GROUP BY a.ruolo")
	public List<Object[]> getRuoliAventeCreditiPerAnno(@Param("providerId") Long providerId, @Param("dataInizio") LocalDate dataInizio, @Param("dataFine") LocalDate dataFine);

	/* Relazione annuale numero di professioni (con discipline selezionate) che hanno avuto crediti */
	@Query("SELECT COUNT(DISTINCT d.professione) from AnagrafeRegionaleCrediti a JOIN a.evento e JOIN e.discipline d WHERE e.provider.id = :providerId AND (a.data BETWEEN :dataInizio AND :dataFine)")
	public int getProfessioniAnagrafeAventeCrediti(@Param("providerId") Long providerId, @Param("dataInizio") LocalDate dataInizio, @Param("dataFine") LocalDate dataFine);

//	@Query("SELECT a.evento from AnagrafeRegionaleCrediti a JOIN a.evento e WHERE e.provider.id = :providerId AND (a.data BETWEEN :dataInizio AND :dataFine)")
//	public Set<Evento> getEventiAnagrafeAventeCrediti(@Param("providerId") Long providerId, @Param("dataInizio") LocalDate dataInizio, @Param("dataFine") LocalDate dataFine);
}
