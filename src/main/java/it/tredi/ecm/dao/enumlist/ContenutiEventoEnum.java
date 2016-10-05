package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum ContenutiEventoEnum {
	ALIMENTAZIONE_PRIMA_INFANZIA(1,"Alimentazione della prima infanzia"),
	MEDICINE_NON_CONVENZIONALE(2,"Medicine non convenzionali"),
	ALTRO(3,"Altro");
	
	private int id;
	private String nome;

	private ContenutiEventoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
