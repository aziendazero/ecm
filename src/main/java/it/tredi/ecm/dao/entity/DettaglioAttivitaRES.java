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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

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

	private final String PAUSA = "PAUSA";
	private final String VALUTAZIONE_APPRENDIMENTO = "VALUTAZIONE APPRENDIMENTO";
	@Transient
	private final String REGISTRAZIONE_PARTECIPANTI = "REGISTRAZIONE PARTECIPANTI";

	@DateTimeFormat (pattern = "HH:mm")
	@Column(name="orario_inizio")
	private LocalTime orarioInizio;

	@DateTimeFormat (pattern = "HH:mm")
	@Column(name="orario_fine")
	private LocalTime orarioFine;
	private String argomento;

	@ManyToOne
	private PersonaEvento docente;
	private String risultatoAtteso;
	@Enumerated(EnumType.STRING)
	private ObiettiviFormativiRESEnum obiettivoFormativo;
	@Enumerated(EnumType.STRING)
	private MetodologiaDidatticaRESEnum metodologiaDidattica;

	private long minutiAttivita;
	private float oreAttivita;

	public void setAsPausa(){
		this.argomento = PAUSA;

		this.docente = null;
		this.risultatoAtteso = "";
		this.obiettivoFormativo = null;
		this.metodologiaDidattica = null;
	}

	public boolean isPausa(){
		if((this.argomento!= null && this.argomento.equalsIgnoreCase(PAUSA)) &&
				this.docente == null &&
				(this.risultatoAtteso != null && this.risultatoAtteso.isEmpty()) &&
				this.obiettivoFormativo == null &&
				this.metodologiaDidattica == null)
			return true;
		else
			return false;
	}

	public void setAsValutazioneApprendimento(){
		this.argomento = VALUTAZIONE_APPRENDIMENTO;

		this.docente = null;
		this.risultatoAtteso = "";
		this.obiettivoFormativo = null;
		this.metodologiaDidattica = null;
	}

	public boolean isValutazioneApprendimento(){
		if((this.argomento!= null && this.argomento.equalsIgnoreCase(VALUTAZIONE_APPRENDIMENTO)) &&
				this.docente == null &&
				(this.risultatoAtteso != null && this.risultatoAtteso.isEmpty()) &&
				this.obiettivoFormativo == null &&
				this.metodologiaDidattica == null)
			return true;
		else
			return false;
	}

	public void setAsRegistrazionePartecipanti() {
		this.argomento = REGISTRAZIONE_PARTECIPANTI;

		this.docente = null;
		this.risultatoAtteso = "";
		this.obiettivoFormativo = null;
		this.metodologiaDidattica = null;
	}

	public boolean isRegistrazionePartecipanti() {
		if((this.argomento!= null && this.argomento.equalsIgnoreCase(REGISTRAZIONE_PARTECIPANTI)) &&
				this.docente == null &&
				(this.risultatoAtteso != null && this.risultatoAtteso.isEmpty()) &&
				this.obiettivoFormativo == null &&
				this.metodologiaDidattica == null)
			return true;
		else
			return false;
	}

	public boolean isExtraType() {
		return (isPausa() || isValutazioneApprendimento() || isRegistrazionePartecipanti());
	}

	public String getExtraType(){
		if(isPausa())
			return PAUSA;

		if(isValutazioneApprendimento())
			return VALUTAZIONE_APPRENDIMENTO;

		if(isRegistrazionePartecipanti())
			return REGISTRAZIONE_PARTECIPANTI;

		return "";
	}

	public void setExtraType(String extraType){
		if(extraType.equalsIgnoreCase(PAUSA))
			setAsPausa();

		if(extraType.equalsIgnoreCase(VALUTAZIONE_APPRENDIMENTO))
			setAsValutazioneApprendimento();

		if(extraType.equalsIgnoreCase(REGISTRAZIONE_PARTECIPANTI))
			setAsRegistrazionePartecipanti();
	}

	public void calcolaOreAttivita(){
		if(orarioInizio !=null && orarioFine != null){
			Duration duration = Duration.between(orarioInizio, orarioFine);
			BigDecimal bg = new BigDecimal(duration.toMinutes() / 60.0).setScale(2, RoundingMode.HALF_UP);
			this.oreAttivita = bg.floatValue();
			this.minutiAttivita = duration.toMinutes();
		}
		else{
			this.oreAttivita = 0;
			this.minutiAttivita = 0;
		}
	}
}

