package it.tredi.ecm.service;

import java.math.BigDecimal;
import java.util.Set;

import it.tredi.ecm.dao.entity.Pagamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.service.bean.ProviderRegistrationWrapper;

public interface ProviderService {
	public Provider getProvider();
	public Provider getProvider(Long id);
	public Provider getProviderByCodiceFiscale(String codiceFiscale);
	public Provider getProviderByPartitaIva(String partitaIva);
	public Set<Provider> getAll();
	public void save(Provider provider);

	public Set<String> getFileTypeUploadedByProviderId(Long id);

	public ProviderRegistrationWrapper getProviderRegistrationWrapper();
	public void saveProviderRegistrationWrapper(ProviderRegistrationWrapper providerWrapper) throws Exception;

	public Long getProviderIdByAccountId(Long accountId);
	public boolean canInsertPianoFormativo(Long providerId);
	public boolean canInsertEvento(Long providerId);
	public boolean canInsertAccreditamentoStandard(Long providerId);
	public boolean hasAlreadySedeLegaleProvider(Provider provider, Sede sede);
	
	public void saveFromIntegrazione(Provider provider);
}
