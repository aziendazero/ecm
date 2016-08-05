package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;

import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Setter
@Getter
public abstract class FieldIntegrazione extends Field{
	
	@Type(type = "serializable")
	private Object newValue;
	@Column(name="data_modifica")
	private LocalDate dataModifica;
	@Enumerated(EnumType.STRING)
	private TipoIntegrazioneEnum tipoIntegrazioneEnum;
	
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
