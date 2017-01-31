package it.tredi.ecm.service;

import java.util.List;
import it.tredi.ecm.audit.AuditLabelInfo;


public interface AuditService {
	String getLabelForAuditProperty(List<AuditLabelInfo> auditLabelInfos, String propertyLabel);
}
