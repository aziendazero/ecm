package it.tredi.ecm.dao.entity;

import java.time.LocalTime;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaFADEnum;
import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaRESEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiFADEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiRESEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class DettaglioAttivitaFAD{
	private String argomento;
	@ManyToOne
	private PersonaEvento docente;
	private String risultatoAtteso;
	private ObiettiviFormativiFADEnum obiettivoFormativo;
	private MetodologiaDidatticaFADEnum metodologiaDidattica;
	private Long oreAttivita;
}

