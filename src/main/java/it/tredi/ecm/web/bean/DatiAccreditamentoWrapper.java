package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.File;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatiAccreditamentoWrapper extends Wrapper{
	private DatiAccreditamento datiAccreditamento;
	
	private Long accreditamentoId;
	
	private File estrattoBilancioFormazione; 
	private File budgetPrevisionale;
	private File funzionigramma;
	private File organigramma;
	
	public void setOffsetAndIds(){
		setIdOffset(39);;
	}
}
