package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.DatiAccreditamento;

public interface DatiAccreditamentoService {
	public DatiAccreditamento getDatiAccreditamento(Long id);
	public void save(DatiAccreditamento datiAccreditamento, Long accreditamentoId);
	public Set<String> getFileTypeUploadedByDatiAccreditamentoId(Long id);
}
