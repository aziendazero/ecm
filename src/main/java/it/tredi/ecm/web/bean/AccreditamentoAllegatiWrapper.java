package it.tredi.ecm.web.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.FileEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccreditamentoAllegatiWrapper extends Wrapper{
	private Long accreditamentoId;
	private Provider provider;

	private File attoCostitutivo;
	private File esperienzaFormazione;
	private File utilizzo;
	private File sistemaInformatico;
	private File pianoQualita;
	private File dichiarazioneLegale;
	private File dichiarazioneEsclusione;

	private Long attoCostitutivoModel;
	private Long esperienzaFormazioneModel;
	private Long utilizzoModel;
	private Long sistemaInformaticoModel;
	private Long pianoQualitaModel;
	private Long dichiarazioneLegaleModel;
	private Long dichiarazioneEsclusioneModel;

	public AccreditamentoAllegatiWrapper(){
		setAttoCostitutivo(new File(FileEnum.FILE_ATTO_COSTITUTIVO));
		setEsperienzaFormazione(new File(FileEnum.FILE_ESPERIENZA_FORMAZIONE));
		setUtilizzo(new File(FileEnum.FILE_UTILIZZO));
		setSistemaInformatico(new File(FileEnum.FILE_SISTEMA_INFORMATICO));
		setPianoQualita(new File(FileEnum.FILE_PIANO_QUALITA));
		setDichiarazioneLegale(new File(FileEnum.FILE_DICHIARAZIONE_LEGALE));
		setDichiarazioneEsclusione(new File(FileEnum.FILE_DICHIARAZIONE_ESCLUSIONE));
	}

	public void setModelIds(HashMap<FileEnum, Long> modelIds){
		attoCostitutivoModel = modelIds.get(FileEnum.FILE_MODELLO_ATTO_COSTITUTIVO);
		esperienzaFormazioneModel = modelIds.get(FileEnum.FILE_MODELLO_ESPERIENZA_FORMAZIONE);
		utilizzoModel = modelIds.get(FileEnum.FILE_MODELLO_UTILIZZO);
		sistemaInformaticoModel = modelIds.get(FileEnum.FILE_MODELLO_SISTEMA_INFORMATICO);
		pianoQualitaModel = modelIds.get(FileEnum.FILE_MODELLO_PIANO_QUALITA);
		dichiarazioneLegaleModel = modelIds.get(FileEnum.FILE_MODELLO_DICHIARAZIONE_LEGALE);
		dichiarazioneEsclusioneModel = modelIds.get(FileEnum.FILE_DICHIARAZIONE_ESCLUSIONE);
	}

	public void setAttoCostitutivo(File file){
		attoCostitutivo = file;
		if(provider != null)
			provider.addFile(attoCostitutivo);
	}

	public void setEsperienzaFormazione(File file){
		esperienzaFormazione = file;
		if(provider != null)
			provider.addFile(esperienzaFormazione);
	}

	public void setUtilizzo(File file){
		utilizzo = file;
		if(provider != null)
			provider.addFile(utilizzo);
	}

	public void setSistemaInformatico(File file){
		sistemaInformatico = file;
		if(provider != null)
			provider.addFile(sistemaInformatico);
	}

	public void setPianoQualita(File file){
		pianoQualita = file;
		if(provider != null)
			provider.addFile(pianoQualita);
	}

	public void setDichiarazioneLegale(File file){
		dichiarazioneLegale = file;
		if(provider != null)
			provider.addFile(dichiarazioneLegale);
	}

	public void setDichiarazioneEsclusione(File file) {
		dichiarazioneEsclusione = file;
		if(provider != null)
			provider.addFile(dichiarazioneEsclusione);
	}

	public Set<File> getFiles(){
		Set<File> files = new HashSet<File>();
		files.add(attoCostitutivo);
		files.add(esperienzaFormazione);
		files.add(utilizzo);
		files.add(sistemaInformatico);
		files.add(pianoQualita);
		files.add(dichiarazioneLegale);
		files.add(dichiarazioneEsclusione);
		return files;
	}
	
	public void setFiles(Set<File> files){
		Set<File> fs = new HashSet<File>(files);
		for(File file : fs){
			if(file.isATTOCOSTITUTIVO())
				this.setAttoCostitutivo(file);
			else if(file.isESPERIENZAFORMAZIONE())
				this.setEsperienzaFormazione(file);
			else if(file.isUTILIZZO())
				this.setUtilizzo(file);
			else if(file.isSISTEMAINFORMATICO())
				this.setSistemaInformatico(file);
			else if(file.isPIANOQUALITA())
				this.setPianoQualita(file);
			else if(file.isDICHIARAZIONELEGALE())
				this.setDichiarazioneLegale(file);
			else if(file.isDICHIARAZIONEESCLUSIONE())
				this.setDichiarazioneEsclusione(file);
		}
	}
}
