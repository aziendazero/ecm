package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.tredi.ecm.dao.entity.Provider;

@Repository
public interface ProviderRepository extends CrudRepository<Provider, Long> {
	//TODO @EntityGraph al momento non funziona...sarebbe utile investigare per capire perchè
	//@EntityGraph(value = "graph.provider.files", type = EntityGraphType.FETCH)
	@EntityGraph(value = "graph.provider.minimal", type = EntityGraphType.FETCH)
	public Provider findOne(Long id);
	
	public Provider findOneByAccountId(Long id);
	public Provider findOneByCodiceFiscale(String codiceFiscale);
	public Provider findOneByPartitaIva(String partitaIva);
	public Set<Provider> findAll();
	
	@Query("SELECT files.tipo From Provider p JOIN p.files files WHERE p.id = :id")
	public Set<String> findAllFileTipoByProviderId(@Param("id") Long id);

	@Query("SELECT p.account.id FROM Provider p WHERE p.id = :providerId")
	public Long getAccountIdById(@Param("providerId")Long providerId);
	
	@Query("SELECT p.canInsertPianoFormativo FROM Provider p WHERE p.id = :providerId")
	public boolean canInsertPianoFormativo(@Param("providerId")Long providerId);
	@Query("SELECT p.canInsertAccreditamentoStandard FROM Provider p WHERE p.id = :providerId")
	public boolean canInsertAccreditamentoStandard(@Param("providerId")Long providerId);
	@Query("SELECT p.canInsertEvento FROM Provider p WHERE p.id = :providerId")
	public boolean canInsertEvento(@Param("providerId")Long providerId);
}
