package it.tredi.ecm.service;

import java.util.List;
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
	
	public List<Integer> getIdEditabili(Long accreditamentoId);
	public void removeIdEditabili(Long accrediatementoId, List<Integer> idEditabiliToRemove);
	public void addIdEditabili(Long accrediatementoId, List<Integer> idEditabiliToAdd);
	public void inviaDomandaAccreditamento(Long accreditamentoId);
}
