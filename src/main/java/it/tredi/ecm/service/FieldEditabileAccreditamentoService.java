package it.tredi.ecm.service;

import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.FieldEditabileAccreditamento;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;

public interface FieldEditabileAccreditamentoService {
	public Set<FieldEditabileAccreditamento> getAllFieldEditabileForAccreditamento(Long accreditamentoId);
	public Set<FieldEditabileAccreditamento> getAllFieldEditabileForAccreditamentoAndObject(Long accreditamentoId, Long objectReference);
	public Set<FieldEditabileAccreditamento> getFullLista(Long accreditamentoId,Long objectReference);
	public void insertFieldEditabileForAccreditamento(Long accreditamentoId, Long objectReference, SubSetFieldEnum subset, Set<IdFieldEnum> toInsert);
	public void insertFieldEditabileForAccreditamento(Long accreditamentoId, Long objectReference, SubSetFieldEnum subset, Set<IdFieldEnum> toInsert, Map<IdFieldEnum, String> mappaNoteFieldEditabileAccreditamento);
	public void removeFieldEditabileForAccreditamento(Long accreditamentoId, Long objectReference, SubSetFieldEnum subset);
	public void removeAllFieldEditabileForAccreditamento(Long accreditamentoId);
	public void delete(FieldEditabileAccreditamento field);
	public void update(FieldEditabileAccreditamento field);
}
