package it.tredi.ecm.dao.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import it.tredi.ecm.dao.enumlist.EventoStatoEnum;
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
	private Set<Evento> eventiAttuati = new HashSet<Evento>();
	
	@Transient
	private Set<Evento> eventiRendicontati = new HashSet<Evento>();
	@Transient
	private Set<Evento> eventiAnnullati = new HashSet<Evento>();
	@Transient
	private Set<Evento> eventiRendicontati_Riedizione = new HashSet<Evento>();
	
	@Transient
	private int eventiInseritiPFA = 0;//numero di eventi inseriti nel PFA dell'anno precedente
	@Transient
	private int eventiDefinitiviPFA = 0;//numero di eventi rendicontati come attuazione di eventi del PFA dell'anno precedente
	@Transient
	private int eventiDefinitiviManuali = 0;//numero di eventi manuali rendicontati nell'anno precedente
	@Transient
	private float rapportoAttuazione = 0;//numero di eventi manuali rendicontati nell'anno precedente

	private int numeroPartecipantiNoCrediti;
	private BigDecimal costiTotaliEventi;
	private BigDecimal ricaviDaSponsor;
	private BigDecimal altriFinanziamenti;
	private BigDecimal quoteDiPartecipazione;
	
	@Transient
	private float rapportoCostiEntrate;//(costiTotaliEventi / (ricaviDaSponsor + altriFinanziamenti + quoteDiPartecipazione))
	
	@ManyToMany
	private Set<Obiettivo> riepilogoObiettivi;
	@ManyToMany
	private Set<Disciplina> riepilogoDiscipline;
	@ManyToMany
	private Set<Professione> riepilogoProfessioni;
	@ManyToMany
	private Set<Obiettivo> riepilogoObiettiviRegionali;
	
	@OneToOne
	private File relazioneFinale;
	
	public void elabora(){
		if(eventiPFA != null)
			eventiInseritiPFA = eventiPFA.size();
		
		if(eventiAttuati != null){
			for(Evento e :  eventiRendicontati){
				if(e.getStato() == EventoStatoEnum.CANCELLATO){
					eventiAnnullati.add(e);
				}else if(e.getStato() == EventoStatoEnum.RAPPORTATO){
					//per il calcolo di rapportoAttuazione NON si tiene conto delle riedizioni (salvo per eventi padre cancellati) 
					if(e.isRiedizione()){
						eventiRendicontati_Riedizione.add(e);
					}else{
						if(e.getEventoPianoFormativo() != null){
							eventiDefinitiviPFA++;
						}else{
							eventiDefinitiviManuali++;
						}
					}
					
				}
				
				//TODO fare funzione che prende tutte le info per professioni,discipline,obiettivi per ogni evento
				riepilogoDiscipline.addAll(e.getDiscipline());
			}
		}
		
		
		
		rapportoAttuazione = eventiDefinitiviPFA/eventiInseritiPFA;
		rapportoCostiEntrate = (costiTotaliEventi.floatValue() / (ricaviDaSponsor.floatValue() + altriFinanziamenti.floatValue() + quoteDiPartecipazione.floatValue()));
	}
}
