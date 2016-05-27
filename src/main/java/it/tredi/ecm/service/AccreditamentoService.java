package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Accreditamento;

public interface AccreditamentoService{
	public Accreditamento getNewAccreditamentoForCurrentProvider() throws Exception;
	public Accreditamento getAccreditamento(Long id);
	public Accreditamento getAccreditamento();
	
	public Set<Accreditamento> getAllAccreditamentiForProvider(Long providerId);
	public Set<Accreditamento> getAccreditamentiAttviForProvider(Long providerId, String tipoTomanda);
	public void save(Accreditamento accreditamento);
	
	public boolean canProviderCreateAccreditamento(Long providerId);
}
