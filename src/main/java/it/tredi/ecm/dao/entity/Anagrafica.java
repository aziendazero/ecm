package it.tredi.ecm.dao.entity;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Anagrafica extends BaseEntity{
	@JsonView(JsonViewModel.Integrazione.class)
	private String cognome;
	@JsonView(JsonViewModel.Integrazione.class)
	private String nome;
	@JsonView(JsonViewModel.Integrazione.class)
	private String codiceFiscale;
	@JsonView(JsonViewModel.Integrazione.class)
	private boolean straniero = false;
	@JsonView(JsonViewModel.Integrazione.class)
	private String telefono;
	@JsonView(JsonViewModel.Integrazione.class)
	private String cellulare;
	@JsonView(JsonViewModel.Integrazione.class)
	private String email;
	@JsonView(JsonViewModel.Integrazione.class)
	private String pec;
	
	public void setCodiceFiscale(String codiceFiscale){
		this.codiceFiscale = codiceFiscale.toUpperCase();
	}
	
	@JsonIgnore
	@OneToOne
	private Provider provider;
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Anagrafica entitapiatta = (Anagrafica) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
