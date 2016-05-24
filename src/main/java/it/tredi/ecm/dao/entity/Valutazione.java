package it.tredi.ecm.dao.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Valutazione extends BaseEntity{
	private int campo;
	private boolean esito;
	private String valutazione;
	
	@ManyToOne
	private Accreditamento accreditamento;
	
	@ManyToOne
	private Persona valutatore;
}
