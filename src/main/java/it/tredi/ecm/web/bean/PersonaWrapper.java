package it.tredi.ecm.web.bean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.utils.MultiPartBuilder;
import it.tredi.ecm.utils.Utils;
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
	
	public void setOffsetAndIds(){
		if(persona.isLegaleRappresentante())
			setIdOffset(22);
		else if(persona.isLegaleRappresentante())
			setIdOffset(31);
		else if(persona.isResponsabileSegreteria())
			setIdOffset(46);
		else if(persona.isResponsabileAmministrativo())
			setIdOffset(53);
		else if(persona.isResponsabileSistemaInformatico())
			setIdOffset(71);
		else if(persona.isResponsabileQualita())
			setIdOffset(79);
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
		cv = file;
		cv.setTipo(Costanti.FILE_CV);
		cv.setPersona(persona);
		cv.setDataCreazione(LocalDate.now());
	}
	
	public void setDelega(File file){
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
	
//	public void setAttoNomina_persona(MultipartFile multiPartFile){
//		setAttoNomina(Utils.convertFromMultiPart(multiPartFile));
//	}
//	public void setCv_persona(MultipartFile multiPartFile){
//		setCv(Utils.convertFromMultiPart(multiPartFile));
//	}
//	public void setDelega_persona(MultipartFile multiPartFile){
//		setDelega(Utils.convertFromMultiPart(multiPartFile));
//	}
}
