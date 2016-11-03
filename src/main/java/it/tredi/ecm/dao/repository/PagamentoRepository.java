package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Pagamento;
import it.tredi.ecm.dao.entity.Provider;

public interface PagamentoRepository extends CrudRepository<Pagamento, Long> {

	public Pagamento findOneByProviderIdAndAnnoPagamento(Long providerId, Integer annoPagamento);
	
	public Pagamento findOneByQuotaAnnualeId(Long quotaAnnualeId);

//	@Query("SELECT distinct p FROM Provider p WHERE p.status IN ('ACCREDITATO_PROVVISORIAMENTE','ACCREDITATO_STANDARD') and p.id NOT IN (SELECT distinct pag.provider.id FROM Pagamento pag WHERE pag.annoPagamento = :annoPagamento) and p.codiceEsito not in ('PAA_ESEGUITO')")
//	public Set<Provider> findAllProviderNotPagamentoEffettuato(@Param("annoPagamento")Integer annoPagamento);

//	@Query("SELECT p FROM Pagamento p WHERE p.provider.pagato = false AND p.provider.pagInCorso = true")
//	public Set<Pagamento> getPagamentiProviderDaVerificare();
	
//	public Set<Pagamento> findAllByProviderId(Long providerId);
	
	public Set<Pagamento> findAll();
	
	@Query("SELECT p FROM Pagamento p WHERE p.evento = :evento")
	public Pagamento getPagamentoByEvento(@Param("evento")Evento evento);

	@Query("SELECT p FROM Pagamento p WHERE p.evento.pagato = false AND p.evento.pagInCorso = true")
	public Set<Pagamento> getPagamentiEventiDaVerificare();
	
}


// and p.codiceEsito not in ('PAA_ESEGUITO', 'PAA_PAGAMENTO_ANNULLATO', 'PAA_PAGAMENTO_SCADUTO', 'PAA_ENTE_NON_VALIDO', 'PAA_ID_SESSION_NON_VALIDO')")

