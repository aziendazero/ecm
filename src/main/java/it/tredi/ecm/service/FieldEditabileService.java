package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.FieldEditabile;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;

public interface FieldEditabileService {
	public Set<FieldEditabile> getAllFieldEditabileForAccreditamento(Long accreditamentoId);
	public Set<FieldEditabile> getAllFieldEditabileForAccreditamentoAndObject(Long accreditamentoId, Long objectReference);
	public void removeFieldEditabileForAccreditamento(Long accreditamentoId, Long objectReference, SubSetFieldEnum subset);
}
