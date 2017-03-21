package it.tredi.ecm.audit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import org.javers.common.exception.JaversException;
import org.javers.core.Javers;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ElementValueChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.container.ValueAdded;
import org.javers.core.diff.changetype.container.ValueRemoved;
import org.javers.core.diff.changetype.map.EntryAdded;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.diff.changetype.map.EntryRemoved;
import org.javers.core.diff.changetype.map.EntryValueChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.ValueObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.service.AuditService;

public class EcmAuditInfoChangeLog extends AbstractAuditInfoChangeLog {
	//private static final Logger LOGGER = LoggerFactory.getLogger(EcmAuditInfoChangeLog.class);
	private Logger LOGGER = LoggerFactory.getLogger(EcmAuditInfoChangeLog.class);
    //public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormat.mediumDateTime();
    //private final DateTimeFormatter dateTimeFormatter;

    private AuditInfo auditInfo;
    private AccountService accountService;
    private AuditService auditService;
    private Javers javers;
    private JaversType javersType;

//    public EcmAuditInfoChangeLog(Class entityClass, Long entityId, Javers javers, AuditService auditService, AccountService accountService) {
//        this(entityClass, entityId, javers, auditService, accountService, DEFAULT_DATE_FORMATTER);
//    }

    public EcmAuditInfoChangeLog(Class entityClass, Long entityId, Javers javers, AuditService auditService, AccountService accountService) {
        this.auditInfo = new AuditInfo(entityId, entityClass);
        this.javers = javers;
        this.auditService = auditService;
        this.accountService = accountService;
        //this.dateTimeFormatter = dateTimeFormatter;

        this.javersType = javers.getTypeMapping(entityClass);
    }

    @Override
    public void onCommit(CommitMetadata commitMetadata) {
    	//Viene chiamato quando si passa da un commit ad un'altro oppure da un oggetto ad un altro nello stesso commit
    	//Per la gestione attuale non lo prendo in considerazione
    }

	//globalId.getTypeName() restituisce il tipo dell'oggetto se l'attributo withTypeName non è impostato altrimenti restituisce withTypeName
    @Override
    public void onAffectedObject(GlobalId globalId) {
    	//Viene chiamato quando si passa ad un nuovo oggetto di cui verranno poi elencate le proprietà modificate
    	//Non gestito si ricava l'oggetto dalla property modificata in onValueChange
    }

    @Override
    public void onValueChange(ValueChange valueChange) {
    	//Viene chiamato per ogni commit per ogni property di tipo valore modificata
    	if(leftAndRightLikeEmpty(valueChange))
    		return;
    	auditInfo.getAuditProperties().add(createAuditPropertyChangeInfo(valueChange));
    }

    private AuditPropertyChangeInfo createAuditPropertyChangeInfo(ValueChange valueChange) {
    	AuditPropertyChangeInfo auditPropertyChangeInfo = new AuditPropertyChangeInfo();
    	auditPropertyChangeInfo.setValueChange(valueChange);
		if(valueChange != null) {
			if(valueChange.getLeft() != null) {
				AuditObjectValueInfo auditObjectValueInfo = new AuditObjectValueInfo();
				auditObjectValueInfo.setAuditChangeInfoTypeEnum(AuditObjectInfoTypeEnum.VALUE);
				auditObjectValueInfo.setValue(valueChange.getLeft().toString());
				auditPropertyChangeInfo.setPreviousAuditObjectInfo(auditObjectValueInfo);
			}
			if(valueChange.getRight() != null) {
				AuditObjectValueInfo auditObjectValueInfo = new AuditObjectValueInfo();
				auditObjectValueInfo.setAuditChangeInfoTypeEnum(AuditObjectInfoTypeEnum.VALUE);
				auditObjectValueInfo.setValue(valueChange.getRight().toString());
				auditPropertyChangeInfo.setAfterAuditObjectInfo(auditObjectValueInfo);
			}
		}
    	auditPropertyChangeInfo.setAuditPropertyChangeInfoTypeEnum(AuditPropertyChangeInfoTypeEnum.VALUE);
    	setCommitInfo(auditPropertyChangeInfo, valueChange.getCommitMetadata());
		setLabel(auditPropertyChangeInfo, valueChange.getAffectedGlobalId(), valueChange.getPropertyName());
		return auditPropertyChangeInfo;
	}

    private void setCommitInfo(AuditPropertyChangeInfo auditPropertyChangeInfo, org.javers.common.collections.Optional<CommitMetadata> commitMetadata) {
    	Account account = null;
    	if(commitMetadata.isPresent() && commitMetadata.get().getAuthor() != null) {
        	auditPropertyChangeInfo.setDataModifica(commitMetadata.get().getCommitDate());
        	auditPropertyChangeInfo.setUserName(commitMetadata.get().getAuthor());
    		Optional<Account> optAcc = accountService.getUserByUsername(commitMetadata.get().getAuthor());
    		if(optAcc.isPresent())
    			account = optAcc.get();
    	}
    	auditPropertyChangeInfo.setAccount(account);
    }

	private void setLabel(AuditPropertyChangeInfo auditPropertyChangeInfo, GlobalId affectedGlobalId, String propertyName) {
		if(LOGGER.isDebugEnabled())
			setLabelShowLog(affectedGlobalId, propertyName);
		auditPropertyChangeInfo.setPropertyLabelInfo(AuditUtils.getTypeNameWithoutPackage(affectedGlobalId.getTypeName()) + "." + propertyName);
		auditPropertyChangeInfo.setPropertyFullPath(AuditUtils.getTypeNameWithoutPackage(affectedGlobalId.getTypeName()));
		//    InstanceId, UnboundedValueObjectId, ValueObjectId
		String propertyOwnerObject;
		if(affectedGlobalId instanceof InstanceId) {
			auditPropertyChangeInfo.setPropertyFullPath(auditPropertyChangeInfo.getPropertyFullPath() + "." + propertyName);
			InstanceId instanceId = (InstanceId)affectedGlobalId;
		} else if (affectedGlobalId instanceof ValueObjectId) {
			ValueObjectId valueObjectId = (ValueObjectId)affectedGlobalId;
			propertyOwnerObject = AuditUtils.getTypeNameWithoutPackage(valueObjectId.getOwnerId().getTypeName());
			//Ricavo l'albero delle label
			// esempi getFragment()
			// - Caso List
			// programma/0						propertyName: giorno		propertyOwnerObject: ProgrammaGiornalieroRES
			// programma/1/sede					propertyName: provincia		propertyOwnerObject: SedeEvento
			// programma/0/programma/1			propertyName: orarioInizio	propertyOwnerObject: DettaglioAttivitaRES
			// - Caso Map
			// componentiComitatoScientificoMap/1334/anagrafica - propertyName: id

			//splitto
			if(valueObjectId.getFragment().isEmpty()) {
				auditPropertyChangeInfo.setPropertyLabelInfo(propertyOwnerObject + "." + propertyName);
				auditPropertyChangeInfo.setPropertyFullPath(propertyOwnerObject + "." + propertyName);
			} else {
				String[] split = valueObjectId.getFragment().split("/");
				String labelPath = propertyOwnerObject;
				ManagedType prevEntType = (ManagedType)javersType;
				for(int i = 0; i < split.length; i++) {
					Property prop = null;
					try {
						prop = prevEntType.getProperty(split[i]);
					} catch (JaversException e) {

					}
					if(prop != null) {
						AuditLabelInfo auditLabelInfo = new AuditLabelInfo();
						Type type = prop.getMember().getGenericResolvedType();
					    if(type != null) {
							if (type instanceof ParameterizedType) {
						        ParameterizedType pType = (ParameterizedType)type;
						        prevEntType = javers.getTypeMapping(pType.getActualTypeArguments()[pType.getActualTypeArguments().length-1]);
						        if(i + 1 < split.length) {
						        	i++;
						        	//se split[i] e' un intero è la posizione nella lista partendo da 0 lo aumento di 1
						        	String identifier = split[i];
							        //Controllare se e' una lista e fare quanto segue solo in tal caso
						        	//(Collection.class.isAssignableFrom(field.getType()))

						        	//non gli sommo 1 perche' nel campo lista poi e' senza il +1
//						        	if(pType.getRawType() instanceof Class && List.class.isAssignableFrom(((Class)pType.getRawType()))) {
//							        	try {
//							                int intIdentifier = Integer.parseInt(identifier);
//							                identifier = Integer.toString(intIdentifier + 1);
//							            } catch(Exception e) {
//							            	//Non è un intero
//							            }
//						        	}
						        	auditLabelInfo.setObjectIdentifier(identifier);
						        }
						    } else {
						    	prevEntType = javers.getTypeMapping(type);
						    }
							if(!labelPath.isEmpty())
								labelPath += ".";
							labelPath += prop.getName();
							auditLabelInfo.setPropertyLabel(labelPath);
							auditPropertyChangeInfo.getAuditLabelInfos().add(auditLabelInfo);
					    }
					}
				}
				auditPropertyChangeInfo.setPropertyFullPath(labelPath + "." + propertyName);
			}
		}
//		LOGGER.info("affectedGlobalId: " + affectedGlobalId);
//		LOGGER.info("propertyName: " + propertyName);
//		LOGGER.info("auditPropertyChangeInfo.getPropertyFullPath(): " + auditPropertyChangeInfo.getPropertyFullPath());
		auditPropertyChangeInfo.setLabel(auditService.getLabelForAuditProperty(auditPropertyChangeInfo.getAuditLabelInfos(), auditPropertyChangeInfo.getPropertyLabelInfo()));
	}

	private void setLabelShowLog(GlobalId affectedGlobalId, String propertyName) {
		LOGGER.debug("setLabelLog - affectedGlobalId: " + affectedGlobalId);
		LOGGER.debug("setLabelLog - propertyName: " + propertyName);
		//    InstanceId, UnboundedValueObjectId, ValueObjectId
		String propertyOwnerObject;
		if(affectedGlobalId instanceof InstanceId) {
			InstanceId instanceId = (InstanceId)affectedGlobalId;
			LOGGER.debug("instanceId.getCdoId().toString(): " + instanceId.getCdoId().toString());
		} else if (affectedGlobalId instanceof ValueObjectId) {
			ValueObjectId valueObjectId = (ValueObjectId)affectedGlobalId;
			propertyOwnerObject = AuditUtils.getTypeNameWithoutPackage(valueObjectId.getOwnerId().getTypeName());
			LOGGER.debug("propertyOwnerObject: " + propertyOwnerObject);
			//Ricavo l'albero delle label
			// esempi getFragment()
			// - Caso List
			// programma/0						propertyName: giorno		propertyOwnerObject: ProgrammaGiornalieroRES
			// programma/1/sede					propertyName: provincia		propertyOwnerObject: SedeEvento
			// programma/0/programma/1			propertyName: orarioInizio	propertyOwnerObject: DettaglioAttivitaRES
			// - Caso Map
			// componentiComitatoScientificoMap/1334/anagrafica - propertyName: id
			//splitto
			if(valueObjectId.getFragment().isEmpty()) {
				LOGGER.debug("valueObjectId.getFragment() == empty");
				LOGGER.debug("PropertyLabelInfo: " + propertyOwnerObject + "." + propertyName);
				LOGGER.debug("PropertyFullPath: " + propertyOwnerObject + "." + propertyName);
			} else {
				LOGGER.debug("valueObjectId.getFragment(): " + valueObjectId.getFragment() + " - propertyName: " + propertyName);
				String[] split = valueObjectId.getFragment().split("/");
				String labelPath = propertyOwnerObject;
				ManagedType prevEntType = (ManagedType)javersType;
				for(int i = 0; i < split.length; i++) {
					Property prop = null;
					try {
						prop = prevEntType.getProperty(split[i]);
					} catch (JaversException e) {

					}
					if(prop != null) {
						LOGGER.debug("prop.getName(): " + prop.getName());
						AuditLabelInfo auditLabelInfo = new AuditLabelInfo();
						Type type = prop.getMember().getGenericResolvedType();
					    if(type != null) {
							if (type instanceof ParameterizedType) {
						        ParameterizedType pType = (ParameterizedType)type;
						        LOGGER.debug("Raw type: " + pType.getRawType() + " - ");
						        LOGGER.debug("Type args: " + pType.getActualTypeArguments()[pType.getActualTypeArguments().length-1]);
						        prevEntType = javers.getTypeMapping(pType.getActualTypeArguments()[pType.getActualTypeArguments().length-1]);
						        if(i + 1 < split.length) {
						        	i++;
						        	//se split[i] e' un intero è la posizione nella lista partendo da 0 lo aumento di 1
						        	String identifier = split[i];
							        //Controllare se e' una lista e fare quanto segue solo in tal caso
						        	//(Collection.class.isAssignableFrom(field.getType()))
						        	if(pType.getRawType() instanceof Class && List.class.isAssignableFrom(((Class)pType.getRawType()))) {
							        	try {
							                int intIdentifier = Integer.parseInt(identifier);
							                identifier = Integer.toString(intIdentifier + 1);
							            } catch(Exception e) {
							            	//Non è un intero
							            }
						        	}
						        	auditLabelInfo.setObjectIdentifier(identifier);
						        }
						    } else {
						    	LOGGER.debug("Type: " + prop.getMember().getGenericResolvedType());
						    	prevEntType = javers.getTypeMapping(type);
						    }
							if(!labelPath.isEmpty())
								labelPath += ".";
							labelPath += prop.getName();
							auditLabelInfo.setPropertyLabel(labelPath);
							LOGGER.debug("split[i]: " + split[i] + "; " + auditLabelInfo.toString());
					    }
					}
				}
				LOGGER.debug("PropertyFullPath: " + labelPath + "." + propertyName);
			}
		}
	}

    private boolean leftAndRightLikeEmpty(ValueChange valueChange) {
    	if(		(valueChange.getLeft() == null || valueChange.getLeft().toString() == null || valueChange.getLeft().toString().isEmpty())
    			&&
    			(valueChange.getRight() == null || valueChange.getRight().toString() == null || valueChange.getRight().toString().isEmpty())
    	) {
    		return true;
    	}
    	return false;
    }

    private void showPropInfo(Property prop) {
		LOGGER.debug("-------------- PROPERTY INFO INIZIO --------------");
		if(prop==null) {
			LOGGER.debug("prop = NULL");
			return;
		}
		LOGGER.debug("Property: " + prop.toString());
		LOGGER.debug("prop.getName(): " + prop.getName());
		LOGGER.debug("prop.getGenericType(): " + prop.getGenericType());
		LOGGER.debug("prop.getRawType(): " + prop.getRawType());
		LOGGER.debug("prop.hasShallowReferenceAnn(): " + prop.hasShallowReferenceAnn());
		LOGGER.debug("prop.looksLikeId(): " + prop.looksLikeId());
		LOGGER.debug("prop.getMember(): " + prop.getMember());
		LOGGER.debug("--------------  PROPERTY INFO FINE  --------------");
    }

    private void showGlobalIdInfo(GlobalId globalId) {
    	showGlobalIdInfo(globalId, true);
    }

    private void showGlobalIdInfo(GlobalId globalId, boolean isRadice) {
    	if(isRadice)
			LOGGER.info("--- showGlobalIdInfo - INIZIO ---");
		if(globalId instanceof InstanceId) {
			InstanceId instanceId = (InstanceId)globalId;
			LOGGER.info("showGlobalIdInfo - instanceId.getCdoId().getTypeName(): " + instanceId.getTypeName());
			LOGGER.info("showGlobalIdInfo - instanceId.getCdoId().toString(): " + instanceId.getCdoId().toString());
		} else if (globalId instanceof ValueObjectId) {
			ValueObjectId valueObjectId = (ValueObjectId)globalId;
			LOGGER.info("valueObjectId - valueObjectId.getTypeName(): " + valueObjectId.getTypeName());
			LOGGER.info("valueObjectId - valueObjectId.getFragment(): " + valueObjectId.getFragment());
			LOGGER.info("valueObjectId - showGlobalIdInfo di valueObjectId.getOwnerId()");
			showGlobalIdInfo(valueObjectId.getOwnerId(), false);
		}
    	if(isRadice)
			LOGGER.info("---- showGlobalIdInfo - FINE ----");
    }

    private Long getEntityIdFromGlobalId(GlobalId globalId) {
		if(globalId instanceof InstanceId) {
			InstanceId instanceId = (InstanceId)globalId;
			return (Long)instanceId.getCdoId();
		}
		return null;
    }

    private void showType(Type type) {
    	LOGGER.info("showType - TypeMapping(" + type + "): " + javers.getTypeMapping(type).prettyPrint());
    	JaversType javersType = javers.getTypeMapping(type);
    	if(javersType instanceof ValueObjectType)
    		LOGGER.info("showType è un ValueObjectType");
    	else if(javersType instanceof EntityType)
    		LOGGER.info("showType è un EntityType");
    	else
    		LOGGER.info("showType è altro");
    }

    private void getPropertyChangeInfo(PropertyChange propertyChange) {
    	Property prop = javers.getProperty(propertyChange);
    	LOGGER.info("----------- getPropertyChangeInfo INIZIO -------------");
    	showType(prop.getGenericType());
    	LOGGER.info("-----------  getPropertyChangeInfo FINE  -------------");
    }

    private void getInfoOnCurrentProperty(GlobalId globalId, String propertyName) {
    	LOGGER.info("----------- getInfoOnCurrentProperty INIZIO -------------");
    	LOGGER.info("globalId: " + globalId.toString());
    	LOGGER.info("propertyName: " + propertyName);
		ManagedType prevEntType = (ManagedType)javersType;
    	LOGGER.info("prevEntType: " + prevEntType);
		Property prop = null;
		if(globalId instanceof InstanceId) {
			InstanceId instanceId = (InstanceId)globalId;
			LOGGER.info("instanceId - instanceId.getCdoId().toString(): " + instanceId.getCdoId().toString());
			prop = prevEntType.getProperty(propertyName);
			showPropInfo(prop);
		} else if (globalId instanceof ValueObjectId) {
			ValueObjectId valueObjectId = (ValueObjectId)globalId;
			String propertyOwnerObject;
			propertyOwnerObject = AuditUtils.getTypeNameWithoutPackage(valueObjectId.getOwnerId().getTypeName());
			LOGGER.info("ValueObjectId - propertyOwnerObject: " + propertyOwnerObject);
			if(valueObjectId.getFragment().isEmpty()) {
				LOGGER.info("valueObjectId.getFragment().isEmpty()");
				prop = prevEntType.getProperty(propertyName);
				showPropInfo(prop);
			} else {
				LOGGER.info("NOT valueObjectId.getFragment().isEmpty()");
				String[] split = valueObjectId.getFragment().split("/");
				String labelPath = propertyOwnerObject;
				for(int i = 0; i < split.length; i++) {
					LOGGER.info("split[" + i + "]: " + split[i]);
					try {
						prop = prevEntType.getProperty(split[i]);
					} catch (JaversException e) {
					}
					if(prop == null) {
						LOGGER.info("PROP NULL split[" + i + "]: " + split[i]);
					} else {
						showPropInfo(prop);
						Type type = prop.getMember().getGenericResolvedType();
					    if(type != null) {
							if (type instanceof ParameterizedType) {
						        ParameterizedType pType = (ParameterizedType)type;
						        LOGGER.debug("Raw type: " + pType.getRawType() + " - ");
						        LOGGER.debug("Type args: " + pType.getActualTypeArguments()[0]);
						        prevEntType = javers.getTypeMapping(pType.getActualTypeArguments()[0]);
						        //TODO
						        //Controllare se e' una lista e fare quanto segue solo in tal caso
						        if(i + 1 < split.length) {
						        	i++;
						        	//se split[i] e' un intero è la posizione nella lista partendo da 0 lo aumento di 1
						        	String identifier = split[i];
						        	try {
						                int intIdentifier = Integer.parseInt(identifier);
						                identifier = Integer.toString(intIdentifier + 1);
						            } catch(Exception e) {
						            	//Non è un intero
						            }
						        }
						    } else {
						    	LOGGER.debug("Type: " + prop.getMember().getGenericResolvedType());
						    }
					    }
					}
				}
				try {
					prop = prevEntType.getProperty(propertyName);
				} catch (JaversException e) {
				}
				if(prop == null) {
					LOGGER.info("PROP NULL " + propertyName);
				} else {
					showPropInfo(prop);
				}
			}
		}

		if(prop != null)
			showType(prop.getGenericType());
    	LOGGER.info("----------- getInfoOnCurrentProperty FINE -------------");
    }

    @Override
    public void onReferenceChange(ReferenceChange referenceChange) {
    	if(LOGGER.isDebugEnabled())
    		onReferenceChangeShowLog(referenceChange);
    	Property prop = javers.getProperty(referenceChange);
    	JaversType javersType = javers.getTypeMapping(prop.getGenericType());
    	//Se il reference (la properties che sto modificando) è un embedded (javers ValuleObject)
    	//vengono anche elencate le modifiche di tutte le relative property nel valueChange quindi lo saltiamo
    	if(javersType instanceof ValueObjectType) {
    		LOGGER.info("onReferenceChange di un ValueObjectType return");
    		return;
    	} else if(javersType instanceof EntityType)
    		LOGGER.info("onReferenceChange di un EntityType");
    	else {
    		LOGGER.warn("onReferenceChange di oggetto ne ValueObjectType ne EntityType");
    		return;
    	}
    	EntityType entityType = (EntityType)javersType;
    	if(entityType.getBaseJavaClass().equals(it.tredi.ecm.dao.entity.File.class)) {
    		//la property in modifica è un file
        	AuditPropertyChangeInfo auditPropertyChangeInfo = new AuditPropertyChangeInfo();
        	auditPropertyChangeInfo.setAuditPropertyChangeInfoTypeEnum(AuditPropertyChangeInfoTypeEnum.FILE);
        	auditPropertyChangeInfo.setPreviousAuditObjectInfo(getAuditObjectInfoForGlobalId(referenceChange.getLeft()));
        	auditPropertyChangeInfo.setAfterAuditObjectInfo(getAuditObjectInfoForGlobalId(referenceChange.getRight()));
        	auditPropertyChangeInfo.setReferenceChange(referenceChange);
        	setCommitInfo(auditPropertyChangeInfo, referenceChange.getCommitMetadata());
    		setLabel(auditPropertyChangeInfo, referenceChange.getAffectedGlobalId(), referenceChange.getPropertyName());
    		auditInfo.getAuditProperties().add(auditPropertyChangeInfo);
    	} else {
        	AuditPropertyChangeInfo auditPropertyChangeInfo = new AuditPropertyChangeInfo();
        	auditPropertyChangeInfo.setAuditPropertyChangeInfoTypeEnum(AuditPropertyChangeInfoTypeEnum.ENTITY);
        	auditPropertyChangeInfo.setPreviousAuditObjectInfo(getAuditObjectInfoForGlobalId(referenceChange.getLeft()));
        	auditPropertyChangeInfo.setAfterAuditObjectInfo(getAuditObjectInfoForGlobalId(referenceChange.getRight()));
        	auditPropertyChangeInfo.setReferenceChange(referenceChange);
        	setCommitInfo(auditPropertyChangeInfo, referenceChange.getCommitMetadata());
    		setLabel(auditPropertyChangeInfo, referenceChange.getAffectedGlobalId(), referenceChange.getPropertyName());
    		auditInfo.getAuditProperties().add(auditPropertyChangeInfo);
    	}
    }

    public void onReferenceChangeShowLog(ReferenceChange referenceChange) {
    	Property prop = javers.getProperty(referenceChange);
    	LOGGER.debug("----------- REFERENCE CHANGE INIZIO -------------");
    	LOGGER.debug(referenceChange.toString());
    	LOGGER.debug("referenceChange.getPropertyName(): " + referenceChange.getPropertyName());
    	LOGGER.debug("referenceChange.getAffectedGlobalId(): " + referenceChange.getAffectedGlobalId());
    	LOGGER.debug("referenceChange.getAffectedLocalId(): " + referenceChange.getAffectedLocalId());
    	LOGGER.debug("referenceChange.getAffectedObject(): " + referenceChange.getAffectedObject());
    	LOGGER.debug("referenceChange.getCommitMetadata(): " + referenceChange.getCommitMetadata());
    	LOGGER.debug("referenceChange.getLeft(): " + referenceChange.getLeft());
    	LOGGER.debug("referenceChange.getRight(): " + referenceChange.getRight());
    	LOGGER.debug("----------- REFERENCE CHANGE FINE -------------");
    }

    @Override
    public void onNewObject(NewObject newObject) {
    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {
    	//Chiamata quando l'oggetto è stato cancellato
    }

    private AuditObjectInfoTypeEnum getAuditChangeInfoTypeEnumForGlobalId(GlobalId globalId) {
		if(globalId != null) {
			if(globalId instanceof InstanceId) {
				InstanceId instanceId = (InstanceId)globalId;
				LOGGER.info("showGlobalIdInfo - instanceId.getCdoId().getTypeName(): " + instanceId.getTypeName());
				//instanceId.getCdoId().getTypeName()
				if("File".equals(instanceId.getTypeName()))
					return AuditObjectInfoTypeEnum.FILE;
				else
					return AuditObjectInfoTypeEnum.ENTITY;
			} else if (globalId instanceof ValueObjectId) {
				return AuditObjectInfoTypeEnum.VALUEOBJECT;
			}
		}
		return AuditObjectInfoTypeEnum.VALUE;
    }

    private AuditObjectValueInfo getAuditObjectInfoForGlobalId(GlobalId globalId) {
    	AuditObjectValueInfo toRet = null;
    	if(globalId != null) {
			if(globalId instanceof InstanceId) {
				InstanceId instanceId = (InstanceId)globalId;
				LOGGER.debug("getAuditObjectInfoForGlobalId - instanceId.getCdoId().getTypeName(): " + instanceId.getTypeName());
				if(instanceId.getCdoId() instanceof Long) {
					toRet = new AuditObjectValueInfo();
					//toRet.setEntity(AuditUtils.getTypeNameWithoutPackage(entityType.getBaseJavaClass().getName()));
					//toRet.setEntity(AuditUtils.getTypeNameWithoutPackage(instanceId.getTypeName()));
					toRet.setEntity(instanceId.getTypeName());
					toRet.setId((Long)instanceId.getCdoId());
					if("File".equals(instanceId.getTypeName()))
						toRet.setAuditChangeInfoTypeEnum(AuditObjectInfoTypeEnum.FILE);
					else
						toRet.setAuditChangeInfoTypeEnum(AuditObjectInfoTypeEnum.ENTITY);
				} else {
					LOGGER.warn("InstanceId - Id non di tipo long per TypeName " + instanceId.getTypeName() + "; instanceId: " + instanceId.toString());
				}
			} else if (globalId instanceof ValueObjectId) {
				ValueObjectId valueObjectId = (ValueObjectId)globalId;
				if(valueObjectId.getOwnerId() instanceof InstanceId) {
					InstanceId ownerInstanceId = (InstanceId)valueObjectId.getOwnerId();
					if(ownerInstanceId.getCdoId() instanceof Long) {
						toRet = new AuditObjectValueInfo();
						toRet.setAuditChangeInfoTypeEnum(AuditObjectInfoTypeEnum.VALUEOBJECT);
						toRet.setEntity(ownerInstanceId.getTypeName());
						toRet.setId((Long)ownerInstanceId.getCdoId());
						toRet.setValueObjectFragment(valueObjectId.getFragment());
					} else {
						LOGGER.warn("ValueObjectId - Id di getOwnerId non di tipo long per TypeName " + ownerInstanceId.getTypeName() + "; valueObjectId: " + valueObjectId.toString());
					}
				} else {
					LOGGER.warn("ValueObjectId - getOwnerId non di tipo InstanceId " + valueObjectId.getOwnerId() + "; valueObjectId: " + valueObjectId.toString());
				}
			}
		}
		return toRet;
    }

    private AuditObjectValueInfo getAuditObjectInfoForValue(String value) {
    	AuditObjectValueInfo toRet = null;
    	if(value != null) {
			toRet = new AuditObjectValueInfo();
			toRet.setAuditChangeInfoTypeEnum(AuditObjectInfoTypeEnum.VALUE);
			toRet.setValue(value);
		}
		return toRet;
    }

    private AuditPropertyChangeInfoTypeEnum getMapAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(AuditObjectInfoTypeEnum auditChangeInfoTypeEnum) {
    	switch (auditChangeInfoTypeEnum) {
		case VALUEOBJECT:
			return AuditPropertyChangeInfoTypeEnum.MAP_VALUEOBJECT;
		case FILE:
			return AuditPropertyChangeInfoTypeEnum.MAP_FILE;
		case ENTITY:
			return AuditPropertyChangeInfoTypeEnum.MAP_ENTITY;
		default:
			return AuditPropertyChangeInfoTypeEnum.MAP_VALUE;
		}
    }

    private AuditPropertyChangeInfoTypeEnum getSetAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(AuditObjectInfoTypeEnum auditChangeInfoTypeEnum) {
    	switch (auditChangeInfoTypeEnum) {
		case VALUEOBJECT:
			return AuditPropertyChangeInfoTypeEnum.SET_VALUEOBJECT;
		case FILE:
			return AuditPropertyChangeInfoTypeEnum.SET_FILE;
		case ENTITY:
			return AuditPropertyChangeInfoTypeEnum.SET_ENTITY;
		default:
			return AuditPropertyChangeInfoTypeEnum.SET_VALUE;
		}
    }

    private AuditPropertyChangeInfoTypeEnum getListAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(AuditObjectInfoTypeEnum auditChangeInfoTypeEnum) {
    	switch (auditChangeInfoTypeEnum) {
		case VALUEOBJECT:
			return AuditPropertyChangeInfoTypeEnum.LIST_VALUEOBJECT;
		case FILE:
			return AuditPropertyChangeInfoTypeEnum.LIST_FILE;
		case ENTITY:
			return AuditPropertyChangeInfoTypeEnum.LIST_ENTITY;
		default:
			return AuditPropertyChangeInfoTypeEnum.LIST_VALUE;
		}
    }

    private AuditPropertyChangeInfoTypeEnum getArrayAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(AuditObjectInfoTypeEnum auditChangeInfoTypeEnum) {
    	switch (auditChangeInfoTypeEnum) {
		case VALUEOBJECT:
			return AuditPropertyChangeInfoTypeEnum.ARRAY_VALUEOBJECT;
		case FILE:
			return AuditPropertyChangeInfoTypeEnum.ARRAY_FILE;
		case ENTITY:
			return AuditPropertyChangeInfoTypeEnum.ARRAY_ENTITY;
		default:
			return AuditPropertyChangeInfoTypeEnum.ARRAY_VALUE;
		}
    }

    private AuditPropertyChangeInfoTypeEnum getAuditPropertyChangeInfoTypeEnumForMap(MapChange mapChange) {
    	AuditPropertyChangeInfoTypeEnum toRet = AuditPropertyChangeInfoTypeEnum.MAP_VALUE;
    	EntryChange change = mapChange.getEntryChanges().get(0);
    	if(change instanceof EntryValueChange) {
    		EntryValueChange entryValueChange = (EntryValueChange)change;
    		GlobalId globalId = null;
    		if(entryValueChange.getLeftValue() != null && entryValueChange.getLeftValue() instanceof GlobalId) {
    			globalId = (GlobalId)entryValueChange.getLeftValue();
    		} else if(entryValueChange.getRightValue() != null && entryValueChange.getRightValue() instanceof GlobalId) {
    			globalId = (GlobalId)entryValueChange.getRightValue();
    		}
    		if(globalId != null) {
    			toRet = getMapAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(getAuditChangeInfoTypeEnumForGlobalId(globalId));
    		}
		} else if(change instanceof EntryRemoved) {
			EntryRemoved entryRemoved = (EntryRemoved)change;
			if(entryRemoved.getValue() instanceof GlobalId) {
				GlobalId globalId = (GlobalId)entryRemoved.getValue();
	    		if(globalId != null) {
	    			toRet = getMapAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(getAuditChangeInfoTypeEnumForGlobalId(globalId));
	    		}
			}
		} else if(change instanceof EntryAdded) {
			EntryAdded entryAdded = (EntryAdded)change;
			if(entryAdded.getValue() instanceof GlobalId) {
				GlobalId globalId = (GlobalId)entryAdded.getValue();
	    		if(globalId != null) {
	    			toRet = getMapAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(getAuditChangeInfoTypeEnumForGlobalId(globalId));
	    		}
			}
		}
    	return toRet;
    }

    private AuditPropertyChangeInfoTypeEnum getAuditPropertyChangeInfoTypeEnumForSet(SetChange mapChange) {
    	AuditPropertyChangeInfoTypeEnum toRet = AuditPropertyChangeInfoTypeEnum.SET_VALUE;
    	ContainerElementChange change = mapChange.getChanges().get(0);
    	if(change instanceof ElementValueChange) {
    		ElementValueChange elementValueChange = (ElementValueChange)change;
    		GlobalId globalId = null;
    		if(elementValueChange.getLeftValue() != null && elementValueChange.getLeftValue() instanceof GlobalId) {
    			globalId = (GlobalId)elementValueChange.getLeftValue();
    		} else if(elementValueChange.getRightValue() != null && elementValueChange.getRightValue() instanceof GlobalId) {
    			globalId = (GlobalId)elementValueChange.getRightValue();
    		}
    		if(globalId != null) {
    			toRet = getSetAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(getAuditChangeInfoTypeEnumForGlobalId(globalId));
    		}
		} else if(change instanceof ValueRemoved) {
			ValueRemoved valueRemoved = (ValueRemoved)change;
			if(valueRemoved.getValue() instanceof GlobalId) {
				GlobalId globalId = (GlobalId)valueRemoved.getValue();
	    		if(globalId != null) {
	    			toRet = getSetAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(getAuditChangeInfoTypeEnumForGlobalId(globalId));
	    		}
			}
		} else if(change instanceof ValueAdded) {
			ValueAdded valueAdded = (ValueAdded)change;
			if(valueAdded.getValue() instanceof GlobalId) {
				GlobalId globalId = (GlobalId)valueAdded.getValue();
	    		if(globalId != null) {
	    			toRet = getSetAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(getAuditChangeInfoTypeEnumForGlobalId(globalId));
	    		}
			}
		}
    	return toRet;
    }

    private AuditPropertyChangeInfoTypeEnum getAuditPropertyChangeInfoTypeEnumForList(ListChange mapChange) {
    	AuditPropertyChangeInfoTypeEnum toRet = AuditPropertyChangeInfoTypeEnum.LIST_VALUE;
    	ContainerElementChange change = mapChange.getChanges().get(0);
    	if(change instanceof ElementValueChange) {
    		ElementValueChange elementValueChange = (ElementValueChange)change;
    		GlobalId globalId = null;
    		if(elementValueChange.getLeftValue() != null && elementValueChange.getLeftValue() instanceof GlobalId) {
    			globalId = (GlobalId)elementValueChange.getLeftValue();
    		} else if(elementValueChange.getRightValue() != null && elementValueChange.getRightValue() instanceof GlobalId) {
    			globalId = (GlobalId)elementValueChange.getRightValue();
    		}
    		if(globalId != null) {
    			toRet = getListAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(getAuditChangeInfoTypeEnumForGlobalId(globalId));
    		}
		} else if(change instanceof ValueRemoved) {
			ValueRemoved valueRemoved = (ValueRemoved)change;
			if(valueRemoved.getValue() instanceof GlobalId) {
				GlobalId globalId = (GlobalId)valueRemoved.getValue();
	    		if(globalId != null) {
	    			toRet = getListAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(getAuditChangeInfoTypeEnumForGlobalId(globalId));
	    		}
			}
		} else if(change instanceof ValueAdded) {
			ValueAdded valueAdded = (ValueAdded)change;
			if(valueAdded.getValue() instanceof GlobalId) {
				GlobalId globalId = (GlobalId)valueAdded.getValue();
	    		if(globalId != null) {
	    			toRet = getListAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(getAuditChangeInfoTypeEnumForGlobalId(globalId));
	    		}
			}
		}
    	return toRet;
    }

    private AuditPropertyChangeInfoTypeEnum getAuditPropertyChangeInfoTypeEnumForArray(ArrayChange mapChange) {
    	AuditPropertyChangeInfoTypeEnum toRet = AuditPropertyChangeInfoTypeEnum.ARRAY_VALUE;
    	ContainerElementChange change = mapChange.getChanges().get(0);
    	if(change instanceof ElementValueChange) {
    		ElementValueChange elementValueChange = (ElementValueChange)change;
    		GlobalId globalId = null;
    		if(elementValueChange.getLeftValue() != null && elementValueChange.getLeftValue() instanceof GlobalId) {
    			globalId = (GlobalId)elementValueChange.getLeftValue();
    		} else if(elementValueChange.getRightValue() != null && elementValueChange.getRightValue() instanceof GlobalId) {
    			globalId = (GlobalId)elementValueChange.getRightValue();
    		}
    		if(globalId != null) {
    			toRet = getArrayAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(getAuditChangeInfoTypeEnumForGlobalId(globalId));
    		}
		} else if(change instanceof ValueRemoved) {
			ValueRemoved valueRemoved = (ValueRemoved)change;
			if(valueRemoved.getValue() instanceof GlobalId) {
				GlobalId globalId = (GlobalId)valueRemoved.getValue();
	    		if(globalId != null) {
	    			toRet = getArrayAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(getAuditChangeInfoTypeEnumForGlobalId(globalId));
	    		}
			}
		} else if(change instanceof ValueAdded) {
			ValueAdded valueAdded = (ValueAdded)change;
			if(valueAdded.getValue() instanceof GlobalId) {
				GlobalId globalId = (GlobalId)valueAdded.getValue();
	    		if(globalId != null) {
	    			toRet = getArrayAuditPropertyChangeInfoTypeEnumForAuditChangeInfoTypeEnum(getAuditChangeInfoTypeEnumForGlobalId(globalId));
	    		}
			}
		}
    	return toRet;
    }

    @Override
    public void onMapChange(MapChange mapChange) {
    	if(LOGGER.isDebugEnabled())
    		onMapChangeShowLog(mapChange);
    	if(mapChange.getEntryChanges() == null || mapChange.getEntryChanges().size() == 0)
    		return;
    	AuditPropertyChangeInfo auditPropertyChangeInfo = new AuditPropertyChangeInfo();
    	setCommitInfo(auditPropertyChangeInfo, mapChange.getCommitMetadata());
    	auditPropertyChangeInfo.setAuditPropertyChangeInfoTypeEnum(getAuditPropertyChangeInfoTypeEnumForMap(mapChange));
		setLabel(auditPropertyChangeInfo, mapChange.getAffectedGlobalId(), mapChange.getPropertyName());
    	for(EntryChange change : mapChange.getEntryChanges()) {
			if(change instanceof EntryValueChange) {
    			EntryValueChange entryValueChange = (EntryValueChange)change;
				AuditCollectionChangeInfo changed;
				switch (auditPropertyChangeInfo.getAuditPropertyChangeInfoTypeEnum()) {
					case MAP_ENTITY:
					case MAP_FILE:
					case MAP_VALUEOBJECT:
						changed = AuditCollectionChangeInfo.createChangedAuditMapChangeInfo(change.getKey().toString(), getAuditObjectInfoForGlobalId((GlobalId)entryValueChange.getLeftValue()), getAuditObjectInfoForGlobalId((GlobalId)entryValueChange.getRightValue()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
					case MAP_VALUE:
						changed = AuditCollectionChangeInfo.createChangedAuditMapChangeInfo(change.getKey().toString(), getAuditObjectInfoForValue(entryValueChange.getLeftValue().toString()), getAuditObjectInfoForValue(entryValueChange.getRightValue().toString()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
				}
    		} else if(change instanceof EntryRemoved) {
    			EntryRemoved entryRemoved = (EntryRemoved)change;
				AuditCollectionChangeInfo changed;
				switch (auditPropertyChangeInfo.getAuditPropertyChangeInfoTypeEnum()) {
					case MAP_ENTITY:
					case MAP_FILE:
					case MAP_VALUEOBJECT:
						changed = AuditCollectionChangeInfo.createRemovedAuditMapChangeInfo(change.getKey().toString(), getAuditObjectInfoForGlobalId((GlobalId)entryRemoved.getValue()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
					case MAP_VALUE:
						changed = AuditCollectionChangeInfo.createRemovedAuditMapChangeInfo(change.getKey().toString(), getAuditObjectInfoForValue(entryRemoved.getValue().toString()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
				}
    		} else if(change instanceof EntryAdded) {
    			EntryAdded entryAdded = (EntryAdded)change;
    			AuditCollectionChangeInfo changed;
				switch (auditPropertyChangeInfo.getAuditPropertyChangeInfoTypeEnum()) {
					case MAP_ENTITY:
					case MAP_FILE:
					case MAP_VALUEOBJECT:
						changed = AuditCollectionChangeInfo.createAddedAuditMapChangeInfo(change.getKey().toString(), getAuditObjectInfoForGlobalId((GlobalId)entryAdded.getValue()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
					case MAP_VALUE:
						changed = AuditCollectionChangeInfo.createAddedAuditMapChangeInfo(change.getKey().toString(), getAuditObjectInfoForValue(entryAdded.getValue().toString()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
				}
    		}
    	}
    	auditInfo.getAuditProperties().add(auditPropertyChangeInfo);
    }

    private void onMapChangeShowLog(MapChange mapChange) {
    	LOGGER.debug("----------- MAP CHANGE INIZIO -------------");
    	Property prop = javers.getProperty(mapChange);
    	showPropInfo(prop);
    	JaversType javersType = javers.getTypeMapping(prop.getGenericType());
    	LOGGER.debug("javersType.getClass(): " + javersType.getClass());
    	LOGGER.debug("AuditPropertyChangeInfoTypeEnum ricavato da mapChange: " + getAuditPropertyChangeInfoTypeEnumForMap(mapChange));

    	LOGGER.debug("mapChange.getPropertyName(): " + mapChange.getPropertyName());
    	LOGGER.debug("mapChange.getAffectedGlobalId(): " + mapChange.getAffectedGlobalId());
    	LOGGER.debug("mapChange.getAffectedLocalId(): " + mapChange.getAffectedLocalId());
    	LOGGER.debug("mapChange.getAffectedObject(): " + mapChange.getAffectedObject());
    	LOGGER.debug("mapChange.getCommitMetadata(): " + mapChange.getCommitMetadata());
    	LOGGER.debug("mapChange.getEntryChanges(): " + mapChange.getEntryChanges());

    	for(EntryChange change : mapChange.getEntryChanges()) {
    		LOGGER.debug("change.getClass(): " + change.getClass());
    		LOGGER.debug("change.getKey(): " + change.getKey());
			if(change instanceof EntryValueChange) {
    			EntryValueChange entryValueChange = (EntryValueChange)change;
    			if(entryValueChange.getLeftValue() instanceof GlobalId)
    				showGlobalIdInfo((GlobalId)entryValueChange.getLeftValue());
    			if(entryValueChange.getRightValue() instanceof GlobalId)
    				showGlobalIdInfo((GlobalId)entryValueChange.getRightValue());
    			LOGGER.debug("entryValueChange.getLeftValue(): " + entryValueChange.getLeftValue());
    			LOGGER.debug("entryValueChange.getRightValue(): " + entryValueChange.getRightValue());
    		} else if(change instanceof EntryRemoved) {
    			EntryRemoved entryRemoved = (EntryRemoved)change;
    			if(entryRemoved.getValue() instanceof GlobalId)
    				showGlobalIdInfo((GlobalId)entryRemoved.getValue());
    			LOGGER.debug("entryRemoved.getValue().getClass(): " + entryRemoved.getValue().getClass());
    			LOGGER.debug("entryRemoved.getValue(): " + entryRemoved.getValue());
    		} else if(change instanceof EntryAdded) {
    			EntryAdded entryAdded = (EntryAdded)change;
    			if(entryAdded.getValue() instanceof GlobalId) {
    				showGlobalIdInfo((GlobalId)entryAdded.getValue());
    			}
    			LOGGER.debug("valueAdded.getAddedValue().getClass(): " + entryAdded.getValue().getClass());
    			LOGGER.debug("valueAdded.getAddedValue(): " + entryAdded.getValue());
    		}
    	}
    	LOGGER.debug("-----------  MAP CHANGE FINE  -------------");
    }

    @Override
    public void onArrayChange(ArrayChange arrayChange) {
    	if(arrayChange.getChanges() == null || arrayChange.getChanges().size() == 0)
    		return;
    	AuditPropertyChangeInfo auditPropertyChangeInfo = new AuditPropertyChangeInfo();
    	setCommitInfo(auditPropertyChangeInfo, arrayChange.getCommitMetadata());
    	auditPropertyChangeInfo.setAuditPropertyChangeInfoTypeEnum(getAuditPropertyChangeInfoTypeEnumForArray(arrayChange));
		setLabel(auditPropertyChangeInfo, arrayChange.getAffectedGlobalId(), arrayChange.getPropertyName());
    	for(ContainerElementChange change : arrayChange.getChanges()) {
			if(change instanceof ElementValueChange) {
				ElementValueChange entryValueChange = (ElementValueChange)change;
				AuditCollectionChangeInfo changed;
				switch (auditPropertyChangeInfo.getAuditPropertyChangeInfoTypeEnum()) {
					case ARRAY_ENTITY:
					case ARRAY_FILE:
					case ARRAY_VALUEOBJECT:
						changed = AuditCollectionChangeInfo.createChangedAuditArrayChangeInfo(change.getIndex(), getAuditObjectInfoForGlobalId((GlobalId)entryValueChange.getLeftValue()), getAuditObjectInfoForGlobalId((GlobalId)entryValueChange.getRightValue()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
					case ARRAY_VALUE:
						changed = AuditCollectionChangeInfo.createChangedAuditArrayChangeInfo(change.getIndex(), getAuditObjectInfoForValue(entryValueChange.getLeftValue().toString()), getAuditObjectInfoForValue(entryValueChange.getRightValue().toString()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
				}
    		} else if(change instanceof ValueRemoved) {
    			ValueRemoved entryRemoved = (ValueRemoved)change;
				AuditCollectionChangeInfo changed;
				switch (auditPropertyChangeInfo.getAuditPropertyChangeInfoTypeEnum()) {
					case ARRAY_ENTITY:
					case ARRAY_FILE:
					case ARRAY_VALUEOBJECT:
						changed = AuditCollectionChangeInfo.createRemovedAuditArrayChangeInfo(change.getIndex(), getAuditObjectInfoForGlobalId((GlobalId)entryRemoved.getValue()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
					case ARRAY_VALUE:
						changed = AuditCollectionChangeInfo.createRemovedAuditArrayChangeInfo(change.getIndex(), getAuditObjectInfoForValue(entryRemoved.getValue().toString()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
				}
    		} else if(change instanceof ValueAdded) {
    			ValueAdded entryAdded = (ValueAdded)change;
    			AuditCollectionChangeInfo changed;
				switch (auditPropertyChangeInfo.getAuditPropertyChangeInfoTypeEnum()) {
					case ARRAY_ENTITY:
					case ARRAY_FILE:
					case ARRAY_VALUEOBJECT:
						changed = AuditCollectionChangeInfo.createAddedAuditArrayChangeInfo(change.getIndex(), getAuditObjectInfoForGlobalId((GlobalId)entryAdded.getValue()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
					case ARRAY_VALUE:
						changed = AuditCollectionChangeInfo.createAddedAuditArrayChangeInfo(change.getIndex(), getAuditObjectInfoForValue(entryAdded.getValue().toString()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
				}
    		}
    	}
    	auditInfo.getAuditProperties().add(auditPropertyChangeInfo);
    }

    @Override
    public void onListChange(ListChange listChange) {
    	if(listChange.getChanges() == null || listChange.getChanges().size() == 0)
    		return;
    	AuditPropertyChangeInfo auditPropertyChangeInfo = new AuditPropertyChangeInfo();
    	setCommitInfo(auditPropertyChangeInfo, listChange.getCommitMetadata());
    	auditPropertyChangeInfo.setAuditPropertyChangeInfoTypeEnum(getAuditPropertyChangeInfoTypeEnumForList(listChange));
		setLabel(auditPropertyChangeInfo, listChange.getAffectedGlobalId(), listChange.getPropertyName());
    	for(ContainerElementChange change : listChange.getChanges()) {
			if(change instanceof ElementValueChange) {
				ElementValueChange entryValueChange = (ElementValueChange)change;
				AuditCollectionChangeInfo changed;
				switch (auditPropertyChangeInfo.getAuditPropertyChangeInfoTypeEnum()) {
					case LIST_ENTITY:
					case LIST_FILE:
					case LIST_VALUEOBJECT:
						changed = AuditCollectionChangeInfo.createChangedAuditListChangeInfo(change.getIndex(), getAuditObjectInfoForGlobalId((GlobalId)entryValueChange.getLeftValue()), getAuditObjectInfoForGlobalId((GlobalId)entryValueChange.getRightValue()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
					case LIST_VALUE:
						changed = AuditCollectionChangeInfo.createChangedAuditListChangeInfo(change.getIndex(), getAuditObjectInfoForValue(entryValueChange.getLeftValue().toString()), getAuditObjectInfoForValue(entryValueChange.getRightValue().toString()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
				}
    		} else if(change instanceof ValueRemoved) {
    			ValueRemoved entryRemoved = (ValueRemoved)change;
				AuditCollectionChangeInfo changed;
				switch (auditPropertyChangeInfo.getAuditPropertyChangeInfoTypeEnum()) {
					case LIST_ENTITY:
					case LIST_FILE:
					case LIST_VALUEOBJECT:
						changed = AuditCollectionChangeInfo.createRemovedAuditListChangeInfo(change.getIndex(), getAuditObjectInfoForGlobalId((GlobalId)entryRemoved.getValue()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
					case LIST_VALUE:
						changed = AuditCollectionChangeInfo.createRemovedAuditListChangeInfo(change.getIndex(), getAuditObjectInfoForValue(entryRemoved.getValue().toString()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
				}
    		} else if(change instanceof ValueAdded) {
    			ValueAdded entryAdded = (ValueAdded)change;
    			AuditCollectionChangeInfo changed;
				switch (auditPropertyChangeInfo.getAuditPropertyChangeInfoTypeEnum()) {
					case LIST_ENTITY:
					case LIST_FILE:
					case LIST_VALUEOBJECT:
						changed = AuditCollectionChangeInfo.createAddedAuditListChangeInfo(change.getIndex(), getAuditObjectInfoForGlobalId((GlobalId)entryAdded.getValue()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
					case LIST_VALUE:
						changed = AuditCollectionChangeInfo.createAddedAuditListChangeInfo(change.getIndex(), getAuditObjectInfoForValue(entryAdded.getValue().toString()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
				}
    		}
    	}
    	auditInfo.getAuditProperties().add(auditPropertyChangeInfo);
    }

    @Override
    public void onSetChange(SetChange setChange) {
    	if(LOGGER.isDebugEnabled())
    		onSetChangeShowLog(setChange);
    	if(setChange.getChanges() == null || setChange.getChanges().size() == 0)
    		return;
    	AuditPropertyChangeInfo auditPropertyChangeInfo = new AuditPropertyChangeInfo();
    	setCommitInfo(auditPropertyChangeInfo, setChange.getCommitMetadata());
    	auditPropertyChangeInfo.setAuditPropertyChangeInfoTypeEnum(getAuditPropertyChangeInfoTypeEnumForSet(setChange));
		setLabel(auditPropertyChangeInfo, setChange.getAffectedGlobalId(), setChange.getPropertyName());
    	for(ContainerElementChange change : setChange.getChanges()) {
			if(change instanceof ElementValueChange) {
				ElementValueChange entryValueChange = (ElementValueChange)change;
				AuditCollectionChangeInfo changed;
				switch (auditPropertyChangeInfo.getAuditPropertyChangeInfoTypeEnum()) {
					case SET_ENTITY:
					case SET_FILE:
					case SET_VALUEOBJECT:
						changed = AuditCollectionChangeInfo.createChangedAuditSetChangeInfo(getAuditObjectInfoForGlobalId((GlobalId)entryValueChange.getLeftValue()), getAuditObjectInfoForGlobalId((GlobalId)entryValueChange.getRightValue()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
					case SET_VALUE:
						changed = AuditCollectionChangeInfo.createChangedAuditSetChangeInfo(getAuditObjectInfoForValue(entryValueChange.getLeftValue().toString()), getAuditObjectInfoForValue(entryValueChange.getRightValue().toString()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
				}
    		} else if(change instanceof ValueRemoved) {
    			ValueRemoved entryRemoved = (ValueRemoved)change;
				AuditCollectionChangeInfo changed;
				switch (auditPropertyChangeInfo.getAuditPropertyChangeInfoTypeEnum()) {
					case SET_ENTITY:
					case SET_FILE:
					case SET_VALUEOBJECT:
						changed = AuditCollectionChangeInfo.createRemovedAuditSetChangeInfo(getAuditObjectInfoForGlobalId((GlobalId)entryRemoved.getValue()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
					case SET_VALUE:
						changed = AuditCollectionChangeInfo.createRemovedAuditSetChangeInfo(getAuditObjectInfoForValue(entryRemoved.getValue().toString()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
				}
    		} else if(change instanceof ValueAdded) {
    			ValueAdded entryAdded = (ValueAdded)change;
    			AuditCollectionChangeInfo changed;
				switch (auditPropertyChangeInfo.getAuditPropertyChangeInfoTypeEnum()) {
					case SET_ENTITY:
					case SET_FILE:
					case SET_VALUEOBJECT:
						changed = AuditCollectionChangeInfo.createAddedAuditSetChangeInfo(getAuditObjectInfoForGlobalId((GlobalId)entryAdded.getValue()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
					case SET_VALUE:
						changed = AuditCollectionChangeInfo.createAddedAuditSetChangeInfo(getAuditObjectInfoForValue(entryAdded.getValue().toString()));
						auditPropertyChangeInfo.getAuditCollectionChangeInfo().add(changed);
						break;
				}
    		}
    	}
    	auditInfo.getAuditProperties().add(auditPropertyChangeInfo);
    }

    private void onSetChangeShowLog(SetChange setChange) {
    	LOGGER.debug("----------- SET CHANGE INIZIO -------------");
    	Property prop = javers.getProperty(setChange);
    	showPropInfo(prop);
    	JaversType javersType = javers.getTypeMapping(prop.getGenericType());
    	LOGGER.debug("javersType.getClass(): " + javersType.getClass());
    	LOGGER.debug("setChange.getPropertyName(): " + setChange.getPropertyName());
    	LOGGER.debug("setChange.getAffectedGlobalId(): " + setChange.getAffectedGlobalId());
    	LOGGER.debug("setChange.getAffectedLocalId(): " + setChange.getAffectedLocalId());
    	LOGGER.debug("setChange.getAffectedObject(): " + setChange.getAffectedObject());
    	LOGGER.debug("setChange.getCommitMetadata(): " + setChange.getCommitMetadata());
    	LOGGER.debug("setChange.getChanges(): " + setChange.getChanges());

    	for(ContainerElementChange change : setChange.getChanges()) {
    		LOGGER.debug("change.getClass(): " + change.getClass());
    		LOGGER.debug("change.getIndex(): " + change.getIndex());
    			if(change instanceof ElementValueChange) {
        			ElementValueChange elementValueChange = (ElementValueChange)change;
	    			LOGGER.debug("elementValueChange.getLeftValue(): " + elementValueChange.getLeftValue());
	    			LOGGER.debug("elementValueChange.getRightValue(): " + elementValueChange.getRightValue());
	    		} else if(change instanceof ValueRemoved) {
	    			ValueRemoved valueRemoved = (ValueRemoved)change;
	    			LOGGER.debug("valueRemoved.getRemovedValue().getClass(): " + valueRemoved.getRemovedValue().getClass());
	    			LOGGER.debug("valueRemoved.getRemovedValue(): " + valueRemoved.getRemovedValue());
	    		} else if(change instanceof ValueAdded) {
	    			ValueAdded valueAdded = (ValueAdded)change;
	    			if(valueAdded.getAddedValue() instanceof InstanceId) {
	    				InstanceId instanceId = (InstanceId)valueAdded.getAddedValue();
		    			LOGGER.debug("instanceId.getTypeName(): " + instanceId.getTypeName());
		    			LOGGER.debug("getEntityIdFromGlobalId(instanceId): " + getEntityIdFromGlobalId(instanceId));
		    			LOGGER.debug("instanceId.getCdoId().toString(): " + instanceId.getCdoId().toString());
		    			LOGGER.debug("instanceId.value(): " + instanceId.value());
	    			}
	    			LOGGER.debug("valueAdded.getAddedValue().getClass(): " + valueAdded.getAddedValue().getClass());
	    			LOGGER.debug("valueAdded.getAddedValue(): " + valueAdded.getAddedValue());
	    		}
    	}
    	LOGGER.debug("-----------  SET CHANGE FINE  -------------");
    }

    @Override
    public AuditInfo result(){
        return auditInfo;
    }

}
