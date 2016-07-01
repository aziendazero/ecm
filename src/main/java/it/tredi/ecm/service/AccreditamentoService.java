package it.tredi.ecm.service;

import java.util.List;
import java.util.Set;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.enumlist.AccreditamentoEnum;

public interface AccreditamentoService{
	public Accreditamento getNewAccreditamentoForCurrentProvider() throws Exception;
	public Accreditamento getNewAccreditamentoForProvider(Long providerId) throws Exception;
	public Accreditamento getAccreditamento(Long id);
	
	public Set<Accreditamento> getAllAccreditamentiForProvider(Long providerId);
	public Set<Accreditamento> getAccreditamentiAvviatiForProvider(Long providerId, AccreditamentoEnum tipoTomanda);
	public Accreditamento getAccreditamentoAttivoForProvider(Long providerId);
	public void save(Accreditamento accreditamento);
	
	public boolean canProviderCreateAccreditamento(Long providerId);
	
	public List<Integer> getIdEditabili(Long accreditamentoId);
	public void removeIdEditabili(Long accrediatementoId, List<Integer> idEditabiliToRemove);
	public void addIdEditabili(Long accrediatementoId, List<Integer> idEditabiliToAdd);
	public void inviaDomandaAccreditamento(Long accreditamentoId);
	public void inserisciPianoFormativo(Long accreditamentoId);
	
	public DatiAccreditamento getDatiAccreditamentoForAccreditamento(Long accreditamentoId) throws Exception;
}
