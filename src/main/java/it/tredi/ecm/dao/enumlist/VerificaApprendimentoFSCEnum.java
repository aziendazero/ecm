package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum VerificaApprendimentoFSCEnum {

	QUESTIONARIO(1,"Questionario"),
	ESAME_ORALE(2,"Esame orale"),
	ESAME_PRATICO(3,"Esame pratico"),
	PROVA_SCRITTA(4,"Prova scritta"),
	RELAZIONE_FIRMATA(5,"Relazione firmata dal responsabile o dal coordinatore del progetto"),
	RAPPORTO_CONCLUSIVO(6, "Rapporto conclusivo di training individualizzato da parte del tutor");

	private int id;
	private String nome;

	private VerificaApprendimentoFSCEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
