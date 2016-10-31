package it.tredi.ecm.dao.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaFADEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiFADEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class DettaglioAttivitaFAD implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 8581821767653797912L;
	private String argomento;
	@ManyToOne
	private PersonaEvento docente;
	private String risultatoAtteso;
	@Enumerated(EnumType.STRING)
	private ObiettiviFormativiFADEnum obiettivoFormativo;
	@Enumerated(EnumType.STRING)
	private MetodologiaDidatticaFADEnum metodologiaDidattica;
	private float oreAttivita;
}

