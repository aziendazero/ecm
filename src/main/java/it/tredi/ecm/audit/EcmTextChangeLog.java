package it.tredi.ecm.audit;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Sample text changeLog, renders text log like that:
 * <pre>
 * commit 3.0, author:another author, 2014-12-06 13:22:51
 *   changed object: org.javers.core.model.DummyUser/bob
 *     value changed on 'sex' property: 'null' -> 'FEMALE'
 *     set changed on 'stringSet' property: [removed:'groovy', added:'java', added:'scala']
 *     list changed on 'integerList' property: [(0).added:'22', (1).added:'23']
 * commit 2.0, author:some author, 2014-12-06 13:22:51
 *     value changed on 'age' property: '0' -> '18'
 *     value changed on 'surname' property: 'Dijk' -> 'van Dijk'
 *     reference changed on 'supervisor' property: 'null' -> 'org.javers.core.model.DummyUser/New Supervisor'
 * </pre>
 *
 * @author bartosz walacik
 */
public class EcmTextChangeLog extends AbstractTextChangeLog {
    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormat.mediumDateTime();

    private final DateTimeFormatter dateTimeFormatter;

    public EcmTextChangeLog(Long entityId, Class entityClass) {
        this(DEFAULT_DATE_FORMATTER, entityId, entityClass);
    }

    public EcmTextChangeLog(DateTimeFormatter dateTimeFormatter, Long entityId, Class entityClass) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public void onCommit(CommitMetadata commitMetadata) {
        appendln("commit " + commitMetadata.getId().value() + ", author: " + commitMetadata.getAuthor() +
                ", " + dateTimeFormatter.print(commitMetadata.getCommitDate()));
    }

    @Override
    public void onAffectedObject(GlobalId globalId) {
    	//globalId.getTypeName() restituisce il tipo dell'oggetto se l'attributo withTypeName non è impostato altrimenti restituisce withTypeName
        appendln("  changed object: globalId.value(): " + globalId.value() + " - globalId.getTypeName(): " + globalId.getTypeName());
    }

    @Override
    public void onValueChange(ValueChange valueChange) {
        appendln("    value changed on '"+valueChange.getPropertyName()+"' property: '"+ valueChange.getLeft() +
                 "' -> '" + valueChange.getRight() + "'" +
        		" [" + "getAffectedGlobalId: " +  valueChange.getAffectedGlobalId() +
        			   "; getAffectedGlobalId.getTypeName: " +  valueChange.getAffectedGlobalId().getTypeName() +
        			   "; getAffectedLocalId: " +  valueChange.getAffectedLocalId() +
        			   "; getAffectedObject: " +  valueChange.getAffectedObject() +
        			   "; getCommitMetadata().: " +  valueChange.getCommitMetadata().get().getAuthor() + " - " + valueChange.getCommitMetadata().get().getCommitDate() +
                 "]");
    }

    @Override
    public void onReferenceChange(ReferenceChange referenceChange) {
        appendln("    reference changed on '" + referenceChange.getPropertyName() + "' property: '" + referenceChange.getLeft() +
                "' -> '" + referenceChange.getRight() + "'");
    }

    @Override
    public void onNewObject(NewObject newObject) {
        appendln("    new object: " + newObject.getAffectedGlobalId());
    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {
        appendln("    object removed: '" + objectRemoved.getAffectedGlobalId());
    }

    @Override
    public void onMapChange(MapChange mapChange) {
        appendln("    map changed on '" + mapChange.getPropertyName() + "' property: " +
                mapChange.getEntryChanges());
    }

    @Override
    public void onArrayChange(ArrayChange arrayChange) {
        appendln("    array changed on '" + arrayChange.getPropertyName() + "' property: " +
                arrayChange.getChanges());
    }

    @Override
    public void onListChange(ListChange listChange) {
        appendln("    list changed on '" + listChange.getPropertyName() + "' property: " +
                listChange.getChanges());
    }

    @Override
    public void onSetChange(SetChange setChange) {
        appendln("    set changed on '" + setChange.getPropertyName() + "' property: "+
                 setChange.getChanges());
    }

}
