package it.tredi.ecm.dao.entity;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class DelegatoValutazioneSulCampo {

	private String cognome;
	private String nome;
	private String codiceFiscale;

	@OneToOne
	private File delega;
	
	public String getFullName() {
		return this.nome + " " + this.cognome;
	}

}
