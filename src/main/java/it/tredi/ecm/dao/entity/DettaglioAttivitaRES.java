package it.tredi.ecm.dao.entity;

import java.io.Serializable;
import java.time.LocalTime;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.springframework.format.annotation.DateTimeFormat;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaRESEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiRESEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class DettaglioAttivitaRES implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1084979219782048226L;
	@DateTimeFormat (pattern = "HH:mm")
	private LocalTime orario;
	private String argomento;
	
	@ManyToOne
	private PersonaEvento docente;
	private String risultatoAtteso;
	private ObiettiviFormativiRESEnum obiettivoFormativo;
	private MetodologiaDidatticaRESEnum metodologiaDidattica;
	
	private Long oreAttivita;
	
	public void setAsPausa(){
		this.argomento = "PAUSA";
		
		this.docente = null;
		this.risultatoAtteso = "";
		this.obiettivoFormativo = null;
		this.metodologiaDidattica = null;
	}
	
	public boolean isPausa(){
		if(this.argomento.equalsIgnoreCase("PAUSA") && 
				this.docente == null && 
				this.risultatoAtteso.isEmpty() && 
				this.obiettivoFormativo == null && 
				this.metodologiaDidattica == null)
			return true;
		else
			return false;
	}
}

