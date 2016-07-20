package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Provider;

public interface PagamentoService {
	public boolean providerIsPagamentoEffettuato(Long providerId, Integer annoRiferimento);
	public Set<Provider> getAllProviderNotPagamentoEffettuato(Integer annoRiferimento);
}
