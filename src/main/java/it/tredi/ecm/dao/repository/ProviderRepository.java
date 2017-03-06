package it.tredi.ecm.dao.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;

@Repository
public interface ProviderRepository extends CrudRepository<Provider, Long> {
	//TODO @EntityGraph al momento non funziona...sarebbe utile investigare per capire perchè
	//@EntityGraph(value = "graph.provider.files", type = EntityGraphType.FETCH)
	@EntityGraph(value = "graph.provider.minimal", type = EntityGraphType.FETCH)
	public Provider findOne(Long id);

	@Query("SELECT a.provider FROM Account a JOIN a.provider p WHERE a.id = :accountId")
	public Provider getProviderByAccountId(@Param("accountId") Long accountId);

	public Provider findOneByCodiceFiscale(String codiceFiscale);
	public Provider findOneByPartitaIva(String partitaIva);
	public Set<Provider> findAll();

	//@Query("SELECT p FROM Provider p WHERE p.status <> :status")
	public Set<Provider> findAllByStatusNot(@Param("status") ProviderStatoEnum status);

	public Set<Provider> findAllByStatusIn(List<ProviderStatoEnum> stati);

	@Query("SELECT a.provider.id FROM Account a JOIN a.provider p WHERE a.id = :accountId")
	public Long getIdByAccountId(@Param("accountId") Long accountId);

//	@Query("SELECT p.canInsertPianoFormativo FROM Provider p WHERE p.id = :providerId")
//	public boolean canInsertPianoFormativo(@Param("providerId")Long providerId);
//	@Query("SELECT p.canInsertAccreditamentoStandard FROM Provider p WHERE p.id = :providerId")
//	public boolean canInsertAccreditamentoStandard(@Param("providerId")Long providerId);
//	@Query("SELECT p.canInsertEvento FROM Provider p WHERE p.id = :providerId")
//	public boolean canInsertEvento(@Param("providerId")Long providerId);
//	@Query("SELECT p.canInsertRelazioneAnnuale FROM Provider p WHERE p.id = :providerId")
//	public boolean canInsertRelazioneAnnuale(@Param("providerId")Long providerId);

	@Query("SELECT p FROM Provider p WHERE p.status IN :statiAccreditati AND (p.dataScadenzaInsertPianoFormativo is null OR (p.dataScadenzaInsertPianoFormativo <> :defaultDate AND p.dataScadenzaInsertPianoFormativo < :today))")
	public Set<Provider> findAllProviderToUpdateDataPianoFormativo(@Param("statiAccreditati")Set<ProviderStatoEnum> statiProvider, @Param("defaultDate")LocalDate defaultDate, @Param("today")LocalDate today);

	@Query("SELECT p FROM Provider p WHERE p.status IN :statiAccreditati AND (p.dataScadenzaInsertAccreditamentoStandard is not null AND p.dataScadenzaInsertAccreditamentoStandard < :today)")
	public Set<Provider> findAllProviderToUpdateDataDomandaStandard(@Param("statiAccreditati")Set<ProviderStatoEnum> statiProvider, @Param("today")LocalDate today);

	@Query("SELECT p FROM Provider p WHERE p.status IN :statiAccreditati AND (p.dataScadenzaInsertRelazioneAnnuale is null OR (p.dataScadenzaInsertRelazioneAnnuale <> :defaultDate AND p.dataScadenzaInsertRelazioneAnnuale < :today))")
	public Set<Provider> findAllProviderToUpdateDataRelazioneAnnuale(@Param("statiAccreditati")Set<ProviderStatoEnum> statiProvider, @Param("defaultDate")LocalDate defaultDate, @Param("today")LocalDate today);

	@Query("SELECT COUNT (p) FROM Provider p WHERE p.canInsertAccreditamentoStandard IS false AND p.inviatoAccreditamentoStandard IS false")
	public int countAllProviderInadempienti();
	@Query("SELECT p FROM Provider p WHERE p.canInsertAccreditamentoStandard IS false AND p.inviatoAccreditamentoStandard IS false")
	public Set<Provider> getAllProviderInadempienti();
}
