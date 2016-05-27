package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
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
	
	/***	DATI DA COMPILARE NELLA DOMANDA	***/
	//private String tipologiaAccreditamento;//generale o settoriale
	//private List<ProceduraFormativa> procedureFormative;//se generale tutte altrimenti selezionare quale
	
//	@Embedded
//	private DatiEconomici datiEconomici;
//	
//	@OneToMany
//	private List<File> allegati;
	
//	private List<Professione> professioniAccreditamento;//generale o settoriale
//	private List<String> professioni;//se generale tutte altrimenti

	public Accreditamento(){}
	public Accreditamento(String tipoDomanda){
		this.tipoDomanda = tipoDomanda;
		this.stato = Costanti.ACCREDITAMENTO_STATO_BOZZA;
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
		if(dataScadenza.isAfter(LocalDate.now()) || dataScadenza.isEqual(LocalDate.now()))
			return true;
		return false;
	}
	
}
