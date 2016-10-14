package it.tredi.ecm.dao.entity;

import java.time.LocalTime;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.springframework.format.annotation.DateTimeFormat;

import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaRESEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiRESEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class DettaglioAttivitaRES{
	
	@DateTimeFormat (pattern = "HH:mm")
	private LocalTime orario;
	private String argomento;
	
	@ManyToOne
	private PersonaEvento docente;
	
	private String risultatoAtteso;
	private ObiettiviFormativiRESEnum obiettivoFormativo;
	private MetodologiaDidatticaRESEnum metodologiaDidattica;
	private Long oreAttivita;
}

