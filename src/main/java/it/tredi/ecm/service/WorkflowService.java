package it.tredi.ecm.service;

import java.util.List;

import it.tredi.bonita.api.model.TaskInstanceDataModel;
import it.tredi.bonita.api.model.UserDataModel;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.WorkflowInfo;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.service.bean.ProcessInstanceDataModelComplete;
import it.tredi.ecm.utils.Utils;

public interface WorkflowService {
	public boolean isCreateAccountOnLogin();
	public UserDataModel getUserByLogin(String userName) throws Exception;
	public void saveOrUpdateBonitaUserByAccount(Account account) throws Exception;
	public WorkflowInfo createWorkflowAccreditamentoProvvisorio(CurrentUser user, Accreditamento accreditamento) throws Exception;
	public ProcessInstanceDataModelComplete getProcessInstanceDataModelComplete(long processInstanceId, UserDataModel user) throws Exception;
	public List<AccreditamentoStatoEnum> getInserimentoEsitoOdgStatiPossibiliAccreditamento(long processInstanceId) throws Exception;
	public TaskInstanceDataModel currentUserGetTaskForState(Accreditamento accreditamento) throws Exception;
	public TaskInstanceDataModel userGetTaskForState(CurrentUser user, Accreditamento accreditamento) throws Exception;
	public void eseguiTaskValutazioneAssegnazioneCrecmForCurrentUser(Accreditamento accreditamento, List<String> usernameWorkflowValutatoriCrecm) throws Exception;
	public void eseguiTaskValutazioneAssegnazioneCrecmForUser(CurrentUser user, Accreditamento accreditamento, List<String> usernameWorkflowValutatoriCrecm) throws Exception;
	
	public void eseguiTaskValutazioneCrecmForCurrentUser(Accreditamento accreditamento) throws Exception;
	public void eseguiTaskValutazioneCrecmForUser(CurrentUser user, Accreditamento accreditamento) throws Exception;
}
