package it.tredi.ecm.web.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wrapper2 {
	private int idOffset;
	private List<Integer> idEditabili;
	
	public void setOffsetAndIds(List<Integer> idEditabili, List<Integer> accreditamentoIdEditabili){
		setIdOffset(idEditabili.get(0));
		
		idEditabili.retainAll(accreditamentoIdEditabili);//vedo se effettivamente gli id sono modificabili
		setIdEditabili(idEditabili);
	}
}
