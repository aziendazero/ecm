package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Accreditamento;

public interface AccreditamentoService{
	public Set<Accreditamento> getAllAccreditamentiForProvider(Long id);
	public void save(Accreditamento accreditamento);
}
