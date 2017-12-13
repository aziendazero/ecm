package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum RuoloFSCBaseEnum {
	PARTECIPANTE(1,"P"),
	TUTOR(2,"T"),
	ESPERTO(3,"D"),
	COORDINATORE(4,"D"),
	RESPONSABILE(5,"D"),
	RESPONSABILE_SCIENTIFICO(6,"D"),
	COORDINATORE_X(7,"D");
	
	private int id;
	private String codifica; 

	private RuoloFSCBaseEnum(int id, String codifica){
		this.id = id;
		this.codifica = codifica;
	}
}
