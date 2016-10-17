package it.tredi.ecm.dao.entity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AnagraficaEvento extends BaseEntity{
	@Embedded
	private AnagraficaEventoBase anagrafica;

	@JsonIgnore
	@ManyToOne
	private Provider provider;
	
	//TODO procedura che al salvataggio dell'anagrafica va ad aggiornare i campi delle PersonaEvento che sono negli eventi ancora modificabili
}
