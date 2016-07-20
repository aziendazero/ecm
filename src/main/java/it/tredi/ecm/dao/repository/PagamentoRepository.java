package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Pagamento;
import it.tredi.ecm.dao.entity.Provider;

public interface PagamentoRepository extends CrudRepository<Pagamento, Long> {
	
	public Pagamento findOneByProviderIdAndAnnoPagamento(Long providrId, Integer annoPagamento);
	
	@Query("SELECT distinct p FROM Provider p WHERE p.status IN ('ACCREDITATO_PROVVISORIAMENTE','ACCREDITATO_STANDARD') and p.id NOT IN (SELECT distinct pag.provider.id FROM Pagamento pag WHERE pag.annoPagamento = :annoPagamento)")
	public Set<Provider> findAllProviderNotPagamentoEffettuato(@Param("annoPagamento")Integer annoPagamento);
}
