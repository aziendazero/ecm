package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Provider;
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
	public void saveProviderRegistrationWrapper(ProviderRegistrationWrapper providerWrapper);
	
	public Long getAccountIdForProvider(Long providerId);
	public boolean canInsertPianoFormativo(Long providerId);
}
