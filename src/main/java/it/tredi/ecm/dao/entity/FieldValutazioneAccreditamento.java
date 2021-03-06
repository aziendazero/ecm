package it.tredi.ecm.dao.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FieldValutazioneAccreditamento extends Field{
	@ManyToOne
	private Accreditamento accreditamento;
	private Boolean esito;
	@Column(columnDefinition="text")
	private String note;
	private boolean enabled = true;
	private Boolean modificatoInIntegrazione = null;

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldValutazioneAccreditamento entitapiatta = (FieldValutazioneAccreditamento) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
