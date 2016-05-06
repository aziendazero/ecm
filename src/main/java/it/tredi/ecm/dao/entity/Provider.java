package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Provider extends BaseEntity{
	private String denominazioneLegale;
	private String tipoOrganizzatore;//TODO enum?
	@Column(name="cf_piva")
	private String cfPiva;
	
	@OneToMany(mappedBy="provider")
	private Set<Persona> persone = new HashSet<Persona>();
	
	@OneToOne(cascade = CascadeType.ALL)
	private Account account;
	
	/** UTILS **/
	public void addPersona(Persona persona){
		this.persone.add(persona);
		persona.setProvider(this);
	}
}
