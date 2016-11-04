package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Pagamento;

public interface PagamentoService {
	
	public Set<Pagamento> getAllPagamenti();
	public Pagamento getPagamentoById(Long pagamentoId);
	public Pagamento getPagamentoByQuotaAnnualeId(Long quotaAnnualeId);
	public Pagamento getPagamentoByEvento(Evento evento);
	
	/* Pagamenti Eventi */
	public Set<Pagamento> getPagamentiEventiDaVerificare();
	
	public void save(Pagamento p);
	public void deleteAll(Iterable<Pagamento> pagamenti);
}
