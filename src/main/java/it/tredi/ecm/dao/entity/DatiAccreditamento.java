package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import it.tredi.ecm.dao.enumlist.FileEnum;
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
	@Enumerated(EnumType.STRING)
	private Set<ProceduraFormativa> procedureFormative = new HashSet<ProceduraFormativa>();
	private String professioniAccreditamento;

	@ManyToMany
	@JoinTable(name = "dati_accreditamento_discipline_selezionate",
				joinColumns = @JoinColumn(name = "dati_accreditamento_id"),
				inverseJoinColumns = @JoinColumn(name = "disciplina_id")
	)
	private Set<Disciplina> discipline = new HashSet<Disciplina>();

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="dati_accreditamento_files",
	joinColumns={@JoinColumn(name="dati_accreditamento_id")},
	inverseJoinColumns={@JoinColumn(name="files_id")}
			)
	Set<File> files = new HashSet<File>();

	public void addFile(File file){
		Iterator<File> it = this.getFiles().iterator();
		while(it.hasNext()){
			if(it.next().getTipo() == file.getTipo())
				it.remove();
		}
		this.getFiles().add(file);
	}

	public void removeFileByType(FileEnum fileType){
		Iterator<File> it = this.getFiles().iterator();
		while(it.hasNext()){
			if(it.next().getTipo() == fileType)
				it.remove();
		}
	}

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

	public boolean isTipologiaFormativaInserita() {
		return (this.tipologiaAccreditamento != null && !this.tipologiaAccreditamento.isEmpty()
				&& this.procedureFormative != null && !this.procedureFormative.isEmpty()
				&& this.professioniAccreditamento != null && !this.professioniAccreditamento.isEmpty()
				&& this.discipline != null && !this.discipline.isEmpty());
	}

	public boolean isDatiEconomiciInseriti() {
		boolean hasEstrattoBilancioFormazione = false;
		for(File file : this.getFiles()) {
			if(file.isESTRATTOBILANCIOFORMAZIONE())
				hasEstrattoBilancioFormazione = true;
		}
		return hasEstrattoBilancioFormazione;
	}

	public boolean isDatiStrutturaInseriti() {
		return (this.numeroDipendentiFormazioneTempoIndeterminato != null
				&& this.numeroDipendentiFormazioneAltro != null);
	}
}
