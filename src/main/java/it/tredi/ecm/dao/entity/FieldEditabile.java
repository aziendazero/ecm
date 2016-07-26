package it.tredi.ecm.dao.entity;

import java.util.Objects;

import javax.persistence.Entity;

import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FieldEditabile extends Field {
	public FieldEditabile() {}
	public FieldEditabile(IdFieldEnum idField, Accreditamento accreditamento) {
		super.setIdField(idField);
		super.setAccreditamento(accreditamento);
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldEditabile entitapiatta = (FieldEditabile) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
