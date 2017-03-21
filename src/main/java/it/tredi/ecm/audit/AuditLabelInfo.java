package it.tredi.ecm.audit;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.GlobalId;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AuditLabelInfo {
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

	@Override
	public String toString() {
		return "AuditLabelInfo [propertyLabel: " + propertyLabel + "; objectIdentifier: " + objectIdentifier + "]";
	}

}
