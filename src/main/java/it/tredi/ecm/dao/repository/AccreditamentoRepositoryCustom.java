package it.tredi.ecm.dao.repository;

import java.util.Set;

import it.tredi.ecm.dao.entity.Accreditamento;

public interface AccreditamentoRepositoryCustom {
	public Set<Accreditamento> findAllAccreditamentiInScadenzaNeiProssimiGiorni(int giorni);
	public long countAllAccreditamentiInScadenzaNeiProssimiGiorni(int giorni);
}
