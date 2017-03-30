package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.AccreditamentoStatoHistory;

public interface AccreditamentoStatoHistoryRepository extends CrudRepository<AccreditamentoStatoHistory, Long> {
	public Set<AccreditamentoStatoHistory> findAllByAccreditamentoIdOrderByDataInizio(Long accreditamentoId);
	public Set<AccreditamentoStatoHistory> findAllByAccreditamentoIdAndProcessInstanceIdOrderByDataInizio(Long accreditamentoId, Long processInstanceId);
	public Set<AccreditamentoStatoHistory> findAllByAccreditamentoIdAndProcessInstanceIdInOrderByDataInizio(Long accreditamentoId, Set<Long> processInstanceIdList);
}
