package it.tredi.ecm.audit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import org.javers.common.exception.JaversException;
import org.javers.core.Javers;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.*;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ElementValueChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.container.ValueAdded;
import org.javers.core.diff.changetype.container.ValueRemoved;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.ValueObjectType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.service.AuditService;

public class EcmAuditInfoChangeLog extends AbstractAuditInfoChangeLog {
	public static final Logger LOGGER = LoggerFactory.getLogger(EcmAuditInfoChangeLog.class);

    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormat.mediumDateTime();

    private final DateTimeFormatter dateTimeFormatter;

    private AuditInfo auditInfo;
    private AccountService accountService;
    private AuditService auditService;
    private Javers javers;
    private JaversType javersType;

    public EcmAuditInfoChangeLog(Class entityClass, Long entityId, Javers javers, AuditService auditService, AccountService accountService) {
        this(entityClass, entityId, javers, auditService, accountService, DEFAULT_DATE_FORMATTER);
    }

    public EcmAuditInfoChangeLog(Class entityClass, Long entityId, Javers javers, AuditService auditService, AccountService accountService, DateTimeFormatter dateTimeFormatter) {
        this.auditInfo = new AuditInfo(entityId, entityClass);
        this.javers = javers;
        this.auditService = auditService;
        this.accountService = accountService;
        this.dateTimeFormatter = dateTimeFormatter;

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

	private void setLabel(AuditPropertyChangeInfo auditPropertyChangeInfo, GlobalId globalId, String propertyName) {
//		LOGGER.info("propertyName: " + propertyName);
		auditPropertyChangeInfo.setPropertyLabelInfo(AuditUtils.getTypeNameWithoutPackage(globalId.getTypeName()) + "." + propertyName);
		//    InstanceId, UnboundedValueObjectId, ValueObjectId
		String propertyOwnerObject;

		if(globalId instanceof InstanceId) {
			//propertyOwnerObject = AuditUtils.getTypeNameWithoutPackage(globalId.getTypeName());
			//LOGGER.info("propertyOwnerObject: " + propertyOwnerObject);
			InstanceId instanceId = (InstanceId)globalId;
//			LOGGER.info("instanceId.getCdoId().toString(): " + instanceId.getCdoId().toString());
		} else if (globalId instanceof ValueObjectId) {
			ValueObjectId valueObjectId = (ValueObjectId)globalId;
			propertyOwnerObject = AuditUtils.getTypeNameWithoutPackage(valueObjectId.getOwnerId().getTypeName());
//			LOGGER.info("propertyOwnerObject: " + propertyOwnerObject);
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
			} else {
				LOGGER.info("valueObjectId.getFragment(): " + valueObjectId.getFragment() + " - propertyName: " + propertyName);
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
						        //TODO
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
						    }
							if(!labelPath.isEmpty())
								labelPath += ".";
							labelPath += prop.getName();
							auditLabelInfo.setPropertyLabel(labelPath);

							auditPropertyChangeInfo.getAuditLabelInfos().add(auditLabelInfo);
					    }
					}
				}
			}

//			LOGGER.info("valueObjectId.getFragment(): " + valueObjectId.getFragment());
//			LOGGER.info("valueObjectId.getTypeName(): " + valueObjectId.getTypeName());
//			LOGGER.info("valueObjectId.value(): " + valueObjectId.value());
//			LOGGER.info("valueObjectId.getOwnerId().toString(): " + valueObjectId.getOwnerId().toString());
			//LOGGER.debug(valueObjectId.hasOwnerOfType(entityType));
		}
		auditPropertyChangeInfo.setLabel(auditService.getLabelForAuditProperty(auditPropertyChangeInfo.getAuditLabelInfos(), auditPropertyChangeInfo.getPropertyLabelInfo()));
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
		LOGGER.info("-------------- PROPERTY INFO INIZIO --------------");
		if(prop==null) {
			LOGGER.info("prop = NULL");
			return;
		}
		LOGGER.info("Property: " + prop.toString());
		LOGGER.info("prop.getName(): " + prop.getName());
		LOGGER.info("prop.getGenericType(): " + prop.getGenericType());
		LOGGER.info("prop.getRawType(): " + prop.getRawType());
		LOGGER.info("prop.hasShallowReferenceAnn(): " + prop.hasShallowReferenceAnn());
		LOGGER.info("prop.looksLikeId(): " + prop.looksLikeId());
		LOGGER.info("prop.getMember(): " + prop.getMember());
		LOGGER.info("--------------  PROPERTY INFO FINE  --------------");
    }

    private void showGlobalIdInfo(GlobalId globalId) {
		if(globalId instanceof InstanceId) {
			InstanceId instanceId = (InstanceId)globalId;
			LOGGER.info("showGlobalIdInfo - instanceId.getCdoId().toString(): " + instanceId.getCdoId().toString());
		} else if (globalId instanceof ValueObjectId) {
			ValueObjectId valueObjectId = (ValueObjectId)globalId;
			LOGGER.info("valueObjectId - valueObjectId.getFragment(): " + valueObjectId.getFragment());
		}
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
    		LOGGER.info("onReferenceChange di oggetto ne ValueObjectType ne EntityType");
    		return;
    	}

    	EntityType entityType = (EntityType)javersType;
    	//entityType.getConcreteClassTypeArguments()
    	if(entityType.getBaseJavaClass().equals(it.tredi.ecm.dao.entity.File.class)) {
    		//la property in modifica è un file
        	LOGGER.info("onReferenceChange File in modifica");
        	LOGGER.info("onReferenceChange File in modifica referenceChange.getLeft(): " + referenceChange.getLeft());
        	showGlobalIdInfo(referenceChange.getLeft());
        	LOGGER.info("onReferenceChange File in modifica referenceChange.getRight(): " + referenceChange.getRight());
        	showGlobalIdInfo(referenceChange.getRight());
        	LOGGER.info("onReferenceChange File in modifica referenceChange.getPropertyName(): " + referenceChange.getPropertyName());
        	LOGGER.info("onReferenceChange File in modifica referenceChange.getAffectedGlobalId(): " + referenceChange.getAffectedGlobalId());
        	LOGGER.info("onReferenceChange File in modifica referenceChange.getAffectedLocalId(): " + referenceChange.getAffectedLocalId());
        	LOGGER.info("onReferenceChange File in modifica referenceChange.getAffectedObject(): " + referenceChange.getAffectedObject());

        	AuditPropertyChangeInfo auditPropertyChangeInfo = new AuditPropertyChangeInfo();
        	auditPropertyChangeInfo.setAuditPropertyChangeInfoTypeEnum(AuditPropertyChangeInfoTypeEnum.FILE);
        	auditPropertyChangeInfo.setPreviousId(getEntityIdFromGlobalId(referenceChange.getLeft()));
        	auditPropertyChangeInfo.setAfterId(getEntityIdFromGlobalId(referenceChange.getRight()));
        	auditPropertyChangeInfo.setReferenceChange(referenceChange);
        	setCommitInfo(auditPropertyChangeInfo, referenceChange.getCommitMetadata());

    		setLabel(auditPropertyChangeInfo, referenceChange.getAffectedGlobalId(), referenceChange.getPropertyName());

    		auditInfo.getAuditProperties().add(auditPropertyChangeInfo);
    	} else {
        	AuditPropertyChangeInfo auditPropertyChangeInfo = new AuditPropertyChangeInfo();
        	auditPropertyChangeInfo.setAuditPropertyChangeInfoTypeEnum(AuditPropertyChangeInfoTypeEnum.ENTITY);
        	auditPropertyChangeInfo.setEntity(AuditUtils.getTypeNameWithoutPackage(entityType.getBaseJavaClass().getName()));
        	auditPropertyChangeInfo.setPreviousId(getEntityIdFromGlobalId(referenceChange.getLeft()));
        	auditPropertyChangeInfo.setAfterId(getEntityIdFromGlobalId(referenceChange.getRight()));
        	auditPropertyChangeInfo.setReferenceChange(referenceChange);
        	setCommitInfo(auditPropertyChangeInfo, referenceChange.getCommitMetadata());

    		setLabel(auditPropertyChangeInfo, referenceChange.getAffectedGlobalId(), referenceChange.getPropertyName());

    		auditInfo.getAuditProperties().add(auditPropertyChangeInfo);
    	}

//
//    	LOGGER.info("onReferenceChange entityType.getBaseJavaClass()" + entityType.getBaseJavaClass().toString());
//    	LOGGER.info("onReferenceChange entityType.getBaseJavaType()" + entityType.getBaseJavaType().toString());
//
//    	getPropertyChangeInfo(referenceChange);
//    	//Quando cambia una property non di tipo valore sia che sia Entity o che sia Embedded (anche taggate come @ShallowReference)
//    	//referenceChange.getLeft() e referenceChange.getRight() contengono l'identificativo dell'oggetto prima e dopo la modifica
//    	LOGGER.info("----------- REFERENCE CHANGE INIZIO -------------");
//    	LOGGER.info(referenceChange.toString());
//
//
//    	//Se il reference (la properties che sto modificando) è un embedded (javers ValuleObject) vengono anche elencate le modifiche di tutte le relative property nel valueChange quindi lo saltiamo
//    	getInfoOnCurrentProperty(referenceChange.getAffectedGlobalId(), referenceChange.getPropertyName());
//
//
//    	//if(leftAndRightLikeEmpty(valueChange))
//    	//	return;
//    	//auditInfo.getAuditProperties().add(auditPropertyChangeInfo(valueChange));
//
//    	GlobalId globalId = referenceChange.getAffectedGlobalId();
//		if(globalId instanceof InstanceId) {
//			InstanceId instanceId = (InstanceId)globalId;
//			LOGGER.info("instanceId - instanceId.getCdoId().toString(): " + instanceId.getCdoId().toString());
//		} else if (globalId instanceof ValueObjectId) {
//			ValueObjectId valueObjectId = (ValueObjectId)globalId;
//			String propertyOwnerObject;
//			propertyOwnerObject = AuditUtils.getTypeNameWithoutPackage(valueObjectId.getOwnerId().getTypeName());
//			LOGGER.info("ValueObjectId - propertyOwnerObject: " + propertyOwnerObject);
//		}

//    	LOGGER.info("referenceChange.getPropertyName(): " + referenceChange.getPropertyName());
//    	LOGGER.info("referenceChange.getAffectedGlobalId(): " + referenceChange.getAffectedGlobalId());
//    	LOGGER.info("referenceChange.getAffectedLocalId(): " + referenceChange.getAffectedLocalId());
//    	LOGGER.info("referenceChange.getAffectedObject(): " + referenceChange.getAffectedObject());
//    	LOGGER.info("referenceChange.getCommitMetadata(): " + referenceChange.getCommitMetadata());
//    	LOGGER.info("referenceChange.getLeft(): " + referenceChange.getLeft());
//    	LOGGER.info("referenceChange.getRight(): " + referenceChange.getRight());
    	LOGGER.info("----------- REFERENCE CHANGE FINE -------------");
    }

    @Override
    public void onNewObject(NewObject newObject) {
    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {
    	//Chiamata quando l'oggetto è stato cancellatao
    }

    @Override
    public void onMapChange(MapChange mapChange) {
    }

    @Override
    public void onArrayChange(ArrayChange arrayChange) {
    }

    @Override
    public void onListChange(ListChange listChange) {
    }

    @Override
    public void onSetChange(SetChange setChange) {
    	LOGGER.info("----------- SET CHANGE INIZIO -------------");
    	Property prop = javers.getProperty(setChange);
    	showPropInfo(prop);
    	JaversType javersType = javers.getTypeMapping(prop.getGenericType());
    	LOGGER.info("javersType.getClass(): " + javersType.getClass());
//    	if(javersType instanceof ValueObjectType) {
//    		LOGGER.info("onSetChange di un ValueObjectType return");
//    		return;
//    	} else if(javersType instanceof EntityType)
//    		LOGGER.info("onSetChange di un EntityType");
//    	else {
//    		LOGGER.info("onSetChange di oggetto ne ValueObjectType ne EntityType");
//    		return;
//    	}

    	LOGGER.info("setChange.getPropertyName(): " + setChange.getPropertyName());
    	LOGGER.info("setChange.getAffectedGlobalId(): " + setChange.getAffectedGlobalId());
    	LOGGER.info("setChange.getAffectedLocalId(): " + setChange.getAffectedLocalId());
    	LOGGER.info("setChange.getAffectedObject(): " + setChange.getAffectedObject());
    	LOGGER.info("setChange.getCommitMetadata(): " + setChange.getCommitMetadata());
    	LOGGER.info("setChange.getChanges(): " + setChange.getChanges());

    	for(ContainerElementChange change : setChange.getChanges()) {
    		LOGGER.info("setChange.getChanges(): " + change.getClass());
    		LOGGER.info("setChange.getIndex(): " + change.getIndex());
    			if(change instanceof ElementValueChange) {
        			ElementValueChange elementValueChange = (ElementValueChange)change;
	    			LOGGER.info("elementValueChange.getLeftValue(): " + elementValueChange.getLeftValue());
	    			LOGGER.info("elementValueChange.getRightValue(): " + elementValueChange.getRightValue());
	    		} else if(change instanceof ValueRemoved) {
	    			ValueRemoved valueRemoved = (ValueRemoved)change;
	    			LOGGER.info("valueRemoved.getRemovedValue().getClass(): " + valueRemoved.getRemovedValue().getClass());
	    			LOGGER.info("valueRemoved.getRemovedValue(): " + valueRemoved.getRemovedValue());
	    		} else if(change instanceof ValueAdded) {
	    			ValueAdded valueAdded = (ValueAdded)change;
	    			if(valueAdded.getAddedValue() instanceof InstanceId) {
	    				InstanceId instanceId = (InstanceId)valueAdded.getAddedValue();
		    			LOGGER.info("instanceId.getTypeName(): " + instanceId.getTypeName());
		    			LOGGER.info("getEntityIdFromGlobalId(instanceId): " + getEntityIdFromGlobalId(instanceId));
		    			LOGGER.info("instanceId.getCdoId().toString(): " + instanceId.getCdoId().toString());

		    			LOGGER.info("instanceId.value(): " + instanceId.value());
	    			}
	    			LOGGER.info("valueAdded.getAddedValue().getClass(): " + valueAdded.getAddedValue().getClass());
	    			LOGGER.info("valueAdded.getAddedValue(): " + valueAdded.getAddedValue());
	    		}

    	}

    	LOGGER.info("-----------  SET CHANGE FINE  -------------");
    }

    @Override
    public AuditInfo result(){
        return auditInfo;
    }

}
