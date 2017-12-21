package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum RuoloPersonaEventoEnum {
	DOCENTE(1,"Docente", "D"),
	RELATORE(2,"Relatore", "R"),
	TUTOR(3,"Tutor", "T"),
	MODERATORE(4,"Moderatore", "M");
	
	private int id;
	private String nome;
	private String nomeCorto;

	private RuoloPersonaEventoEnum(int id, String nome, String nomeCorto){
		this.id = id;
		this.nome = nome;
		this.nomeCorto = nomeCorto;
	}
}
