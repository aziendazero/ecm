package it.tredi.ecm.audit;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditInfo {
	private static Logger LOGGER = LoggerFactory.getLogger(AuditInfo.class);

	private Long entityId;
	private Class entityClass;

	private List<AuditPropertyChangeInfo> auditProperties = new ArrayList<AuditPropertyChangeInfo>();

	private String fullText;

	public AuditInfo(Long entityId, Class entityClass){
		this.entityId = entityId;
		this.entityClass = entityClass;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public Class getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class entityClass) {
		this.entityClass = entityClass;
	}

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	public List<AuditPropertyChangeInfo> getAuditProperties() {
		return auditProperties;
	}

	public void setAuditProperties(List<AuditPropertyChangeInfo> auditProperties) {
		this.auditProperties = auditProperties;
	}


}
