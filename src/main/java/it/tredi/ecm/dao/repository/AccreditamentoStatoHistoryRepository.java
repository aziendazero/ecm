package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.AccreditamentoStatoHistory;

public interface AccreditamentoStatoHistoryRepository extends CrudRepository<AccreditamentoStatoHistory, Long> {
	public Set<AccreditamentoStatoHistory> findAllByAccreditamentoIdOrderByDataFine(Long accreditamentoId);
	public Set<AccreditamentoStatoHistory> findAllByAccreditamentoIdAndProcessInstanceIdOrderByDataFine(Long accreditamentoId, Long processInstanceId);

}
