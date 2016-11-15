package it.tredi.ecm.dao.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonView;

@MappedSuperclass
public class BaseEntity implements Cloneable, Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = -6484707207565905889L;
	@JsonView({JsonViewModel.Integrazione.class, JsonViewModel.ComunicazioniDestinatari.class})
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
    protected Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public boolean isNew() {
        return (this.id == null);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
	public Object clone() throws CloneNotSupportedException {
    	return super.clone();
    }
}
