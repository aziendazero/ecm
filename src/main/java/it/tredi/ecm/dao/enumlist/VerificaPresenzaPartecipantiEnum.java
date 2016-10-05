package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum VerificaPresenzaPartecipantiEnum {
	
	FIRMA_PRESENZA(1,"Firma di presenza"),
	RILEVAZIONE_ELETTRONICA(2,"Rilevazione elettronica di presenza");
	
	private int id;
	private String nome;

	private VerificaPresenzaPartecipantiEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
