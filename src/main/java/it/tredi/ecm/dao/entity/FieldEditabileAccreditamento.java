package it.tredi.ecm.dao.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FieldEditabileAccreditamento extends Field {
	@Column(columnDefinition="text")
	private String nota;
	@ManyToOne
	private Accreditamento accreditamento;

	public FieldEditabileAccreditamento() {}
	public FieldEditabileAccreditamento(IdFieldEnum idField, Accreditamento accreditamento) {
		super.setIdField(idField);
		setAccreditamento(accreditamento);
	}
	public FieldEditabileAccreditamento(IdFieldEnum idField, Accreditamento accreditamento, Long objectReference) {
		super.setIdField(idField);
		setAccreditamento(accreditamento);
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
        FieldEditabileAccreditamento entitapiatta = (FieldEditabileAccreditamento) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
