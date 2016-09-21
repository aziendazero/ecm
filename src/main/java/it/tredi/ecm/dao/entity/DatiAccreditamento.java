package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
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
	//TODO perchè salva l'id con inizializzazione da 0????
	private Set<ProceduraFormativa> procedureFormative = new HashSet<ProceduraFormativa>();
	private String professioniAccreditamento;

	@ManyToMany
	@JoinTable(name = "dati_accreditamento_discipline_selezionate",
				joinColumns = @JoinColumn(name = "dati_accreditamento_id"),
				inverseJoinColumns = @JoinColumn(name = "disciplina_id")
	)
	private Set<Disciplina> discipline = new HashSet<Disciplina>();

	/*** DATI ECONOMICI ***/
	@Embedded
	private DatiEconomici datiEconomici = new DatiEconomici();

	/*** INFO RELATIVE AL PERSONALE ***/
	private Integer numeroDipendentiFormazioneTempoIndeterminato;
	private Integer numeroDipendentiFormazioneAltro;

	public Set<Professione> getProfessioniSelezionate(){
		Set<Professione> professioniSelezionate = new HashSet<Professione>();
		if(discipline != null){
			for(Disciplina d : discipline)
				professioniSelezionate.add(d.getProfessione());
		}
		return professioniSelezionate;
	}
	
	public Set<File> getFiles(){
		return this.accreditamento.getProvider().getFiles();
	}
	
	public void addFile(File file){
		this.accreditamento.getProvider().addFile(file);
	}

	@OneToOne(mappedBy="datiAccreditamento")
	private Accreditamento accreditamento;

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DatiAccreditamento entitapiatta = (DatiAccreditamento) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
