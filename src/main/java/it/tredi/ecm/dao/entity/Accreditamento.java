package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.time.temporal.ChronoUnit;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;

import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Accreditamento extends BaseEntity{

	@Column(name = "tipo_domanda")
	@Enumerated(EnumType.STRING)
	private AccreditamentoTipoEnum tipoDomanda;
	@Enumerated(EnumType.STRING)
	private AccreditamentoStatoEnum stato;
	@Column(name = "data_invio")//invio alla segreteria (domanda non più in BOZZA)
	private LocalDate dataInvio;
	@Column(name = "data_scadenza")//limite di 180 gg per completare il procedimento
	private LocalDate dataScadenza;
	@Column(name = "data_inizio_conteggio")//data fittizia utilizzata per calcolare la reale durata del procedimento
	private LocalDate dataInizioConteggio;
	@Column(name = "durata_procedimento")//campo contenente la durata del procedimento espresso in giorni...nel caso in cui il timer viene messo in pausa dal flusso
	private Integer durataProcedimento;

	@Column(name = "data_valutazione_crecm")//la data in cui il gruppo CRECM termina la valutazione e il flusso avanza
	private LocalDate dataValutazioneCrecm;
	@Column(name = "data_ins_odg")
	private LocalDate dataInserimentoOdg;//domanda inserita in odg della prossima seduta
	@Column(name = "data_valutazione_commissione")
	private LocalDate dataValutazioneCommissione;//domanda discussa dalla commissione ECM
	@Column(name = "data_richiesta_integrazione")
	private LocalDate dataRichiestaIntegrazione;//domanda rispedita al provider per integrazioni
	@Column(name = "data_integrazione")
	private LocalDate dataIntegrazione;//domanda rispedita alla segreteria in seguito alle integrazioni del provider
	@Column(name = "data_inizio_accreditamento")
	private LocalDate dataInizioAccreditamento;//procedimento terminato..con l'accettazione dell'accreditamento...in attesa del pagamento
	@Column(name = "data_fine_accreditamento")//data di scadenza calcolata dall'accreditamento +4anni
	private LocalDate dataFineAccreditamento;

	@JoinColumn(name = "valutato_da")
	@OneToOne(fetch = FetchType.LAZY)
	private Persona valutatore;

	@JoinColumn(name = "provider_id")
	@OneToOne(fetch = FetchType.LAZY)
	private Provider provider;

	@JoinColumn(name = "dati_accreditamento_id")
	@OneToOne(fetch = FetchType.LAZY, cascade= CascadeType.REMOVE)
	private DatiAccreditamento datiAccreditamento;

	//flag che mi dice se la domanda è in visualizzazione o in modifica per il provider
	//gli idEditabili invece indicano quali campi possono essere modificati
	@Type(type = "serializable")
	private List<Integer> idEditabili = new ArrayList<Integer>();
	private boolean editabile = false;

	@OneToOne
	private PianoFormativo pianoFormativo;

	public Accreditamento(){}
	public Accreditamento(AccreditamentoTipoEnum tipoDomanda){
		this.tipoDomanda = tipoDomanda;
		this.stato = AccreditamentoStatoEnum.BOZZA;
		for (int i = 0; i<100; i++)
			idEditabili.add(new Integer(i));
		editabile = true;
	}

	public boolean isProvvisorio(){
		return tipoDomanda.equals(AccreditamentoTipoEnum.PROVVISORIO);
	}

	public boolean isBozza(){
		return stato.equals(AccreditamentoStatoEnum.BOZZA);
	}

	public boolean isProcedimentoAttivo(){
		if( dataScadenza != null && (dataScadenza.isAfter(LocalDate.now()) || dataScadenza.isEqual(LocalDate.now())) )
			return true;
		return false;
	}

	public boolean hasPianoFormativo(){
		return (pianoFormativo != null && !pianoFormativo.isNew());
	}

	public int getDurataProcedimento(){
		if(durataProcedimento == null)
			return new Long(ChronoUnit.DAYS.between(dataInizioConteggio, LocalDate.now())).intValue();
		else
		{
			return durataProcedimento;
		}
	}

	//nel caso in cui si stoppa il conteggio...salviamo momentaneamente la durata già trascorsa 
	public void standbyConteggio(){
		durataProcedimento = getDurataProcedimento();
		dataInizioConteggio = null;
	}

	//nel caso in cui riparte il conteggio...azzero la variabile durataProcedimento e setto una dataInizioConteggio coerente
	public void restartConteggio(){
		dataInizioConteggio = LocalDate.now().minusDays(durataProcedimento);
		durataProcedimento = null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Accreditamento entitapiatta = (Accreditamento) o;
		return Objects.equals(id, entitapiatta.id);
	}

}
