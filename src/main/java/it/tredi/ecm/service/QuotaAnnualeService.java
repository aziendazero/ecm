package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Pagamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.QuotaAnnuale;
import it.tredi.ecm.utils.Utils;

public interface QuotaAnnualeService {
	/* Pagamenti Quote Provider */
	public QuotaAnnuale createPagamentoProviderPerQuotaAnnuale(Long providerId, Integer annoRiferimento, boolean primoAnno);
	public String pagaQuotaAnnualeForProvider(Long quotaAnnualeId, String backURL) throws Exception;
	
	public Set<QuotaAnnuale> getAllQuotaAnnuale();
	public Set<QuotaAnnuale> getAllQuotaAnnualeByProviderId(Long providerId);
	
	public Set<Pagamento> getPagamentiProviderDaVerificare();
	public Set<Provider> getAllProviderNotPagamentoEffettuato(Integer annoRiferimento);
	
	public void save(QuotaAnnuale quotaAnnuale);
}
