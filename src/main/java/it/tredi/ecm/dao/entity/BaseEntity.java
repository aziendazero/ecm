package it.tredi.ecm.dao.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonView;

@MappedSuperclass
public abstract class BaseEntity implements Cloneable, Serializable{
	private static final long serialVersionUID = 9109342126172139038L;

	@JsonView({JsonViewModel.Integrazione.class, JsonViewModel.ComunicazioniDestinatari.class, JsonViewModel.EventoLookup.class})
	public abstract Long getId();

    public boolean isNew() {
        return (this.getId() == null);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId());
    }

    @Override
	public Object clone() throws CloneNotSupportedException {
    	return super.clone();
    }
}
