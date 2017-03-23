package it.tredi.ecm.audit.entity;

import java.util.Objects;

import org.javers.core.metamodel.annotation.TypeName;
import org.javers.core.metamodel.annotation.ValueObject;

import it.tredi.ecm.dao.entity.Sede;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@TypeName("SedeAudit")
@Getter
@Setter
public class SedeAudit {
	private Long id;
	private String provincia;
	private String comune;
	private String indirizzo;
	private String cap;
	private String telefono;
	private String altroTelefono;
	private String fax;
	private String email;

	//boolean controllo tipologia sede (default operativa)
	private boolean sedeLegale = false;
	private boolean sedeOperativa = true;

	public SedeAudit(Sede sede) {
		this.id = sede.getId();
		this.provincia = sede.getProvincia();
		this.comune = sede.getComune();
		this.indirizzo = sede.getIndirizzo();
		this.cap = sede.getCap();
		this.telefono = sede.getTelefono();
		this.altroTelefono = sede.getAltroTelefono();
		this.fax = sede.getFax();
		this.email = sede.getEmail();
		this.sedeLegale = sede.isSedeLegale();
		this.sedeOperativa = sede.isSedeOperativa();
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SedeAudit entitapiatta = (SedeAudit) o;
        return Objects.equals(id, entitapiatta.id);
    }

	@Override
	public int hashCode() {
		return id.intValue();
	}
}
