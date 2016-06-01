package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

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
	
	@OneToMany 
	@JoinTable(name = "pippo",
				joinColumns = @JoinColumn(name = "dati_accreditamento_id"),
				inverseJoinColumns = @JoinColumn(name = "disciplina_id")
	)
	private Set<Disciplina> discipline = new HashSet<Disciplina>();
	
	/*** DATI ECONOMICI ***/
	@Embedded
	private DatiEconomici datiEconomici = new DatiEconomici();

	/*** INFO RELATIVE AL PERSONALE ***/
	private int numeroDipendentiFormazioneTempoIndeterminato;
	private int numeroDipendentiFormazioneAltro;
	
	public Set<Professione> getProfessioniSelezionate(){
		Set<Professione> professioniSelezionate = new HashSet<Professione>();
		for(Disciplina d : discipline)
			professioniSelezionate.add(d.getProfessione());
		return professioniSelezionate;
	}
}
