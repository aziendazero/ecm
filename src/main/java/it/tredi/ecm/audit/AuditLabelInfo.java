package it.tredi.ecm.audit;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.GlobalId;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tredi.ecm.dao.entity.Account;

public class AuditLabelInfo {
	private static Logger LOGGER = LoggerFactory.getLogger(AuditLabelInfo.class);

	private String propertyLabel;
	//private IdentifierTypeEnum identifierType;
	private String objectIdentifier;

	public String getPropertyLabel() {
		return propertyLabel;
	}
	public void setPropertyLabel(String propertyLabel) {
		this.propertyLabel = propertyLabel;
	}

//	public IdentifierTypeEnum getIdentifierType() {
//		return identifierType;
//	}
//	public void setIdentifierType(IdentifierTypeEnum identifierType) {
//		this.identifierType = identifierType;
//	}

	public String getObjectIdentifier() {
		return objectIdentifier;
	}
	public void setObjectIdentifier(String objectIdentifier) {
		this.objectIdentifier = objectIdentifier;
	}


}
