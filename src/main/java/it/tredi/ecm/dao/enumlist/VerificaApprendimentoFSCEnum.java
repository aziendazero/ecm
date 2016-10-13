package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum VerificaApprendimentoFSCEnum {
	
	QUESTIONARIO(1,"Questionario (test)"),
	ESAME_ORALE(2,"Esame orale"),
	ESAME_PRATICO(3,"Esame pratico"),
	PROVA_SCRITTA(4,"Prova scritta (comprende anche il project work, l'elaborato e le domande aperte)"),
	RELAZIONE_FIRMATA(5,"Relazione firmata dal responsabile o dal coordinatore del progetto");
	
	private int id;
	private String nome;

	private VerificaApprendimentoFSCEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
