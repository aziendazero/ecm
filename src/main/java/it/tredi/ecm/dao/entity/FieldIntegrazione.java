package it.tredi.ecm.dao.entity;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class FieldIntegrazione extends Field{
	@Transient
	private Object newValue;
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldIntegrazione entitapiatta = (FieldIntegrazione) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
