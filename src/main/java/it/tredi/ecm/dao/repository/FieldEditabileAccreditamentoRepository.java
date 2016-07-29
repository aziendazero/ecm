package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.FieldEditabileAccreditamento;


public interface FieldEditabileAccreditamentoRepository extends CrudRepository<FieldEditabileAccreditamento, Long> {
	public Set<FieldEditabileAccreditamento> findAllByAccreditamentoId(Long accreditamentoId);
	public Set<FieldEditabileAccreditamento> findAllByAccreditamentoIdAndObjectReference(Long accreditamentoId, Long objectReference);
	public void deleteAllByAccreditamentoId(Long accreditamentoId);
}
