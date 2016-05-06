package it.tredi.ecm.dao.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="persona",
		uniqueConstraints = {
								@UniqueConstraint(columnNames={"provider_id", "ruolo"})
								}
)
@Getter
@Setter
public class Persona extends BaseEntity{
	@ManyToOne(cascade = {CascadeType.PERSIST , CascadeType.MERGE})
	private Anagrafica anagrafica = new Anagrafica();
	@ManyToOne @JoinColumn(name = "provider_id")
	private Provider provider;
	//TODO enum?
	private String ruolo;
	private String incarico = "";
	
	public Persona(){}
	public Persona(String ruolo){this.ruolo = ruolo;}
}
