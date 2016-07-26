package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.FieldEditabile;

public interface FieldEditabileService {
	public Set<FieldEditabile> getAllFieldEditabileForAccreditamento(Long accreditamentoId);
	public Set<FieldEditabile> getAllFieldEditabileForAccreditamentoAndObject(Long accreditamentoId, Long objectReference);
}
