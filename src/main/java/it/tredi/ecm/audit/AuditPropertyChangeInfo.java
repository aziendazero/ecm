package it.tredi.ecm.audit;

import java.util.ArrayList;
import java.util.List;

import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tredi.ecm.dao.entity.Account;

public class AuditPropertyChangeInfo {
	private static Logger LOGGER = LoggerFactory.getLogger(AuditPropertyChangeInfo.class);

	private AuditPropertyChangeInfoTypeEnum auditPropertyChangeInfoTypeEnum;
	private String userName;
	private Account account;
	private LocalDateTime dataModifica;
	private List<AuditLabelInfo> auditLabelInfos;
	private String propertyLabelInfo;
	private String label;
	private String propertyFullPath;

	private AuditObjectValueInfo previousAuditObjectInfo;
	private AuditObjectValueInfo afterAuditObjectInfo;
	//private String entity;
	//private Long previousId;
	//private Long afterId;
	private List<AuditCollectionChangeInfo> auditCollectionChangeInfo = new ArrayList<AuditCollectionChangeInfo>();

	//Per ora tenere
	private ValueChange valueChange;
	private ReferenceChange referenceChange;
	//private ReferenceChange referenceChange;

	AuditPropertyChangeInfo() {
		auditLabelInfos = new ArrayList<AuditLabelInfo>();
	}

	public String getExtraInfo() {
		String toRet = null;
		if(valueChange != null) {
			toRet = "value: " + valueChange.getAffectedGlobalId().value();
			toRet += "\n" + "typeName: " + valueChange.getAffectedGlobalId().getTypeName();
			toRet += "\n" + "propertyName: " + valueChange.getPropertyName();
		}
		for(AuditLabelInfo auditLabelInfo : auditLabelInfos) {
			toRet += "\n\t" + "auditLabelInfo: " + auditLabelInfo.getPropertyLabel() + ": " + auditLabelInfo.getObjectIdentifier();
		}

		return toRet;
	}


	//SETTER GETTER
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getPropertyLabelInfo() {
		return propertyLabelInfo;
	}

	public void setPropertyLabelInfo(String propertyLabelInfo) {
		this.propertyLabelInfo = propertyLabelInfo;
	}

	public List<AuditLabelInfo> getAuditLabelInfos() {
		return auditLabelInfos;
	}

	public void setAuditLabelInfos(List<AuditLabelInfo> auditLabelInfos) {
		this.auditLabelInfos = auditLabelInfos;
	}

	public ValueChange getValueChange() {
		return valueChange;
	}

	public void setValueChange(ValueChange valueChange) {
		this.valueChange = valueChange;
	}

	public LocalDateTime getDataModifica() {
		return dataModifica;
	}

	public void setDataModifica(LocalDateTime dataModifica) {
		this.dataModifica = dataModifica;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ReferenceChange getReferenceChange() {
		return referenceChange;
	}

	public void setReferenceChange(ReferenceChange referenceChange) {
		this.referenceChange = referenceChange;
	}

	public AuditPropertyChangeInfoTypeEnum getAuditPropertyChangeInfoTypeEnum() {
		return auditPropertyChangeInfoTypeEnum;
	}

	public void setAuditPropertyChangeInfoTypeEnum(AuditPropertyChangeInfoTypeEnum auditPropertyChangeInfoTypeEnum) {
		this.auditPropertyChangeInfoTypeEnum = auditPropertyChangeInfoTypeEnum;
	}

	public AuditObjectValueInfo getPreviousAuditObjectInfo() {
		return previousAuditObjectInfo;
	}

	public void setPreviousAuditObjectInfo(AuditObjectValueInfo previousAuditObjectInfo) {
		this.previousAuditObjectInfo = previousAuditObjectInfo;
	}

	public AuditObjectValueInfo getAfterAuditObjectInfo() {
		return afterAuditObjectInfo;
	}

	public void setAfterAuditObjectInfo(AuditObjectValueInfo afterAuditObjectInfo) {
		this.afterAuditObjectInfo = afterAuditObjectInfo;
	}

	public List<AuditCollectionChangeInfo> getAuditCollectionChangeInfo() {
		return auditCollectionChangeInfo;
	}

	public void setAuditCollectionChangeInfo(List<AuditCollectionChangeInfo> auditCollectionChangeInfo) {
		this.auditCollectionChangeInfo = auditCollectionChangeInfo;
	}

	public String getPropertyFullPath() {
		return propertyFullPath;
	}

	public void setPropertyFullPath(String propertyFullPath) {
		this.propertyFullPath = propertyFullPath;
	}

}
