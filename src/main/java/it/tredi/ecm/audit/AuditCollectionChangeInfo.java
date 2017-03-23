package it.tredi.ecm.audit;

public class AuditCollectionChangeInfo {
	private AuditCollectionChangeTypeEnum auditMapChangeTypeEnum;
	private String key;
	private AuditObjectValueInfo previousAuditObjectInfo;
	private AuditObjectValueInfo afterAuditObjectInfo;

	public AuditCollectionChangeTypeEnum getAuditMapChangeTypeEnum() {
		return auditMapChangeTypeEnum;
	}

	public String getKey() {
		return key;
	}

	public AuditObjectValueInfo getPreviousAuditObjectInfo() {
		if(auditMapChangeTypeEnum == AuditCollectionChangeTypeEnum.CHANGED)
			return previousAuditObjectInfo;
		return null;
	}

	public AuditObjectValueInfo getAfterAuditObjectInfo() {
		if(auditMapChangeTypeEnum == AuditCollectionChangeTypeEnum.CHANGED)
			return afterAuditObjectInfo;
		return null;
	}

	public AuditObjectValueInfo getAddedAuditObjectInfo() {
		if(auditMapChangeTypeEnum == AuditCollectionChangeTypeEnum.ADDED)
			return afterAuditObjectInfo;
		return null;
	}

	public AuditObjectValueInfo getRemovedAuditObjectInfo() {
		if(auditMapChangeTypeEnum == AuditCollectionChangeTypeEnum.REMOVED)
			return previousAuditObjectInfo;
		return null;
	}

	static AuditCollectionChangeInfo createAddedAuditMapChangeInfo(String key, AuditObjectValueInfo added) {
		AuditCollectionChangeInfo toRet = new AuditCollectionChangeInfo();
		toRet.key = key;
		toRet.auditMapChangeTypeEnum = AuditCollectionChangeTypeEnum.ADDED;
		toRet.afterAuditObjectInfo = added;
		return toRet;
	}

	static AuditCollectionChangeInfo createRemovedAuditMapChangeInfo(String key, AuditObjectValueInfo removed) {
		AuditCollectionChangeInfo toRet = new AuditCollectionChangeInfo();
		toRet.key = key;
		toRet.auditMapChangeTypeEnum = AuditCollectionChangeTypeEnum.REMOVED;
		toRet.previousAuditObjectInfo = removed;
		return toRet;
	}

	static AuditCollectionChangeInfo createChangedAuditMapChangeInfo(String key, AuditObjectValueInfo previous, AuditObjectValueInfo after) {
		AuditCollectionChangeInfo toRet = new AuditCollectionChangeInfo();
		toRet.key = key;
		toRet.auditMapChangeTypeEnum = AuditCollectionChangeTypeEnum.CHANGED;
		toRet.previousAuditObjectInfo = previous;
		toRet.afterAuditObjectInfo = after;
		return toRet;
	}

	static AuditCollectionChangeInfo createAddedAuditSetChangeInfo(AuditObjectValueInfo added) {
		AuditCollectionChangeInfo toRet = new AuditCollectionChangeInfo();
		toRet.auditMapChangeTypeEnum = AuditCollectionChangeTypeEnum.ADDED;
		toRet.afterAuditObjectInfo = added;
		return toRet;
	}

	static AuditCollectionChangeInfo createRemovedAuditSetChangeInfo(AuditObjectValueInfo removed) {
		AuditCollectionChangeInfo toRet = new AuditCollectionChangeInfo();
		toRet.auditMapChangeTypeEnum = AuditCollectionChangeTypeEnum.REMOVED;
		toRet.previousAuditObjectInfo = removed;
		return toRet;
	}

	static AuditCollectionChangeInfo createChangedAuditSetChangeInfo(AuditObjectValueInfo previous, AuditObjectValueInfo after) {
		AuditCollectionChangeInfo toRet = new AuditCollectionChangeInfo();
		toRet.auditMapChangeTypeEnum = AuditCollectionChangeTypeEnum.CHANGED;
		toRet.previousAuditObjectInfo = previous;
		toRet.afterAuditObjectInfo = after;
		return toRet;
	}

	static AuditCollectionChangeInfo createAddedAuditListChangeInfo(Integer index, AuditObjectValueInfo added) {
		AuditCollectionChangeInfo toRet = new AuditCollectionChangeInfo();
		toRet.key = index.toString();
		toRet.auditMapChangeTypeEnum = AuditCollectionChangeTypeEnum.ADDED;
		toRet.afterAuditObjectInfo = added;
		return toRet;
	}

	static AuditCollectionChangeInfo createRemovedAuditListChangeInfo(Integer index, AuditObjectValueInfo removed) {
		AuditCollectionChangeInfo toRet = new AuditCollectionChangeInfo();
		toRet.key = index.toString();
		toRet.auditMapChangeTypeEnum = AuditCollectionChangeTypeEnum.REMOVED;
		toRet.previousAuditObjectInfo = removed;
		return toRet;
	}

	static AuditCollectionChangeInfo createChangedAuditListChangeInfo(Integer index, AuditObjectValueInfo previous, AuditObjectValueInfo after) {
		AuditCollectionChangeInfo toRet = new AuditCollectionChangeInfo();
		toRet.key = index.toString();
		toRet.auditMapChangeTypeEnum = AuditCollectionChangeTypeEnum.CHANGED;
		toRet.previousAuditObjectInfo = previous;
		toRet.afterAuditObjectInfo = after;
		return toRet;
	}

	static AuditCollectionChangeInfo createAddedAuditArrayChangeInfo(Integer index, AuditObjectValueInfo added) {
		AuditCollectionChangeInfo toRet = new AuditCollectionChangeInfo();
		toRet.key = index.toString();
		toRet.auditMapChangeTypeEnum = AuditCollectionChangeTypeEnum.ADDED;
		toRet.afterAuditObjectInfo = added;
		return toRet;
	}

	static AuditCollectionChangeInfo createRemovedAuditArrayChangeInfo(Integer index, AuditObjectValueInfo removed) {
		AuditCollectionChangeInfo toRet = new AuditCollectionChangeInfo();
		toRet.key = index.toString();
		toRet.auditMapChangeTypeEnum = AuditCollectionChangeTypeEnum.REMOVED;
		toRet.previousAuditObjectInfo = removed;
		return toRet;
	}

	static AuditCollectionChangeInfo createChangedAuditArrayChangeInfo(Integer index, AuditObjectValueInfo previous, AuditObjectValueInfo after) {
		AuditCollectionChangeInfo toRet = new AuditCollectionChangeInfo();
		toRet.key = index.toString();
		toRet.auditMapChangeTypeEnum = AuditCollectionChangeTypeEnum.CHANGED;
		toRet.previousAuditObjectInfo = previous;
		toRet.afterAuditObjectInfo = after;
		return toRet;
	}
}
