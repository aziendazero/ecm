package it.tredi.ecm.service;

import java.util.List;
import it.tredi.ecm.audit.AuditLabelInfo;


public interface AuditService {
	public String getLabelForAuditProperty(List<AuditLabelInfo> auditLabelInfos, String propertyLabel);
	public void commitForCurrentUser(Object entity);
	public void deleteForCurrrentUser(Object entity);
	public void deleteByIdForCurrrentUser(Long entityId, Class javaClass);
}
