package it.tredi.ecm.dao.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import it.tredi.ecm.dao.enumlist.EventoStatoEnum;
import it.tredi.ecm.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RelazioneAnnuale extends BaseEntity{
	private Integer annoRiferimento;//anno di riferimento dell'attivita formativa
	
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	@Column(name="data_fine_modifca")
	private LocalDate dataFineModifca;
	
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
	
	private int eventiInseritiPFA = 0;//numero di eventi inseriti nel PFA dell'anno precedente
	private int eventiDefinitiviPFA = 0;//numero di eventi rendicontati come attuazione di eventi del PFA dell'anno precedente
	private int eventiDefinitiviManuali = 0;//numero di eventi manuali rendicontati nell'anno precedente
	private int totaleEventiDefinitivi = 0;// totale numero di eventi rendicontati nell'anno precedente (eventiDefinitiviPFA + eventiDefinitiviManuali)
	private BigDecimal rapportoAttuazione = new BigDecimal(0);;//eventiDefinitiviPFA/eventiInseritiPFA

	private Integer numeroPartecipantiNoCrediti;
	private BigDecimal costiTotaliEventi = new BigDecimal(0);
	private BigDecimal ricaviDaSponsor = new BigDecimal(0);
	private BigDecimal altriFinanziamenti = new BigDecimal(0);
	private BigDecimal quoteDiPartecipazione = new BigDecimal(0);
	
	private BigDecimal rapportoCostiEntrate = new BigDecimal(0);//(costiTotaliEventi / (ricaviDaSponsor + altriFinanziamenti + quoteDiPartecipazione))
	
	@ElementCollection
	@MapKeyColumn(name="key_obiettivo_nazionale")
    @Column(name="value")
	@CollectionTable(name="relazione_annuale_riepilogo_obiettivi_nazionali", joinColumns=@JoinColumn(name="relazione_annuale_id"))
	private Map<Obiettivo, Integer> riepilogoObiettivi = new HashMap<Obiettivo, Integer>();
	
	@ElementCollection
	@MapKeyColumn(name="key_professione")
    @Column(name="value")
	@CollectionTable(name="relazione_annuale_riepilogo_professioni", joinColumns=@JoinColumn(name="relazione_annuale_id"))
	private Map<Professione, Integer> riepilogoProfessioni = new HashMap<Professione, Integer>();
	
	@ElementCollection
	@MapKeyColumn(name="key_disciplina")
    @Column(name="value")
	@CollectionTable(name="relazione_annuale_riepilogo_discipline", joinColumns=@JoinColumn(name="relazione_annuale_id"))
	private Map<Disciplina, Integer> riepilogoDiscipline = new HashMap<Disciplina, Integer>();
	
	@ElementCollection
	@MapKeyColumn(name="key_obiettivo_regionale")
    @Column(name="value")
	@CollectionTable(name="relazione_annuale_riepilogo_obiettivi_regionali", joinColumns=@JoinColumn(name="relazione_annuale_id"))
	private Map<Obiettivo, Integer> riepilogoObiettiviRegionali = new HashMap<Obiettivo, Integer>();
	
	private BigDecimal rapportoObiettiviRegionali =  new BigDecimal(0);//(# eventi con ObiettiviRegionali / # totale di eventi)
	
	@OneToOne
	private File relazioneFinale;
	
	public boolean isRelazioneAnnualeModificabile(){
		if(dataFineModifca == null)
			return true;
		if(dataFineModifca.isAfter(LocalDate.now()))
			return true;
		return false;
	}
	
	public void elabora(){
		
		eventiInseritiPFA = 0;//numero di eventi inseriti nel PFA dell'anno precedente
		eventiDefinitiviPFA = 0;//numero di eventi rendicontati come attuazione di eventi del PFA dell'anno precedente
		eventiDefinitiviManuali = 0;//numero di eventi manuali rendicontati nell'anno precedente
		totaleEventiDefinitivi = 0;
		rapportoAttuazione = new BigDecimal(0);//eventiDefinitiviPFA/eventiInseritiPFA
		rapportoCostiEntrate = new BigDecimal(0);
		rapportoObiettiviRegionali =  new BigDecimal(0);
		
		riepilogoProfessioni.clear();
		riepilogoDiscipline.clear();
		riepilogoObiettiviRegionali.clear();
		riepilogoObiettivi.clear();
		
		if(eventiPFA != null)
			eventiInseritiPFA = eventiPFA.size();
		
		if(eventiAttuati != null){
			for(Evento e :  eventiAttuati){
				if(e.getStato() == EventoStatoEnum.CANCELLATO){
					eventiAnnullati.add(e);
				}else if(e.getStato() == EventoStatoEnum.RAPPORTATO){
					//per il calcolo di rapportoAttuazione NON si tiene conto delle riedizioni (salvo per eventi padre cancellati) 
					if(e.isRiedizione()){
						eventiRendicontati_Riedizione.add(e);
					}else{
						eventiRendicontati.add(e);
						if(e.isEventoDaPianoFormativo()){
							eventiDefinitiviPFA++;
						}else{
							eventiDefinitiviManuali++;
						}
					}
					
					getInfoRiepilogo(e);
				}
			}
		}
		
		//controllo se tra gli eventi annullati se si è comunque effettuata una riedizione valida per la Relazione Annuale
		if(eventiAnnullati != null){
			for(Evento e : eventiAnnullati){
				//controllo se c'è una riedizione di questo evento annullato per poterla inserire nella relazione annuale
				for(Evento eR : eventiRendicontati_Riedizione){
					if(eR.isRiedizione() && eR.getEventoPadre() == e){
						if(eR.isEventoDaPianoFormativo()){
							eventiDefinitiviPFA++;
						}else{
							eventiDefinitiviManuali++;
						}
						break;
					}
				}
			}
		}
		
		totaleEventiDefinitivi = eventiDefinitiviPFA + eventiDefinitiviManuali;
		
		if(eventiInseritiPFA > 0 )
			rapportoAttuazione = BigDecimal.valueOf(eventiDefinitiviPFA/eventiInseritiPFA).multiply(new BigDecimal(100));
		
		double sum = (ricaviDaSponsor.doubleValue() + altriFinanziamenti.doubleValue() + quoteDiPartecipazione.doubleValue());
		if(sum > 0)
			rapportoCostiEntrate = BigDecimal.valueOf(costiTotaliEventi.doubleValue() / sum).multiply(new BigDecimal(100));
		
		if(riepilogoObiettiviRegionali != null){
			riepilogoObiettiviRegionali.forEach( (k,v) -> {
				if(!k.isNonRientraTraObiettiviRegionali()){
					rapportoObiettiviRegionali = rapportoObiettiviRegionali.add(BigDecimal.valueOf(v.doubleValue()));
				}
			});
		}
			
		if(eventiAttuati != null && eventiAttuati.size() > 0){
			double val = (rapportoObiettiviRegionali.doubleValue() / eventiAttuati.size()) * 100;
			rapportoObiettiviRegionali = BigDecimal.valueOf(val);
		}
	}
	
	private void getInfoRiepilogo(Evento e){
		if(e.getObiettivoNazionale() != null)
			addElement(e.getObiettivoNazionale(), riepilogoObiettivi);
		if(e.getObiettivoRegionale() != null)
			addElement(e.getObiettivoRegionale(), riepilogoObiettiviRegionali);
		
		for(Professione p : e.getProfessioniSelezionate()){
			addElement(p, riepilogoProfessioni);
		}
		
		for(Disciplina d : e.getDiscipline()){
			addElement(d, riepilogoDiscipline);
		}
		
//		if(riepilogoObiettivi.containsKey(e.getObiettivoNazionale())){
//			int value = riepilogoObiettivi.get(e.getObiettivoNazionale());
//			riepilogoObiettivi.put(e.getObiettivoNazionale(),value++);
//		}else{
//			riepilogoObiettivi.put(e.getObiettivoNazionale(),1);
//		}
//		
//		if(riepilogoObiettiviRegionali.containsKey(e.getObiettivoRegionale())){
//			int value = riepilogoObiettivi.get(e.getObiettivoRegionale());
//			riepilogoObiettiviRegionali.put(e.getObiettivoRegionale(),value++);
//		}else{
//			riepilogoObiettiviRegionali.put(e.getObiettivoRegionale(),1);
//		}
		
	}
	
	private <T> void addElement(T element, Map<T,Integer> mappa){
		if(mappa.containsKey(element)){
			int value = mappa.get(element);
			value++;
			mappa.put(element,value);
		}else{
			mappa.put(element,1);
		}
	}
	
}
