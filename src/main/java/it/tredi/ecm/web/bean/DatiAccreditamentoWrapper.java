package it.tredi.ecm.web.bean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.Costanti;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatiAccreditamentoWrapper extends Wrapper{
	private DatiAccreditamento datiAccreditamento;
	private Long accreditamentoId;
	private Provider provider;
	
	private File estrattoBilancioFormazione; 
	private File budgetPrevisionale;
	private File funzionigramma;
	private File organigramma;
	
	public DatiAccreditamentoWrapper() {
		setEstrattoBilancioFormazione(new File());
		setBudgetPrevisionale(new File());
	}
	
	public void setOffsetAndIds(){
		setIdOffset(39);
	}
	
	public void setEstrattoBilancioFormazione(File file){
		if(file.getData() != null && file.getData().length > 0){
			//file e' pieno
			if(file.getId() == null){
				//il file passato è un file nuovo
				if(estrattoBilancioFormazione != null){
					//c'era gia' un file...stiamo sovrascrivendo
					file.setId(estrattoBilancioFormazione.getId());
				}
			}
		}
		
		estrattoBilancioFormazione = file;
		estrattoBilancioFormazione.setTipo(Costanti.FILE_ESTRATTO_BILANCIO_FORMAZIONE);
		//estrattoBilancioFormazione.setProvider(/*MANCA PROVIDER*/); //TODO settare provider
		estrattoBilancioFormazione.setDataCreazione(LocalDate.now());
	}
	
	public void setBudgetPrevisionale(File file){
		if(file.getData() != null && file.getData().length > 0){
			//file e' pieno
			if(file.getId() == null){
				//il file passato è un file nuovo
				if(budgetPrevisionale != null){
					//c'era gia' un file...stiamo sovrascrivendo
					file.setId(budgetPrevisionale.getId());
				}
			}
		}
		
		budgetPrevisionale = file;
		budgetPrevisionale.setTipo(Costanti.FILE_BUDGET_PREVISIONALE);
		//budgetPrevisionale.setProvider(/*MANCA PROVIDER*/); //TODO settare provider
		budgetPrevisionale.setDataCreazione(LocalDate.now());
	}
	
	public Set<File> getFiles(){
		Set<File> files = new HashSet<File>();
		files.add(estrattoBilancioFormazione);
		files.add(budgetPrevisionale);
		return files;
	}
}
