package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum VerificaApprendimentoRESEnum implements INomeEnum {

	QUESTIONARIO(1,"Questionario (test)"),
	ESAME_ORALE(2,"Esame orale"),
	ESAME_PRATICO(3,"Esame pratico"),
	PROVA_SCRITTA(4,"Prova scritta (comprende anche il project work, l'elaborato e le domande aperte)"),
	AUTOCERTFICAZIONE(5,"Autocertificazione del partecipante");

	private int id;
	private String nome;

	private VerificaApprendimentoRESEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
