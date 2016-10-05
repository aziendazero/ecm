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
public class AnagraficaEventoBase{
	private String cognome;
	private String nome;
	private String codiceFiscale;
	private boolean straniero = false;
	@OneToOne
	private File cv;
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		AnagraficaEventoBase aB = (AnagraficaEventoBase) super.clone();
		if(aB.getCv() != null)
			aB.setCv((File) aB.getCv().clone());
		return aB;
	}
	
	//TODO procedura che al salvataggio dell'anagrafica va ad aggiornare i campi delle PersonaEvento che sono negli eventi ancora modificabili
}
