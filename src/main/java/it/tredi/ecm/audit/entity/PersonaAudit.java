package it.tredi.ecm.audit.entity;

import java.util.Objects;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;
import org.javers.core.metamodel.annotation.ValueObject;

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@TypeName("PersonaAudit")
@Getter
@Setter
public class PersonaAudit {
	private Long id;
	private Anagrafica anagrafica = new Anagrafica();
	private Ruolo ruolo;
	private String incarico = "";
	//private Professione professione;
	private String professione;
	//private Boolean coordinatoreComitatoScientifico;

	private File curriculumVitae;
	private File attoDiNomina;
	private File delega;

	public PersonaAudit(Persona persona){
		this.id = persona.getId();
		this.anagrafica = persona.getAnagrafica();
		this.ruolo = persona.getRuolo();
		this.incarico= persona.getIncarico();
		if(persona.getProfessione() == null)
			this.professione = null;
		else
			this.professione = persona.getProfessione().getNome();
		//this.coordinatoreComitatoScientifico = persona.getCoordinatoreComitatoScientifico();
		for(File file : persona.getFiles()) {
			if(file.getTipo() == FileEnum.FILE_ATTO_NOMINA)
				this.attoDiNomina = file;
			else if(file.getTipo() == FileEnum.FILE_CV)
				this.curriculumVitae = file;
			else if(file.getTipo() == FileEnum.FILE_DELEGA)
				this.delega = file;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PersonaAudit entitapiatta = (PersonaAudit) o;
		return Objects.equals(id, entitapiatta.id);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return id.intValue();
	}
}
