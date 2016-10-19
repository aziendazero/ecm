package it.tredi.ecm.dao.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Period;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.apache.tomcat.jni.Time;
import org.springframework.format.annotation.DateTimeFormat;

import com.itextpdf.text.log.SysoCounter;
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
	@Column(name="orario_inizio")
	private LocalTime orarioInizio;
	
	@DateTimeFormat (pattern = "HH:mm")
	@Column(name="orario_fine")
	private LocalTime orarioFine;
	private String argomento;
	
	@ManyToOne()
	private PersonaEvento docente;
	private String risultatoAtteso;
	private ObiettiviFormativiRESEnum obiettivoFormativo;
	private MetodologiaDidatticaRESEnum metodologiaDidattica;
	
	private float oreAttivita;
	
	public void setAsPausa(){
		this.argomento = "PAUSA";
		
		this.docente = null;
		this.risultatoAtteso = "";
		this.obiettivoFormativo = null;
		this.metodologiaDidattica = null;
	}
	
	public boolean isPausa(){
		if((this.argomento!= null && this.argomento.equalsIgnoreCase("PAUSA")) && 
				this.docente == null && 
				(this.risultatoAtteso != null && this.risultatoAtteso.isEmpty()) && 
				this.obiettivoFormativo == null && 
				this.metodologiaDidattica == null)
			return true;
		else
			return false;
	}
	
	public void calcolaOreAttivita(){
		if(orarioInizio !=null && orarioFine != null){
			Duration duration = Duration.between(orarioInizio, orarioFine);
			BigDecimal bg = new BigDecimal(duration.toMinutes() / 60.0).setScale(2, RoundingMode.HALF_UP);
			this.oreAttivita = bg.floatValue();
		}
		else{
			this.oreAttivita = 0;
		}
	}
}

