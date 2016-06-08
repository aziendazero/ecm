package it.tredi.ecm.dao.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Anagrafica extends BaseEntity{
	private String cognome;
	private String nome;
	private String codiceFiscale;
	private boolean straniero = false;
	private String telefono;
	private String cellulare;
	private String email;
	private String pec;
	
	@OneToOne
	private Provider provider;
}
