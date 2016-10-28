package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.dao.enumlist.VerificaApprendimentoRESEnum;
import it.tredi.ecm.dao.enumlist.VerificaPresenzaPartecipantiEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue(value = "RES")
public class EventoRES extends Evento{
	@Embedded
	private SedeEvento sedeEvento;

	//comprese tra dataInizio e dataFine
	@ElementCollection
	private Set<LocalDate> dateIntermedie = new HashSet<LocalDate>();

	@Enumerated(EnumType.STRING)
	@Column(name = "tipologia_evento_res")
	private TipologiaEventoRESEnum tipologiaEvento;
	private boolean workshopSeminariEcm;
	private String titoloConvegno;

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="docente_id")
	private List<PersonaEvento> docenti = new ArrayList<PersonaEvento>();

	private String razionale;
	@ElementCollection
	private Set<String> risultatiAttesi = new HashSet<String>();

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@OrderBy("giorno ASC")
	@JoinColumn(name = "programma_res_id")
	private List<ProgrammaGiornalieroRES> programma = new ArrayList<ProgrammaGiornalieroRES>();

	@ElementCollection
	private Set<VerificaApprendimentoRESEnum> verificaApprendimento;

	private boolean confermatiCrediti;

	@ElementCollection
	private Set<VerificaPresenzaPartecipantiEnum> verificaPresenzaPartecipanti;

	private String materialeDurevoleRilasciatoAiPratecipanti;

	private Boolean soloLinguaItaliana;
	private String linguaStranieraUtilizzata;
	private Boolean esisteTraduzioneSimultanea;

	private Boolean verificaRicaduteFormative;
	private String descrizioneVerificaRicaduteFormative;
	@OneToOne
	private File documentoVerificaRicaduteFormative;
	
	@Embedded
	private RiepilogoRES riepilogoRES = new RiepilogoRES();
	
}