package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import it.tredi.ecm.dao.enumlist.AccreditamentoEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Accreditamento extends BaseEntity{
	
	@Column(name = "tipo_domanda")
	@Enumerated(EnumType.STRING)
	private AccreditamentoEnum tipoDomanda;
	@Enumerated(EnumType.STRING)
	private AccreditamentoEnum stato;
	@Column(name = "data_invio")
	private LocalDate dataInvio;
	@Column(name = "data_scadenza")
	private LocalDate dataScadenza;
	
	@Column(name = "data_valutazione")
	private LocalDate dataValutazione;
	@Column(name = "data_fine_accreditamento")
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
	public Accreditamento(AccreditamentoEnum tipoDomanda){
		this.tipoDomanda = tipoDomanda;
		this.stato = AccreditamentoEnum.ACCREDITAMENTO_STATO_BOZZA;
		for (int i = 0; i<100; i++)
			idEditabili.add(new Integer(i));
		editabile = true;
	}
	
	public boolean isProvvisorio(){
		return tipoDomanda.equals(AccreditamentoEnum.ACCREDITAMENTO_TIPO_PROVVISORIO);
	}
	
	public boolean isBozza(){
		return stato.equals(AccreditamentoEnum.ACCREDITAMENTO_STATO_BOZZA);
	}
	
	public boolean isInviato(){
		return stato.equals(AccreditamentoEnum.ACCREDITAMENTO_STATO_INVIATO);
	}
	
	public boolean isProcedimentoAttivo(){
		if( dataScadenza != null && (dataScadenza.isAfter(LocalDate.now()) || dataScadenza.isEqual(LocalDate.now())) )
			return true;
		return false;
	}
	
	public boolean hasPianoFormativo(){
		return pianoFormativo.isNew();
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
