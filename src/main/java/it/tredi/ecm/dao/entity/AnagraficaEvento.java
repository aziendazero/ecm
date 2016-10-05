package it.tredi.ecm.dao.entity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AnagraficaEvento extends BaseEntity{
	@Embedded
	private AnagraficaEventoBase anagrafica;
	
	//TODO procedura che al salvataggio dell'anagrafica va ad aggiornare i campi delle PersonaEvento che sono negli eventi ancora modificabili
}
