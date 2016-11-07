package it.tredi.ecm.dao.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RelazioneAnnuale extends BaseEntity{
	private Integer annoRiferimento;//anno di riferimento dell'attivita formativa
	
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	@Column(name="data_scadenza")
	private LocalDate dataScadenza;
	
	@ManyToOne
	private Provider provider;
	
	@OneToMany
	private Set<EventoPianoFormativo> eventiPFA = new HashSet<EventoPianoFormativo>();
	@OneToMany
	private Set<Evento> eventiRendicontati = new HashSet<Evento>();
	
	@Transient
	private int eventiInseritiPFA;//numero di eventi inseriti nel PFA dell'anno precedente
	@Transient
	private int eventiDefinitiviPFA;//numero di eventi rendicontati come attuazione di eventi del PFA dell'anno precedente
	@Transient
	private int eventiDefinitiviManuali;//numero di eventi manuali rendicontati nell'anno precedente

	private int numeroPartecipantiNoCrediti;
	private BigDecimal costiTotaliEventi;
	private BigDecimal ricaviDaSponsor;
	private BigDecimal altriFinanziamenti;
	private BigDecimal quoteDiPartecipazione;
	private float rapportoCostiEntrate;//(costiTotaliEventi / (ricaviDaSponsor + altriFinanziamenti + quoteDiPartecipazione))
	
	private Set<Obiettivo> riepilogoObiettivi;
	private Set<Disciplina> riepilogoDiscipline;
	private Set<Professione> riepilogoProfessioni;
	private Set<Obiettivo> riepilogoObiettiviRegionali;
	
	@OneToOne
	private File relazioneFinale;
}
