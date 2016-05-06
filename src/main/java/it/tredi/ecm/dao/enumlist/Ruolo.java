package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum Ruolo {
	RICHIEDENTE (1, "Richiedente"),
	LEGALE_RAPPRESENTANTE (2, "Legale Rappresentante");
		
	private int id;
	private String nome;
	
	private Ruolo(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
