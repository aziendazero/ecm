package it.tredi.ecm.dao.entity;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Valutazione extends BaseEntity{
	private int campo;
	private boolean esito;
	private String valutazione;
	
	@ManyToOne
	private Accreditamento accreditamento;
	
	@ManyToOne
	private Persona valutatore;
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Valutazione entitapiatta = (Valutazione) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
