package it.tredi.ecm.audit;

import java.util.ArrayList;
import java.util.List;

import org.javers.core.diff.changetype.ValueChange;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tredi.ecm.dao.entity.Account;

public class AuditPropertyChangeInfo {
	private static Logger LOGGER = LoggerFactory.getLogger(AuditPropertyChangeInfo.class);

	private String userName;
	private Account account;
	private LocalDateTime dataModifica;
	private List<AuditLabelInfo> auditLabelInfos;
	private String propertyLabelInfo;
	private String label;


	//Per ora tenere
	private ValueChange valueChange;



//	JaversType javersType;
//	Javers javers;

//	private String propertyOwnerObject;
//
//	private Class rootClass;
//	private String fullPath;
//	private String fullPathForLabel;
//
//
//	private CommitMetadata commitMetadata;


	AuditPropertyChangeInfo() {
		auditLabelInfos = new ArrayList<AuditLabelInfo>();
	}

//	AuditPropertyChangeInfo(ValueChange valueChange, Javers javers, JaversType javersType, Account account) {
//		this.valueChange = valueChange;
//		this.account = account;
//		this.javers = javers;
//		this.javersType = javersType;
//		if(valueChange.getCommitMetadata().isPresent())
//			this.commitMetadata = valueChange.getCommitMetadata().get();
//		//valueChange.getAffectedObject()
//
//		auditLabelInfos = new ArrayList<AuditLabelInfo>();
//		setLabel(valueChange.getAffectedGlobalId(), valueChange.getPropertyName());
//	}

//	private void setLabel(GlobalId globalId, String propertyName) {
//		LOGGER.info("propertyName: " + propertyName);
//		propertyLabel = AuditUtils.getTypeNameWithoutPackage(globalId.getTypeName()) + "." + propertyName;
//		//    InstanceId, UnboundedValueObjectId, ValueObjectId
//		if(globalId instanceof InstanceId) {
//			//propertyOwnerObject = AuditUtils.getTypeNameWithoutPackage(globalId.getTypeName());
//			//LOGGER.info("propertyOwnerObject: " + propertyOwnerObject);
//			InstanceId instanceId = (InstanceId)globalId;
//			LOGGER.info("instanceId.getCdoId().toString(): " + instanceId.getCdoId().toString());
//		} else if (globalId instanceof ValueObjectId) {
//			ValueObjectId valueObjectId = (ValueObjectId)globalId;
//			propertyOwnerObject = AuditUtils.getTypeNameWithoutPackage(valueObjectId.getOwnerId().getTypeName());
//			LOGGER.info("propertyOwnerObject: " + propertyOwnerObject);
//			//Ricavo l'albero delle label
//			// esempi getFragment()
//			// programma/0						propertyName: giorno		propertyOwnerObject: ProgrammaGiornalieroRES
//			// programma/1/sede					propertyName: provincia		propertyOwnerObject: SedeEvento
//			// programma/0/programma/1			propertyName: orarioInizio	propertyOwnerObject: DettaglioAttivitaRES
//
//			//splitto
//			if(valueObjectId.getFragment().isEmpty()) {
//				propertyLabel = propertyOwnerObject + "." + propertyName;
//			} else {
//				String[] split = valueObjectId.getFragment().split("/");
//				String labelPath = propertyOwnerObject;
//				ManagedType prevEntType = (ManagedType)javersType;
//				for(int i = 0; i < split.length; i++) {
//					Property prop = null;
//					try {
//						prop = prevEntType.getProperty(split[i]);
//					} catch (JaversException e) {
//
//					}
//					if(prop != null) {
//						LOGGER.debug("prop.getName(): " + prop.getName());
//						AuditLabelInfo auditLabelInfo = new AuditLabelInfo();
//
//						Type type = prop.getMember().getGenericResolvedType();
//					    if(type != null) {
//							if (type instanceof ParameterizedType) {
//						        ParameterizedType pType = (ParameterizedType)type;
//						        LOGGER.debug("Raw type: " + pType.getRawType() + " - ");
//						        LOGGER.debug("Type args: " + pType.getActualTypeArguments()[0]);
//						        prevEntType = javers.getTypeMapping(pType.getActualTypeArguments()[0]);
//						        //TODO
//						        //Controllare se e' una lista e fare quanto segue solo in tal caso
//						        if(i + 1 < split.length) {
//						        	i++;
//						        	//se split[i] e' un intero è la posizione nella lista partendo da 0 lo aumento di 1
//						        	String identifier = split[i];
//						        	try {
//						                int intIdentifier = Integer.parseInt(identifier);
//						                identifier = Integer.toString(intIdentifier + 1);
//						            } catch(Exception e) {
//						            	//Non è un intero
//						            }
//						        	auditLabelInfo.setObjectIdentifier(identifier);
//						        }
//						    } else {
//						    	LOGGER.debug("Type: " + prop.getMember().getGenericResolvedType());
//						    }
//							if(!labelPath.isEmpty())
//								labelPath += ".";
//							labelPath += prop.getName();
//							auditLabelInfo.setPropertyLabel(labelPath);
//
//							auditLabelInfos.add(auditLabelInfo);
//					    }
//					}
//
////					if ((i % 2) == 0) {
////					    //pari
////						//auditLabelInfo.setIdentifierType(IdentifierTypeEnum.);;
////
////						auditLabelInfo = new AuditLabelInfo();
////					}
////					else {
////					    //dispari
////					}
//				}
//			}
//
//			LOGGER.info("valueObjectId.getFragment(): " + valueObjectId.getFragment());
//			LOGGER.info("valueObjectId.getTypeName(): " + valueObjectId.getTypeName());
//			LOGGER.info("valueObjectId.value(): " + valueObjectId.value());
//			LOGGER.info("valueObjectId.getOwnerId().toString(): " + valueObjectId.getOwnerId().toString());
//			//LOGGER.debug(valueObjectId.hasOwnerOfType(entityType));
//		}
//	}

//	public LocalDateTime getDataModifica() {
//		return commitMetadata.getCommitDate();
//	}

	public String getPreviousValue() {
		if(valueChange != null) {
			if(valueChange.getLeft() != null)
				return valueChange.getLeft().toString();
		}

		return null;
	}

	public String getAfterValue() {
		if(valueChange != null)
			return valueChange.getRight().toString();

		return null;
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
}
