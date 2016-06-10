package it.tredi.ecm.web.bean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.enumlist.Costanti;
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
	
	private File attoNomina;
	private File cv;
	private File delega;
	
	public PersonaWrapper(){
		setAttoNomina(new File());
		setCv(new File());
		setDelega(new File());
	}
	
	public void setPersona(Persona p){
		this.persona = p;
	}
	
	public void setAttoNomina(File file){
		if(file.getData() != null && file.getData().length > 0){
			//file e' pieno
			if(file.getId() == null){
				//il file passato è un file nuovo
				if(attoNomina != null){
					//c'era gia' un file...stiamo sovrascrivendo
					file.setId(attoNomina.getId());
				}
			}
		}
		
		attoNomina = file;
		attoNomina.setTipo(Costanti.FILE_ATTO_NOMINA);
		attoNomina.setPersona(persona);
		attoNomina.setDataCreazione(LocalDate.now());
	}
	
	public void setCv(File file){
		if(file.getData() != null && file.getData().length > 0){
			//file e' pieno
			if(file.getId() == null){
				//il file passato è un file nuovo
				if(cv != null){
					//c'era gia' un file...stiamo sovrascrivendo
					file.setId(cv.getId());
				}
			}
		}
		
		cv = file;
		cv.setTipo(Costanti.FILE_CV);
		cv.setPersona(persona);
		cv.setDataCreazione(LocalDate.now());
	}
	
	public void setDelega(File file){
		if(file.getData() != null && file.getData().length > 0){
			//file e' pieno
			if(file.getId() == null){
				//il file passato è un file nuovo
				if(delega != null){
					//c'era gia' un file...stiamo sovrascrivendo
					file.setId(delega.getId());
				}
			}
		}
		
		delega = file;
		delega.setTipo(Costanti.FILE_DELEGA);
		delega.setPersona(persona);
		delega.setDataCreazione(LocalDate.now());
	}
	
	public Set<File> getFiles(){
		Set<File> files = new HashSet<File>();
		files.add(attoNomina);
		files.add(cv);
		files.add(delega);
		return files;
	}
}
