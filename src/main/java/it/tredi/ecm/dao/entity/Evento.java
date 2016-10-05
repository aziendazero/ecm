package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tredi.ecm.dao.enumlist.ContenutiEventoEnum;
import it.tredi.ecm.dao.enumlist.DestinatariEventoEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.dao.enumlist.VerificaApprendimentoEnum;
import it.tredi.ecm.dao.enumlist.VerificaPresenzaPartecipantiEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
/*
*  Una volta validato l'evento può essere modificato con le seguenti restrizioni:
*  PROVIDER A (<= fino) (>= dal)
*  	+ (DataInizioEvento - 4) >= Now() => tutto tranne ANTICIPO dataEvento
*  	+ (DataInizioEvento - 3) <= Now() => tutto bloccato tranne campi Docenti
*  	+ (DataInizioEvento) <= Now() => tutto bloccato
*  
*  PROVIDER B
*  	+ (DataInizioEvento - 30) >= Now() => tutto modificabile anche dataInizioEvento (ma limite minimo è Now() + 10) per non corrompere i controlli successivi
* 		+ (DataInizioEvento - 10) >= Now() => tutto tranne ANTICIPO dataEvento
*  	+ (DataInizioEvento - 9) <= Now() => tutto bloccato tranne campi Docenti
*  	+ (DataInizioEvento) <= Now() => tutto bloccato
*  
*  RENDICONTAZIONE
*  	+ (Now() > dataFineEvento && Now() <= dataFineEvento + 90)
*  	+ Inserimento sponsor effettuato
*  
*  INSERIMENTO SPONSOR
*  	+ (Now() <= dataFineEvento + 90)
* 
* */
public class Evento extends BaseEntity{
	private static Logger LOGGER = LoggerFactory.getLogger(Evento.class);
	/*
	 * PREFIX[-edizione]
	 * 	PREFIX=
	 * 		*EVENTO inserito in PIANO FORMATIVO -> COD_PROVIDER-ID_EVENTO (quando lo creo nel piano formativo)
	 * 		
	 * 		*EVENTO inserito in EVENTI a partire da evento in piano formativo -> codiceIdentificativo dell'evento di partenza
	 * 		
	 * 		*EVENTO inserito da nuovo in EVENTI -> COD_PROVIDER-ID_EVENTO
	 * 
	 * 		*EVENTO inserito come RIEDIZIONE -> PREFIX dell'evento padre + #edizione
	 * */
	private String prefix;
	private int edizione = 0;
	public String getCodiceIdentificativo(){
		if(edizione > 0)
			return prefix + "-" + edizione;
		else return prefix;
	}
	
	@Enumerated(EnumType.STRING)
	private ProceduraFormativa proceduraFormativa;
	private String titolo;

	@OneToOne
	private Obiettivo obiettivoNazionale;
	@OneToOne
	private Obiettivo obiettivoRegionale;

	@Column(name="anno_piano_formativo")
	private Integer pianoFormativo;

	@ManyToOne @JoinColumn(name = "provider_id")
	private Provider provider;
	@ManyToOne @JoinColumn(name = "accreditamento_id")
	private Accreditamento accreditamento;

	private String professioniEvento;
	@ManyToMany
	@JoinTable(name = "evento_discipline",
				joinColumns = @JoinColumn(name = "evento_id"),
				inverseJoinColumns = @JoinColumn(name = "disciplina_id")
	)
	private Set<Disciplina> discipline = new HashSet<Disciplina>();
	
	public Set<Professione> getProfessioniSelezionate(){
		Set<Professione> professioniSelezionate = new HashSet<Professione>();
		if(discipline != null){
			for(Disciplina d : discipline)
				professioniSelezionate.add(d.getProfessione());
		}
		return professioniSelezionate;
	}
	/**	Fine sezione in comune con EventoPianoFormativo	**/
	
	/**	Inizio sezione Eventi	**/
	@OneToOne
	private Evento eventoPadre;//valorizzato solo se è una riedizione di un evento
	public boolean isRiedizione(){
		return eventoPadre != null;
	}
	
	//false -> dopo 90gg
	private boolean canAttachSponsor = true;
	//true -> dopo fineEvento
	//false -> dopo aver pagato
	//false -> se passano i 90 gg e non ha fatto nulla
	private boolean canDoPagamento = false; 
	//true -> dopo pagamento (Provider B) and attachSponsor fatto
	//true -> dopo fineEvento (Provider A) and attachSponsor fatto
	//false -> dopo 90gg
	private boolean canDoRendicontazione = false;
	
	private EventoStatoEnum stato;//vedi descrizione in EventoStatoEnum 
	private boolean validatorCheck = false; //(durante il salvataggio check di un flag per sapere se sono stati rispettati tutti i vincoli del validator)
	
	@OneToMany(mappedBy="evento")
	private Set<RendicontazioneInviata> inviiRendicontazione = new HashSet<RendicontazioneInviata>();
	
	/**	Utilizzati da Engineering **/
	private Double costo = 0.00;
	private Boolean pagato = false;
	private Boolean pagInCorso = false;

	/**	Utilizzati per invio Cogeaps	**/
	@OneToOne
	private File reportPartecipantiXML;
	@OneToOne
	private File reportPartecipantiCSV;
	
	@OneToOne //valorizzato solo se è la realizzazione di un evento descritto nel piano formativo
	private EventoPianoFormativo eventoPianoFormativo;
	public boolean isEventoDaPianoFormativo(){
		return eventoPianoFormativo != null;
	}
	
	public void setFromEventoPianoFormativo(EventoPianoFormativo epf){
		this.setEventoPianoFormativo(epf);
		this.titolo = epf.getTitolo();
		this.proceduraFormativa = epf.getProceduraFormativa();
		this.obiettivoNazionale = epf.getObiettivoNazionale();
		this.obiettivoRegionale = epf.getObiettivoRegionale();
		this.provider = epf.getProvider();
		this.accreditamento = epf.getAccreditamento();
		this.professioniEvento = epf.getProfessioniEvento();
		this.discipline = epf.getDiscipline();
		this.prefix = epf.getCodiceIdentificativo();
		this.edizione = 0;
	}
	
	public void buildPrefix(){
		if(isEventoDaPianoFormativo() && eventoPianoFormativo != null){
			//evento inserito a partire dall'evento corrispondente in piano formativo
			this.prefix = eventoPianoFormativo.getPrefix();
		}else if(isRiedizione()){
			//evento inserito come riedizione di un evento
			this.prefix = eventoPadre.getPrefix();
		}
	}
	
	@ElementCollection
	private Set<DestinatariEventoEnum> destinatariEvento = new HashSet<DestinatariEventoEnum>();
	private ContenutiEventoEnum contenutiEvento;
	
	private String provincia; //TODO da lista  
	private String comune; //TODO da lista 
	private String indirizzo;//campo libero
	private String luogo;//campo libero
	
	@Column(name = "data_inizio")//inizio evento
	private LocalDate dataInizio;
	@Column(name = "data_fine")//fine evento
	private LocalDate dataFine;
	
	//comprese tra dataInizio e dataFine
	@ElementCollection
	private Set<LocalDate> dateIntermedie = new HashSet<LocalDate>();
	
	private TipologiaEventoRESEnum tipologiaEvento;
	private boolean workshopSeminariEcm;
	private String titoloConvegno;
	private int numeroPartecipanti;
	
	@OneToMany(mappedBy="eventoResponsabile")
	private Set<PersonaEvento> responsabili = new HashSet<PersonaEvento>();
	@OneToMany(mappedBy="eventoDocente")
	private Set<PersonaEvento> docenti = new HashSet<PersonaEvento>();
	
	private String programmaAttivitaFormativaRazionale;
	@ElementCollection
	private List<String> risultatiAttesi = new ArrayList<String>();
	@ElementCollection
	@OrderBy("orario ASC")
	private List<DettaglioAttivitaRES> programma = new ArrayList<DettaglioAttivitaRES>();
	
	@OneToOne
	private File brochureEvento;
	
	@ElementCollection
	private Set<VerificaApprendimentoEnum> verificaApprendiemento;
	
	private Long durata;//calcolo automatico
	private Long crediti;//calcolo con algoritmo che puo essere modificato dal provider
	private boolean confermatiCrediti;
	
	@OneToOne
	private PersonaEvento responsabileSegreteriaOrganizzativa;
	
	private String materialeDurevoleRilasciatoAiPratecipanti;
	private Double quotaPartecipazione;
	private boolean soloLinguaItaliana;
	private String linguaStranieraUtilizzata;
	private boolean esisteTraduzioneSimultanea;
	
	@ElementCollection
	private Set<VerificaPresenzaPartecipantiEnum> verificaPresenzaPartecipanti;
	
	private boolean verificaRicaduteFormative;
	private String descrizioneVerificaRicaduteFormative;
	@OneToOne
	private File documentoVerificaRicaduteFormative;
	
	private boolean eventoSponsorizzato;
	@OneToMany(mappedBy="evento")
	private Set<Sponsor> sponsors = new HashSet<Sponsor>();
	
	private boolean eventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia;
	@OneToOne
	private File autocertificazioneAssenzaPartecipazioneAziendeAlimentiPrimaInfanzia;
	@OneToOne
	private File autocertificazioneAutorizzazioneMinisteroSalute;

	private boolean altreFormeFinanziamento;
	@OneToOne
	private File autocertificazioneAssenzaFinanziamenti;
	@OneToOne
	private File contrattiAccordiConvenzioni;
	
	private boolean eventoAvvalePartner;
	@OneToMany(mappedBy="evento")
	private Set<Partner> partners = new HashSet<Partner>();
	
	@OneToOne
	private File dichiarazioneAssenzaConflittoInteresse;
	
	private boolean proceduraVerificaQualitaPercepita;

	private boolean autorizzazionePrivacy;
	
	public long calcoloCreditiFormativi(){
		long crediti = 0L;
		
		if(tipologiaEvento == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO){
			crediti = (long) (0.20 * durata);
			if(crediti > 5.0)
				crediti = 5L;
		}
		
		if(tipologiaEvento == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO){
			crediti = 1 * durata;
			if(crediti > 50)
				crediti = 50L;
		}
		
		if(tipologiaEvento == TipologiaEventoRESEnum.CORSO_AGGIORNAMENTO){
			long creditiFrontale = 0L;
			//TODO
			
			
			
			long creditiInterattiva = 0L;
		}
		
		
		
		return crediti;
	}
	
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Evento entitapiatta = (Evento) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
