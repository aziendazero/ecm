package it.tredi.ecm.dao.enumlist;

public enum RuoloPersonaEventoEnum {
	DOCENTE(1,"Docente"),
	RELATORE(2,"Relatore"),
	TUTOR(3,"Tutor");
	
	private int id;
	private String nome;

	private RuoloPersonaEventoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
