package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipologiaEventoFADEnum {
	APPRENDIMENTO_INDIVIDUALE_NO_ONLINE(1,""),
	APPRENDIMENTO_INDIVIDUALE_SI_ONLINE(2,""),
	EVENTI_SEMINARIALI_IN_RETE(3,""),
	APPRENDIMENTO_CONTESTO_SOCIALE(4,"");
	
	private int id;
	private String nome;

	private TipologiaEventoFADEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}	
}
