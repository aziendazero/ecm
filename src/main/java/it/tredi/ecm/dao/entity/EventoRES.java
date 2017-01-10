package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
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
	private List<LocalDate> dateIntermedie = new ArrayList<LocalDate>();

	@Enumerated(EnumType.STRING)
	@Column(name = "tipologia_evento_res")
	private TipologiaEventoRESEnum tipologiaEventoRES;

	private Boolean workshopSeminariEcm;
	@Column(columnDefinition = "text")
	private String titoloConvegno;

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="docente_id")
	private List<PersonaEvento> docenti = new ArrayList<PersonaEvento>();

	@Column(columnDefinition="text")
	private String razionale;
	@ElementCollection
	@Column(columnDefinition="text")
	private Set<String> risultatiAttesi = new HashSet<String>();

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@OrderBy("giorno ASC")
	@JoinColumn(name = "evento_res_id")
	private List<ProgrammaGiornalieroRES> programma = new ArrayList<ProgrammaGiornalieroRES>();

	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<VerificaApprendimentoRESEnum> verificaApprendimento;

	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<VerificaPresenzaPartecipantiEnum> verificaPresenzaPartecipanti;

	@Column(columnDefinition="text")
	private String materialeDurevoleRilasciatoAiPratecipanti;

	private Boolean soloLinguaItaliana;
	private String linguaStranieraUtilizzata;
	private Boolean esisteTraduzioneSimultanea;

	private Boolean verificaRicaduteFormative;
	@Column(columnDefinition = "text")
	private String descrizioneVerificaRicaduteFormative;
	@OneToOne
	private File documentoVerificaRicaduteFormative;

	@Embedded
	private RiepilogoRES riepilogoRES = new RiepilogoRES();

	//se mai un giorno ci venisse in mente di fare le cose come andrebbero fatte
//	public List<LocalDate> getDateIntermedie() {
//		List<LocalDate> dateIntermedie = new ArrayList<LocalDate>();
//		if(this.getProgramma() != null && !this.getProgramma().isEmpty()){
//			for(ProgrammaGiornalieroRES pgr : this.getProgramma()) {
//				//se la data corrisponde alla data inizio o fine NON è considerata intermedia
//				if(pgr.getGiorno() != null && (pgr.getGiorno().isEqual(this.getDataInizio()) || pgr.getGiorno().isEqual(this.getDataFine()))) {
//					continue;
//				}
//				dateIntermedie.add(pgr.getGiorno());
//			}
//
//		}
//		return dateIntermedie;
//	}

}