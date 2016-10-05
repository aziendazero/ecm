package it.tredi.ecm.dao.entity;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FieldEditabileEvento extends Field {
	@ManyToOne
	private EventoPianoFormativo evento;
	public FieldEditabileEvento() {}
	public FieldEditabileEvento(IdFieldEnum idField, EventoPianoFormativo evento) {
		super.setIdField(idField);
		setEvento(evento);
	}
	public FieldEditabileEvento(IdFieldEnum idField, EventoPianoFormativo evento, Long objectReference) {
		super.setIdField(idField);
		setEvento(evento);
		super.setObjectReference(objectReference);
	}
	
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldEditabileEvento entitapiatta = (FieldEditabileEvento) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
