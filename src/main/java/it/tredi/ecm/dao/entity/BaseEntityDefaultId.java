package it.tredi.ecm.dao.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonView;

@MappedSuperclass
public class BaseEntityDefaultId extends BaseEntity implements Cloneable, Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = -6484707207565905889L;
	@JsonView({JsonViewModel.Integrazione.class, JsonViewModel.ComunicazioniDestinatari.class, JsonViewModel.EventoLookup.class})
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
    protected Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
