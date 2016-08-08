package it.tredi.ecm.web.bean;

import java.util.HashSet;
import java.util.Set;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonaWrapper extends Wrapper {
	private Persona persona;
	private Ruolo ruolo;
	private Long accreditamentoId;
	private Long providerId;
	private AccreditamentoStatoEnum statoAccreditamento;
	private Boolean isLookup;
	
	private File attoNomina;
	private File cv;
	private File delega;

	public PersonaWrapper(){
		setAttoNomina(new File(FileEnum.FILE_ATTO_NOMINA));
		setCv(new File(FileEnum.FILE_CV));
		setDelega(new File(FileEnum.FILE_DELEGA));
	}
	
	public PersonaWrapper(Persona persona, Long accreditamentoId, Ruolo ruolo){
		this();
		this.persona = persona;
		this.accreditamentoId = accreditamentoId;
		this.ruolo = ruolo;
	}

	public void setAttoNomina(File file){
		attoNomina = file;
		if(persona != null)
			persona.addFile(attoNomina);
	}

	public void setCv(File file){
		cv = file;
		if(persona != null)
			persona.addFile(cv);
	}

	public void setDelega(File file){
		delega = file;
		if(persona != null)
			persona.addFile(delega);
	}

	public Set<File> getFiles(){
		Set<File> files = new HashSet<File>();
		files.add(attoNomina);
		files.add(cv);
		files.add(delega);
		return files;
	}
	
	public void setFiles(Set<File> files){
		for(File file : files){
			if(file.isCV())
				this.setCv(file);
			else if(file.isDELEGA())
				this.setDelega(file);
			else if(file.isATTONOMINA())
				this.setAttoNomina(file);
		}
	}
}
