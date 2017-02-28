package it.tredi.ecm.service;

import java.time.LocalDateTime;
import java.util.Set;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.AccreditamentoStatoHistory;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;

public interface AccreditamentoStatoHistoryService {
    public Set<AccreditamentoStatoHistory> getAllByAccreditamentoId(Long accreditamentoId);
    public Set<AccreditamentoStatoHistory> getAllByAccreditamentoIdAndProcessInstanceId(Long accreditamentoId, Long processInstanceId);
	public void createHistoryFine(Accreditamento accreditamento, Long processInstanceId, AccreditamentoStatoEnum stato, AccreditamentoStatoEnum prevStato, LocalDateTime dataFine, boolean presaVisione);
	public Set<AccreditamentoStatoHistory> getAllByAccreditamentoIdAndProcessInstanceIdIn(Long id, Set<Long> allWorkflowProcessInstanceIdVariazioneDati);
}
