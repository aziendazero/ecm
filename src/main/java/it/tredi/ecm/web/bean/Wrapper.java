package it.tredi.ecm.web.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wrapper {
	private int idOffset;
	private List<Integer> idEditabili;
	
	public void setOffsetAndIds(int idOffset, List<Integer> idEditabili, List<Integer> accreditamentoIdEditabili){
		setIdOffset(idOffset);
		
		idEditabili.retainAll(accreditamentoIdEditabili);//vedo se effettivamente gli id sono modificabili
		setIdEditabili(idEditabili);
	}
}
