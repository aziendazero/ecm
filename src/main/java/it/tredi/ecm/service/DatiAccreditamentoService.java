package it.tredi.ecm.service;

import it.tredi.ecm.dao.entity.DatiAccreditamento;

public interface DatiAccreditamentoService {
	public DatiAccreditamento getDatiAccreditamento(Long id);
	public void save(DatiAccreditamento datiAccreditamento, Long accreditamentoId);
}
