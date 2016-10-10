package it.tredi.ecm.dao.entity;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class AnagraficaFullEventoBase{
	private String cognome;
	private String nome;
	private String codiceFiscale;
	private String email;
	private String telefono;
	private String cellulare;
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		AnagraficaFullEventoBase aB = (AnagraficaFullEventoBase) super.clone();
		return aB;
	}
	
	//TODO procedura che al salvataggio dell'anagrafica va ad aggiornare i campi delle PersonaFullEvento che sono negli eventi ancora modificabili
}
