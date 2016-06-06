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
		setEsperienzaFormazione(new File());
		setUtilizzo(new File());
		setSistemaInformatico(new File());
		setPianoQualita(new File());
		setDichiarazioneLegale(new File());
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
		attoCostitutivo.setTipo(Costanti.FILE_ATTO_COSTITUTIVO);
		attoCostitutivo.setProvider(provider);
		attoCostitutivo.setDataCreazione(LocalDate.now());
	}
	
	public void setEsperienzaFormazione(File file){
		if(file.getData() != null && file.getData().length > 0){
			//file e' pieno
			if(file.getId() == null){
				//il file passato è un file nuovo
				if(esperienzaFormazione != null){
					//c'era gia' un file...stiamo sovrascrivendo
					file.setId(esperienzaFormazione.getId());
				}
			}
		}
		
		esperienzaFormazione = file;
		esperienzaFormazione.setTipo(Costanti.FILE_ESPERIENZA_FORMAZIONE);
		esperienzaFormazione.setProvider(provider);
		esperienzaFormazione.setDataCreazione(LocalDate.now());
	}
	
	public void setUtilizzo(File file){
		if(file.getData() != null && file.getData().length > 0){
			//file e' pieno
			if(file.getId() == null){
				//il file passato è un file nuovo
				if(utilizzo != null){
					//c'era gia' un file...stiamo sovrascrivendo
					file.setId(utilizzo.getId());
				}
			}
		}
		
		utilizzo = file;
		utilizzo.setTipo(Costanti.FILE_UTILIZZO);
		utilizzo.setProvider(provider);
		utilizzo.setDataCreazione(LocalDate.now());
	}
	
	public void setSistemaInformatico(File file){
		if(file.getData() != null && file.getData().length > 0){
			//file e' pieno
			if(file.getId() == null){
				//il file passato è un file nuovo
				if(sistemaInformatico != null){
					//c'era gia' un file...stiamo sovrascrivendo
					file.setId(sistemaInformatico.getId());
				}
			}
		}
		
		sistemaInformatico = file;
		sistemaInformatico.setTipo(Costanti.FILE_SISTEMA_INFORMATICO);
		sistemaInformatico.setProvider(provider);
		sistemaInformatico.setDataCreazione(LocalDate.now());
	}
	
	public void setPianoQualita(File file){
		if(file.getData() != null && file.getData().length > 0){
			//file e' pieno
			if(file.getId() == null){
				//il file passato è un file nuovo
				if(pianoQualita != null){
					//c'era gia' un file...stiamo sovrascrivendo
					file.setId(pianoQualita.getId());
				}
			}
		}
		
		pianoQualita = file;
		pianoQualita.setTipo(Costanti.FILE_PIANO_QUALITA);
		pianoQualita.setProvider(provider);
		pianoQualita.setDataCreazione(LocalDate.now());
	}
	
	public void setDichiarazioneLegale(File file){
		if(file.getData() != null && file.getData().length > 0){
			//file e' pieno
			if(file.getId() == null){
				//il file passato è un file nuovo
				if(dichiarazioneLegale != null){
					//c'era gia' un file...stiamo sovrascrivendo
					file.setId(dichiarazioneLegale.getId());
				}
			}
		}
		
		dichiarazioneLegale = file;
		dichiarazioneLegale.setTipo(Costanti.FILE_DICHIARAZIONE_LEGALE);
		dichiarazioneLegale.setProvider(provider);
		dichiarazioneLegale.setDataCreazione(LocalDate.now());
	}

	public Set<File> getFiles(){
		Set<File> files = new HashSet<File>();
		files.add(attoCostitutivo);
		files.add(esperienzaFormazione);
		files.add(utilizzo);
		files.add(sistemaInformatico);
		files.add(pianoQualita);
		files.add(dichiarazioneLegale);
		return files;
	}

}
