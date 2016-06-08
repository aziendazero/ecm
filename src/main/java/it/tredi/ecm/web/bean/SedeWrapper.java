package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.Sede;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SedeWrapper extends Wrapper{
	private Sede sede;
	private String tipologiaSede; 
	private Long accreditamentoId;
	
	public void setTipologiaSede(String tipologiaSede){
		this.tipologiaSede = tipologiaSede;
	}
}
