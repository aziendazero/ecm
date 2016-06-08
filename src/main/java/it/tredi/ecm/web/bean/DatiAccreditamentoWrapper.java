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
		setFunzionigramma(new File());
		setOrganigramma(new File());
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
		estrattoBilancioFormazione.setProvider(provider);
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
		budgetPrevisionale.setProvider(provider);
		budgetPrevisionale.setDataCreazione(LocalDate.now());
	}
	
	public void setFunzionigramma(File file){
		if(file.getData() != null && file.getData().length > 0){
			//file e' pieno
			if(file.getId() == null){
				//il file passato è un file nuovo
				if(funzionigramma != null){
					//c'era gia' un file...stiamo sovrascrivendo
					file.setId(funzionigramma.getId());
				}
			}
		}
		
		funzionigramma = file;
		funzionigramma.setTipo(Costanti.FILE_FUNZIONIGRAMMA);
		funzionigramma.setProvider(provider);
		funzionigramma.setDataCreazione(LocalDate.now());
	}
	
	public void setOrganigramma(File file){
		if(file.getData() != null && file.getData().length > 0){
			//file e' pieno
			if(file.getId() == null){
				//il file passato è un file nuovo
				if(organigramma != null){
					//c'era gia' un file...stiamo sovrascrivendo
					file.setId(organigramma.getId());
				}
			}
		}
		
		organigramma = file;
		organigramma.setTipo(Costanti.FILE_ORGANIGRAMMA);
		organigramma.setProvider(provider);
		organigramma.setDataCreazione(LocalDate.now());
	}
	
	public Set<File> getFiles(){
		Set<File> files = new HashSet<File>();
		files.add(estrattoBilancioFormazione);
		files.add(budgetPrevisionale);
		files.add(funzionigramma);
		files.add(organigramma);
		return files;
	}
}
