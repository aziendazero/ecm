package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.FieldEditabile;


public interface FieldEditabileRepository extends CrudRepository<FieldEditabile, Long> {
	public Set<FieldEditabile> findAllByAccreditamentoId(Long accreditamentoId);
	public Set<FieldEditabile> findAllByAccreditamentoIdAndObjectReference(Long accreditamentoId, Long objectReference);
}
