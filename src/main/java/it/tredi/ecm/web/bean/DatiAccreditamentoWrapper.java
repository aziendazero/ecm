package it.tredi.ecm.web.bean;

import java.util.HashSet;
import java.util.Set;

import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.FileEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatiAccreditamentoWrapper extends Wrapper{
	private DatiAccreditamento datiAccreditamento;
	private Long accreditamentoId;
	private Provider provider;

	private File estrattoBilancioComplessivo;
	private File estrattoBilancioFormazione;
	private File budgetPrevisionale;
	private File funzionigramma;
	private File organigramma;

	public DatiAccreditamentoWrapper() {
		setEstrattoBilancioComplessivo(new File(FileEnum.FILE_ESTRATTO_BILANCIO_COMPLESSIVO));
		setEstrattoBilancioFormazione(new File(FileEnum.FILE_ESTRATTO_BILANCIO_FORMAZIONE));
		setBudgetPrevisionale(new File(FileEnum.FILE_BUDGET_PREVISIONALE));
		setFunzionigramma(new File(FileEnum.FILE_FUNZIONIGRAMMA));
		setOrganigramma(new File(FileEnum.FILE_ORGANIGRAMMA));
	}

	public void setEstrattoBilancioComplessivo(File file){
		estrattoBilancioComplessivo = file;
		if(provider != null)
			provider.addFile(estrattoBilancioComplessivo);
	}

	public void setEstrattoBilancioFormazione(File file){
		estrattoBilancioFormazione = file;
		if(provider != null)
			provider.addFile(estrattoBilancioFormazione);
	}

	public void setBudgetPrevisionale(File file){
		budgetPrevisionale = file;
		if(provider != null)
			provider.addFile(budgetPrevisionale);
	}

	public void setFunzionigramma(File file){
		funzionigramma = file;
		if(provider != null)
			provider.addFile(funzionigramma);
	}

	public void setOrganigramma(File file){
		organigramma = file;
		if(provider != null)
			provider.addFile(organigramma);
	}

	public Set<File> getFiles(){
		Set<File> files = new HashSet<File>();
		files.add(estrattoBilancioComplessivo);
		files.add(estrattoBilancioFormazione);
		files.add(budgetPrevisionale);
		files.add(funzionigramma);
		files.add(organigramma);
		return files;
	}
}
