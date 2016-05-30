package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DatiAccreditamento extends BaseEntity {
	/*** INFO RELATIVE ALLA RICHIESTA ***/
	private String tipologiaAccreditamento;
	@ElementCollection
	private Set<ProceduraFormativa> procedureFormative = new HashSet<ProceduraFormativa>();
	private String professioniAccreditamento;
	
	/*** DATI ECONOMICI ***/
	@Embedded
	private DatiEconomici datiEconomici;

	/*** INFO RELATIVE AL PERSONALE ***/
	private int numeroDipendentiFormazioneTempoIndeterminato;
	private int numeroDipendentiFormazioneAltro;
	
	public void initDatiEconomici(){
		int currentYear = LocalDate.now().getYear();
		
		if(datiEconomici.isEmpty()){
			datiEconomici.init(currentYear);
		}
	}
}
