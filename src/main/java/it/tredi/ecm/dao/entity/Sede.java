package it.tredi.ecm.dao.entity;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="sede")
@Getter
@Setter
public class Sede extends BaseEntity{
	private String provincia;
	private String comune;
	private String indirizzo;
	private String cap;
	private String telefono;
	private String altroTelefono;
	private String fax;
	private String email;
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sede entitapiatta = (Sede) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
