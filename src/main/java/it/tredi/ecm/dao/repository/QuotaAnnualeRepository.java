package it.tredi.ecm.dao.repository;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Pagamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.QuotaAnnuale;

public interface QuotaAnnualeRepository extends CrudRepository<QuotaAnnuale, Long> {

	public QuotaAnnuale findOneByProviderIdAndAnnoRiferimento(Long providerId, Integer annoRiferimento);
	public Set<QuotaAnnuale> findAll();
	public Set<QuotaAnnuale> findAllByProviderId(Long providerId);
	
	@Query("SELECT p FROM Pagamento p WHERE p.id IN (SELECT qa.pagamento.id FROM QuotaAnnuale qa WHERE qa.pagato = false AND qa.pagInCorso = true)")
	public Set<Pagamento> getPagamentiProviderDaVerificare();
	
	@Query("SELECT p FROM Provider p WHERE p.id IN (SELECT distinct qa.provider.id FROM QuotaAnnuale qa WHERE qa.pagato = false AND qa.pagInCorso = false AND qa.pagamento.dataScadenzaPagamento < :now)")
	public Set<Provider> findAllProviderNotPagamentoEffettuatoAllaScadenza(@Param("now") LocalDate now);
	
	public int countByProviderIdAndPagatoFalse(Long providerId);
	
	@Query("SELECT p FROM Provider p WHERE p.status IN ('ACCREDITATO_PROVVISORIAMENTE','ACCREDITATO_STANDARD') AND p.id NOT IN (SELECT distinct qa.provider.id FROM QuotaAnnuale qa WHERE qa.annoRiferimento = :annoRiferimento)")
	public Set<Provider> findAllProviderNotPagamentoRegistrato(@Param("annoRiferimento")Integer annoRiferimento);
}

// and p.codiceEsito not in ('PAA_ESEGUITO', 'PAA_PAGAMENTO_ANNULLATO', 'PAA_PAGAMENTO_SCADUTO', 'PAA_ENTE_NON_VALIDO', 'PAA_ID_SESSION_NON_VALIDO')")

