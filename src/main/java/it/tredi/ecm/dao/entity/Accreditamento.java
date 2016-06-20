package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;

import it.tredi.ecm.dao.enumlist.Costanti;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Accreditamento extends BaseEntity{
	
	@Column(name = "tipo_domanda")
	private String tipoDomanda;
	private String stato;
	@Column(name = "data_invio")
	private LocalDate dataInvio;
	@Column(name = "data_valutazione")
	private LocalDate dataValutazione;
	@Column(name = "data_scadenza")
	private LocalDate dataScadenza;
	
	@JoinColumn(name = "valutato_da")
	@OneToOne(fetch = FetchType.LAZY)
	private Persona valutatore;
	
	@JoinColumn(name = "provider_id")
	@OneToOne(fetch = FetchType.LAZY)
	private Provider provider;
	
	@JoinColumn(name = "dati_accreditamento_id")
	@OneToOne(fetch = FetchType.LAZY)
	private DatiAccreditamento datiAccreditamento;
	
	@Type(type = "serializable")
	private List<Integer> idEditabili = new ArrayList<Integer>();
	
	public Accreditamento(){}
	public Accreditamento(String tipoDomanda){
		this.tipoDomanda = tipoDomanda;
		this.stato = Costanti.ACCREDITAMENTO_STATO_BOZZA;
		
		for (int i = 0; i<100; i++)
			idEditabili.add(new Integer(i));
	}
	
	public boolean isProvvisorio(){
		if(tipoDomanda.equals(Costanti.ACCREDITAMENTO_PROVVISORIO))
			return true;
		return false;
	}
	
	public boolean isBozza(){
		if(stato.equals(Costanti.ACCREDITAMENTO_STATO_BOZZA))
			return true;
		return false;
	}
	
	public boolean isAttivo(){
		if( dataScadenza != null && (dataScadenza.isAfter(LocalDate.now()) || dataScadenza.isEqual(LocalDate.now())) )
			return true;
		return false;
	}
	
	public boolean isInviato(){
		if(stato.equals(Costanti.ACCREDITAMENTO_STATO_INVIATO))
			return true;
		return false;
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
