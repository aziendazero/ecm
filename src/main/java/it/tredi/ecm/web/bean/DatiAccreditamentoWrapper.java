package it.tredi.ecm.web.bean;

import java.util.HashSet;
import java.util.Set;

import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.enumlist.FileEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatiAccreditamentoWrapper extends Wrapper{
	private DatiAccreditamento datiAccreditamento;
	private Long accreditamentoId;
	private Long providerId;
	//private Provider provider;
	private int sezione;

	private File estrattoBilancioComplessivo;
	private File estrattoBilancioFormazione;
	private File funzionigramma;
	private File organigramma;

	public DatiAccreditamentoWrapper() {
		setEstrattoBilancioComplessivo(new File(FileEnum.FILE_ESTRATTO_BILANCIO_COMPLESSIVO));
		setEstrattoBilancioFormazione(new File(FileEnum.FILE_ESTRATTO_BILANCIO_FORMAZIONE));
		setFunzionigramma(new File(FileEnum.FILE_FUNZIONIGRAMMA));
		setOrganigramma(new File(FileEnum.FILE_ORGANIGRAMMA));
	}

	public DatiAccreditamentoWrapper(DatiAccreditamento datiAccreditamento, Long accreditamentoId, Long providerId){
		this();
		this.datiAccreditamento = datiAccreditamento;
		this.accreditamentoId = accreditamentoId;
		this.providerId = providerId;
	}

	public void setEstrattoBilancioComplessivo(File file){
		estrattoBilancioComplessivo = file;
		if(datiAccreditamento != null) {
			if(file != null) {
				datiAccreditamento.addFile(estrattoBilancioComplessivo);
			} else {
				datiAccreditamento.removeFileByType(FileEnum.FILE_ESTRATTO_BILANCIO_COMPLESSIVO);
			}
		}
	}

	public void setEstrattoBilancioFormazione(File file){
		estrattoBilancioFormazione = file;
		if(datiAccreditamento != null) {
			if(file != null) {
				datiAccreditamento.addFile(estrattoBilancioFormazione);
			} else {
				datiAccreditamento.removeFileByType(FileEnum.FILE_ESTRATTO_BILANCIO_FORMAZIONE);
			}
		}
	}

	public void setFunzionigramma(File file){
		funzionigramma = file;
		if(datiAccreditamento != null) {
			if(file != null) {
				datiAccreditamento.addFile(funzionigramma);
			} else {
				datiAccreditamento.removeFileByType(FileEnum.FILE_FUNZIONIGRAMMA);
			}
		}
	}

	public void setOrganigramma(File file){
		organigramma = file;
		if(datiAccreditamento != null) {
			if(file != null) {
				datiAccreditamento.addFile(organigramma);
			} else {
				datiAccreditamento.removeFileByType(FileEnum.FILE_ORGANIGRAMMA);
			}
		}
	}

	public Set<File> getFiles(){
		Set<File> files = new HashSet<File>();
		files.add(estrattoBilancioComplessivo);
		files.add(estrattoBilancioFormazione);
		files.add(funzionigramma);
		files.add(organigramma);
		return files;
	}

	public void setFiles(Set<File> files){
		Set<File> fs = new HashSet<File>(files);
		for(File file : fs){
			if(file.isESTRATTOBILANCIOCOMPLESSIVO())
				this.setEstrattoBilancioComplessivo(file);
			else if(file.isESTRATTOBILANCIOFORMAZIONE())
				this.setEstrattoBilancioFormazione(file);
			else if(file.isFUNZIONIGRAMMA())
				this.setFunzionigramma(file);
			else if(file.isORGANIGRAMMA())
				this.setOrganigramma(file);
		}
	}
}
