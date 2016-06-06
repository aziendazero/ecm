package it.tredi.ecm.web.bean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.Costanti;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccreditamentoAllegatiWrapper extends Wrapper{
	private Long accreditamentoId;
	private Provider provider;
	
	private File attoCostitutivo;
	private long attoCostitutivoModel;
	private File esperienzaFormazione;
	private long esperienzaFormazioneModel;
	private File utilizzo;
	private long utilizzoModel;
	private File sistemaInformatico;
	private long sistemaInformaticoModel;
	private File pianoQualita;
	private long pianoQualitaModel;
	private File dichiarazioneLegale;
	private long dichiarazioneLegaleModel;
	
	public AccreditamentoAllegatiWrapper(){
		setAttoCostitutivo(new File());
		private File esperienzaFormazione;
		private File utilizzo;
		private File sistemaInformatico;
		private File pianoQualita;
		private File dichiarazioneLegale;
	}
	
	public void setOffsetAndIds(){
		setIdOffset(87);//fino a 92
	}
	
	public void setAttoCostitutivo(File file){
		if(file.getData() != null && file.getData().length > 0){
			//file e' pieno
			if(file.getId() == null){
				//il file passato è un file nuovo
				if(attoCostitutivo != null){
					//c'era gia' un file...stiamo sovrascrivendo
					file.setId(attoCostitutivo.getId());
				}
			}
		}
		
		attoCostitutivo = file;
		attoCostitutivo.setTipo(Costanti.FILE_FUNZIONIGRAMMA);
		attoCostitutivo.setProvider(provider);
		attoCostitutivo.setDataCreazione(LocalDate.now());
	}

	public Set<File> getFiles(){
		Set<File> files = new HashSet<File>();
		files.add(attoCostitutivo);
		return files;
	}

}
