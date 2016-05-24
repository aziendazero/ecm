package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.Costanti;
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
		if(tipologiaSede.equals(Costanti.SEDE_LEGALE))
			setIdOffset(8);
		else
			setIdOffset(15);
	}
}
