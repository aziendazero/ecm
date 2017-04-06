package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Pagamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.QuotaAnnuale;

public interface QuotaAnnualeService {
	/* Pagamenti Quote Provider */
	public QuotaAnnuale createPagamentoProviderPerQuotaAnnuale(Long providerId, Integer annoRiferimento, boolean primoAnno);
	public String pagaQuotaAnnualeForProvider(Long quotaAnnualeId, String backURL) throws Exception;

	public Set<QuotaAnnuale> getAllQuotaAnnuale();
	public Set<QuotaAnnuale> getAllQuotaAnnualeByProviderId(Long providerId);

	public QuotaAnnuale getQuotaAnnualeForProviderIdAndAnnoRiferimento(Long providerId, Integer annoRiferimento);
	public Set<Pagamento> getPagamentiProviderDaVerificare();
	public boolean hasProviderPagamentiNonEffettuati(Long providerId);
	public Set<Provider> getAllProviderNotPagamentoRegistrato(Integer annoRiferimento);

	public void checkAndCreateQuoteAnnualiPerAnnoInCorso();

	public Set<Provider> getAllProviderNotPagamentoEffettuatoAllaScadenza();
	public int countProviderNotPagamentoEffettuatoAllaScadenza();

	public void save(QuotaAnnuale quotaAnnuale);
	public void salvaQuietanzaPagamento(File quietanzaPagamento, Long quotaAnnualeId);
}
