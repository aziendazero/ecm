package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
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

	private EventoStatoEnum stato; 
	private boolean validatorCheck = false; //(durante il salvataggio check di un flag per sapere se sono stati ripsettati tutti i vincoli del validator)
	
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
	@OneToOne
	private Evento eventoPadre;//valorizzato solo se è una riedizione
	public boolean isRiedizione(){
		return eventoPadre != null;
	}
	
	@Column(name = "data_inizio")//inizio evento
	private LocalDate dataInizio;
	@Column(name = "data_fine")//fine evento
	private LocalDate dataFine;
	
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
	
	@OneToMany(mappedBy="evento")
	private Set<RendicontazioneInviata> inviiRendicontazione = new HashSet<RendicontazioneInviata>();
	
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

	
	private Double costo = 0.00;
	private Boolean pagato = false;
	private Boolean pagInCorso = false;

	@Type(type = "serializable")
	private List<Integer> idEditabili = new ArrayList<Integer>();

	public Evento() {
		for (int i = 0; i<10; i++)
			idEditabili.add(new Integer(i));
	}
	
	public Set<Professione> getProfessioniSelezionate(){
		Set<Professione> professioniSelezionate = new HashSet<Professione>();
		if(discipline != null){
			for(Disciplina d : discipline)
				professioniSelezionate.add(d.getProfessione());
		}
		return professioniSelezionate;
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
