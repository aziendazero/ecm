package it.tredi.ecm.dao.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaRESEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiRESEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class DettaglioAttivitaRES extends BaseEntity implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -1084979219782048226L;

	@Transient
	private final String PAUSA = "PAUSA";
	@Transient
	private final String VALUTAZIONE_APPRENDIMENTO = "VALUTAZIONE APPRENDIMENTO";
	@Transient
	private final String REGISTRAZIONE_PARTECIPANTI = "REGISTRAZIONE PARTECIPANTI";

	@DateTimeFormat (pattern = "HH:mm")
	@Column(name="orario_inizio")
	private LocalTime orarioInizio;

	@DateTimeFormat (pattern = "HH:mm")
	@Column(name="orario_fine")
	private LocalTime orarioFine;
	@Column(columnDefinition = "text")
	private String argomento;

	@ManyToMany
	@JoinTable(name = "dettaglio_attivitares_docente",
		joinColumns = { @JoinColumn(name = "dettaglio_id") },
		inverseJoinColumns = { @JoinColumn(name = "docente_id") })
	private Set<PersonaEvento> docenti = new HashSet<PersonaEvento>();
	@Column(columnDefinition = "text")
	private String risultatoAtteso;
	@Enumerated(EnumType.STRING)
	private ObiettiviFormativiRESEnum obiettivoFormativo;
	@Enumerated(EnumType.STRING)
	private MetodologiaDidatticaRESEnum metodologiaDidattica;

	@DiffIgnore
	private long minutiAttivita;
	@DiffIgnore
	private float oreAttivita;

	public void setAsPausa(){
		this.argomento = PAUSA;

		this.docenti = null;
		this.risultatoAtteso = "";
		this.obiettivoFormativo = null;
		this.metodologiaDidattica = null;
	}

	public boolean isPausa(){
		if((this.argomento!= null && this.argomento.equalsIgnoreCase(PAUSA)) &&
				(this.docenti == null || this.docenti.isEmpty()) &&
				(this.risultatoAtteso != null && this.risultatoAtteso.isEmpty()) &&
				this.obiettivoFormativo == null &&
				this.metodologiaDidattica == null)
			return true;
		else
			return false;
	}

	public void setAsValutazioneApprendimento(){
		this.argomento = VALUTAZIONE_APPRENDIMENTO;

		this.docenti = null;
		this.risultatoAtteso = "";
		this.obiettivoFormativo = null;
		this.metodologiaDidattica = null;
	}

	public boolean isValutazioneApprendimento(){
		if((this.argomento!= null && this.argomento.equalsIgnoreCase(VALUTAZIONE_APPRENDIMENTO)) &&
				(this.docenti == null || this.docenti.isEmpty()) &&
				(this.risultatoAtteso != null && this.risultatoAtteso.isEmpty()) &&
				this.obiettivoFormativo == null &&
				this.metodologiaDidattica == null)
			return true;
		else
			return false;
	}

	public void setAsRegistrazionePartecipanti() {
		this.argomento = REGISTRAZIONE_PARTECIPANTI;

		this.docenti = null;
		this.risultatoAtteso = "";
		this.obiettivoFormativo = null;
		this.metodologiaDidattica = null;
	}

	public boolean isRegistrazionePartecipanti() {
		if((this.argomento!= null && this.argomento.equalsIgnoreCase(REGISTRAZIONE_PARTECIPANTI)) &&
				(this.docenti == null || this.docenti.isEmpty()) &&
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
		if(orarioInizio != null && orarioFine != null){
			Duration duration = Duration.between(orarioInizio, orarioFine);
			BigDecimal bg = new BigDecimal(duration.toMinutes() / 60.0).setScale(6, RoundingMode.HALF_UP);
			this.oreAttivita = bg.floatValue();
			this.minutiAttivita = duration.toMinutes();
		}
		else{
			this.oreAttivita = 0;
			this.minutiAttivita = 0;
		}
	}
}

