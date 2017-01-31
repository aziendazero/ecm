package it.tredi.ecm.audit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Optional;

import org.javers.common.exception.JaversException;
import org.javers.core.Javers;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.*;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ManagedType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.service.AuditService;
import it.tredi.ecm.web.AuditController;

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
    }

    @Override
    public void onAffectedObject(GlobalId globalId) {
    	//globalId.getTypeName() restituisce il tipo dell'oggetto nel sia settato caso withTypeName restituisce questo
    }

    @Override
    public void onValueChange(ValueChange valueChange) {
    	if(leftAndRightLikeEmpty(valueChange))
    		return;
    	auditInfo.getAuditProperties().add(auditPropertyChangeInfo(valueChange));
    }

    private AuditPropertyChangeInfo auditPropertyChangeInfo(ValueChange valueChange) {
    	AuditPropertyChangeInfo auditPropertyChangeInfo = new AuditPropertyChangeInfo();
    	auditPropertyChangeInfo.setValueChange(valueChange);
    	Account account = null;
    	if(valueChange.getCommitMetadata().isPresent() && valueChange.getCommitMetadata().get().getAuthor() != null) {
        	auditPropertyChangeInfo.setDataModifica(valueChange.getCommitMetadata().get().getCommitDate());
        	auditPropertyChangeInfo.setUserName(valueChange.getCommitMetadata().get().getAuthor());
    		Optional<Account> optAcc = accountService.getUserByUsername(valueChange.getCommitMetadata().get().getAuthor());
    		if(optAcc.isPresent())
    			account = optAcc.get();
    	}
    	auditPropertyChangeInfo.setAccount(account);

		setLabel(auditPropertyChangeInfo, valueChange.getAffectedGlobalId(), valueChange.getPropertyName());
		return auditPropertyChangeInfo;
	}

	private void setLabel(AuditPropertyChangeInfo auditPropertyChangeInfo, GlobalId globalId, String propertyName) {
		LOGGER.info("propertyName: " + propertyName);
		auditPropertyChangeInfo.setPropertyLabelInfo(AuditUtils.getTypeNameWithoutPackage(globalId.getTypeName()) + "." + propertyName);
		//    InstanceId, UnboundedValueObjectId, ValueObjectId
		String propertyOwnerObject;

		if(globalId instanceof InstanceId) {
			//propertyOwnerObject = AuditUtils.getTypeNameWithoutPackage(globalId.getTypeName());
			//LOGGER.info("propertyOwnerObject: " + propertyOwnerObject);
			InstanceId instanceId = (InstanceId)globalId;
			LOGGER.info("instanceId.getCdoId().toString(): " + instanceId.getCdoId().toString());
		} else if (globalId instanceof ValueObjectId) {
			ValueObjectId valueObjectId = (ValueObjectId)globalId;
			propertyOwnerObject = AuditUtils.getTypeNameWithoutPackage(valueObjectId.getOwnerId().getTypeName());
			LOGGER.info("propertyOwnerObject: " + propertyOwnerObject);
			//Ricavo l'albero delle label
			// esempi getFragment()
			// programma/0						propertyName: giorno		propertyOwnerObject: ProgrammaGiornalieroRES
			// programma/1/sede					propertyName: provincia		propertyOwnerObject: SedeEvento
			// programma/0/programma/1			propertyName: orarioInizio	propertyOwnerObject: DettaglioAttivitaRES

			//splitto
			if(valueObjectId.getFragment().isEmpty()) {
				auditPropertyChangeInfo.setPropertyLabelInfo(propertyOwnerObject + "." + propertyName);
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
						LOGGER.debug("prop.getName(): " + prop.getName());
						AuditLabelInfo auditLabelInfo = new AuditLabelInfo();

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

			LOGGER.info("valueObjectId.getFragment(): " + valueObjectId.getFragment());
			LOGGER.info("valueObjectId.getTypeName(): " + valueObjectId.getTypeName());
			LOGGER.info("valueObjectId.value(): " + valueObjectId.value());
			LOGGER.info("valueObjectId.getOwnerId().toString(): " + valueObjectId.getOwnerId().toString());
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

    @Override
    public void onReferenceChange(ReferenceChange referenceChange) {
    }

    @Override
    public void onNewObject(NewObject newObject) {
    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {
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
    }

    @Override
    public AuditInfo result(){
        return auditInfo;
    }

}
