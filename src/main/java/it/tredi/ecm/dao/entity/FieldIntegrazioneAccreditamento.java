package it.tredi.ecm.dao.entity;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class FieldIntegrazioneAccreditamento extends FieldIntegrazione{
	@ManyToOne
	private Accreditamento accreditamento;
	
	public FieldIntegrazioneAccreditamento(){}
	
	public FieldIntegrazioneAccreditamento(IdFieldEnum idField, Accreditamento accreditamento, Object newValue, TipoIntegrazioneEnum tipoIntegrazione) {
		super.setIdField(idField);
		setAccreditamento(accreditamento);
		setNewValue(newValue);
		setTipoIntegrazioneEnum(tipoIntegrazione);
	}
	public FieldIntegrazioneAccreditamento(IdFieldEnum idField, Accreditamento accreditamento, Long objectReference, Object newValue, TipoIntegrazioneEnum tipoIntegrazione) {
		super.setIdField(idField);
		setAccreditamento(accreditamento);
		super.setObjectReference(objectReference);
		setNewValue(newValue);
		setTipoIntegrazioneEnum(tipoIntegrazione);
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldIntegrazioneAccreditamento entitapiatta = (FieldIntegrazioneAccreditamento) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
